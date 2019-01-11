/*
 *  GRAKN.AI - THE KNOWLEDGE GRAPH
 *  Copyright (C) 2018 Grakn Labs Ltd
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package grakn.benchmark.runner;

import grakn.benchmark.runner.util.BenchmarkConfiguration;
import grakn.benchmark.runner.executor.QueryExecutor;
import grakn.benchmark.runner.generator.DataGenerator;
import grakn.benchmark.runner.usecase.BenchmarkExistingKeyspace;
import grakn.benchmark.runner.usecase.GenerateAndBenchmark;
import grakn.benchmark.runner.util.BenchmarkArguments;
import grakn.benchmark.runner.util.ElasticSearchManager;
import grakn.core.client.Grakn;
import grakn.core.util.SimpleURI;
import org.apache.commons.cli.CommandLine;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Class in charge of
 * - initialising Benchmark dependencies and BenchmarkConfiguration
 * - run data generation (populate empty keyspace) (DataGenerator)
 * - run benchmark on queries (QueryExecutor)
 */
public class GraknBenchmark {
    private static final Logger LOG = LoggerFactory.getLogger(GraknBenchmark.class);

    private final BenchmarkConfiguration configuration;
    private final String executionName;

    /**
     * Entry point invoked by benchmark.sh script
     */
    public static void main(String[] args) {
        try {
            // Initialise Ignite and ElasticSearch
            Ignite ignite = Ignition.start();
            ElasticSearchManager.init();

            GraknBenchmark benchmark = new GraknBenchmark(args);
            benchmark.start();

            ignite.close();
        } catch (Exception e) {
            LOG.error("Unable to start Grakn Benchmark", e);
        }
    }

    public GraknBenchmark(String[] args) {

        // Parse arguments from console
        CommandLine arguments = BenchmarkArguments.parse(args);

        // Build benchmark configuration
        this.configuration = new BenchmarkConfiguration(arguments);

        // generate a name for this specific execution of the benchmarking
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = dateFormat.format(new Date());
        this.executionName = String.join(" ", Arrays.asList(dateString, configuration.getConfigName(), arguments.getOptionValue("execution-name", ""))).trim();
    }

    public void start() {
        Grakn client = new Grakn(new SimpleURI(configuration.uri()));
        Grakn.Session session = client.session(configuration.getKeyspace());

        QueryExecutor queryExecutor = new QueryExecutor(session, executionName, configuration.getQueries());

        //TODO add check to make sure currentKeyspace does not exist, if it does throw exception
        // this can be done once we implement keyspaces().retrieve() on the client Java (issue #4675)

        if (configuration.generateData()) {
            int randomSeed = 0;
            DataGenerator dataGenerator = new DataGenerator(session, configuration.getConfigName(), configuration.getGraqlSchema(), randomSeed);
            GenerateAndBenchmark generateUseCase = new GenerateAndBenchmark(queryExecutor, dataGenerator, configuration);
            generateUseCase.start();
        } else {
            BenchmarkExistingKeyspace existingUseCase = new BenchmarkExistingKeyspace(queryExecutor, configuration.numQueryRepetitions());
            existingUseCase.start();
        }

        session.close();
    }
}
