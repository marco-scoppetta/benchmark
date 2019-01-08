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

import grakn.benchmark.runner.executionconfig.BenchmarkConfiguration;
import grakn.benchmark.runner.executor.QueryExecutor;
import grakn.benchmark.runner.generator.DataGenerator;
import grakn.benchmark.runner.sharedconfig.Configs;
import grakn.benchmark.runner.usecase.BenchmarkExistingKeyspace;
import grakn.benchmark.runner.usecase.GenerateAndBenchmark;
import grakn.core.Keyspace;
import grakn.core.client.Grakn;
import grakn.core.util.SimpleURI;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Class in charge of
 * - initialising Benchmark dependencies and BenchmarkConfiguration
 * - run data generation (populate empty keyspace) (DataGenerator)
 * - run benchmark on queries (QueryExecutor)
 */
public class GraknBenchmark {
    private static final Logger LOG = LoggerFactory.getLogger(GraknBenchmark.class);
    private static final String ES_SERVER_HOST = "localhost";
    private static final int ES_SERVER_PORT = 9200;
    private static final String ES_SERVER_PROTOCOL = "http";
    private static final String USECASE_GENERATE = "generate";
    private static final String USECASE_EXISTING = "existing";

    private final BenchmarkConfiguration configuration;
    private final int repetitionsPerQuery;
    private final String uri;
    private final String executionName;
    private final Keyspace currentKeyspace;
    private final String usecase;

    /**
     * Entry point invoked by runner.py
     * Initialise dependencies and configurations.
     */
    public static void main(String[] args) {
        try {
            // Initialise Ignite and ElasticSearch
            Ignite ignite = initIgniteServer();
            initElasticSearch();
            GraknBenchmark benchmark = new GraknBenchmark(args);
            benchmark.start();
            ignite.close();
        } catch (Exception e) {
            LOG.error("Unable to start Grakn Benchmark", e);
        }
    }

    public GraknBenchmark(String[] args) {

        // Parse arguments from console
        Options options = BenchmarkOptions.build();
        CommandLineParser parser = new DefaultParser();
        CommandLine arguments = null;
        try {
            arguments = parser.parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException("Exception while parsing arguments", e);
        }

        this.usecase = args[0];

        // Build benchmark configuration
        this.configuration = new BenchmarkConfiguration(arguments);
        this.repetitionsPerQuery = configuration.numQueryRepetitions();
        this.uri = (arguments.hasOption("uri")) ? arguments.getOptionValue("uri") : Configs.DEFAULT_GRAKN_URI;

        // generate a name for this specific execution of the benchmarking
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = dateFormat.format(new Date());
        this.executionName = String.join(" ", Arrays.asList(dateString, configuration.getConfigName(), arguments.getOptionValue("execution-name", ""))).trim();

        //Open new session to currentKeyspace
        this.currentKeyspace = configuration.getKeyspace();
    }

    public void start() {

        switch (usecase) {
            case USECASE_EXISTING:
                BenchmarkExistingKeyspace existingUseCase = new BenchmarkExistingKeyspace();
                existingUseCase.start();
                break;
            case USECASE_GENERATE:
                GenerateAndBenchmark generateUseCase = new GenerateAndBenchmark();
                generateUseCase.start();
                break;
            default:
                throw new RuntimeException("Use case " + usecase + " not recognised.");
        }

        Grakn client = new Grakn(new SimpleURI(uri));
        // TODO fix sometime
        // workaround to make deletions work...
        if (configuration.schemaLoad()) {
            System.out.println("Deleting keyspace `" + currentKeyspace + "`");
            client.keyspaces().delete(currentKeyspace);
        }

        Grakn.Session session = client.session(currentKeyspace);

        QueryExecutor queryExecutor = new QueryExecutor(currentKeyspace, uri, executionName, configuration.getQueries());
        int repetitionsPerQuery = configuration.numQueryRepetitions();


        // No data generation means NEITHER schema load NOR data generate
        if (configuration.dataGeneration()) {
            int randomSeed = 0;
            generateData(session, configuration, randomSeed, queryExecutor);
        } else {
            int numConcepts = queryExecutor.aggregateCount();
            // only 1 point to profile at
            queryExecutor.processStaticQueries(repetitionsPerQuery, numConcepts, "Preconfigured DB - no data gen");
        }

    }

    private static void generateData(Grakn.Session session, BenchmarkConfiguration config, int randomSeed, QueryExecutor queryExecutor) {
        DataGenerator dataGenerator = new DataGenerator(session, config.getConfigName(), config.getSchemaGraql(), randomSeed);
        // load schema if not disabled
        if (config.schemaLoad()) {
            dataGenerator.loadSchema();
        }
        List<Integer> numConceptsInRun = config.getConceptsToBenchmark();
        runAtConcepts(numConceptsInRun, dataGenerator, queryExecutor);
    }


    /**
     * Given a list of database sizes to perform profiling at,
     * Populate the DB to a given size, then run the benchmark
     *
     * @param numConceptsInRun
     */
    private static void runAtConcepts(List<Integer> numConceptsInRun, DataGenerator dataGenerator, QueryExecutor queryExecutor) {
        for (int numConcepts : numConceptsInRun) {
            LOG.info("Running queries with " + Integer.toString(numConcepts) + " concepts");
            dataGenerator.generate(numConcepts);
            queryExecutor.processStaticQueries(repetitionsPerQuery, numConcepts);
        }
    }


    private static boolean indexTemplateExists(RestClient esClient, String indexTemplateName) throws IOException {
        try {
            Request templateExistsRequest = new Request(
                    "GET",
                    "/_template/" + indexTemplateName
            );
            Response response = esClient.performRequest(templateExistsRequest);
            LOG.info("Index template `" + indexTemplateName + "` already exists");
            return true;
        } catch (ResponseException err) {
            // 404 => template does not exist yet
            LOG.error("Index template `" + indexTemplateName + "` does not exist", err);
            return false;
        }
    }

    private static void putIndexTemplate(RestClient esClient, String indexTemplateName, String indexTemplate) throws IOException {
        Request putTemplateRequest = new Request(
                "PUT",
                "/_template/" + indexTemplateName
        );
        HttpEntity entity = new StringEntity(indexTemplate, ContentType.APPLICATION_JSON);
        putTemplateRequest.setEntity(entity);
        esClient.performRequest(putTemplateRequest);
        LOG.info("Created index template `" + indexTemplateName + "`");
    }

    private static void initElasticSearch() throws IOException {
        RestClientBuilder esRestClientBuilder = RestClient.builder(new HttpHost(ES_SERVER_HOST, ES_SERVER_PORT, ES_SERVER_PROTOCOL));
        esRestClientBuilder.setDefaultHeaders(new Header[]{new BasicHeader("header", "value")});
        RestClient restClient = esRestClientBuilder.build();

        String indexTemplateName = Configs.ElasticSearchConfig.INDEX_TEMPLATE_NAME;
        if (!indexTemplateExists(restClient, indexTemplateName)) {
            String indexTemplate = Configs.ElasticSearchConfig.INDEX_TEMPLATE;
            putIndexTemplate(restClient, indexTemplateName, indexTemplate);
        }
        restClient.close();
    }

    private static Ignite initIgniteServer() throws IgniteException {
        return Ignition.start();
    }

    private static class BenchmarkOptions {

        static Options build() {

            Option configFileOption = Option.builder("c")
                    .longOpt("config")
                    .hasArg(true)
                    .desc("Benchmarking YAML file (required)")
                    .required(true)
                    .type(String.class)
                    .build();

            Option graknAddressOption = Option.builder("u")
                    .longOpt("uri")
                    .hasArg(true)
                    .desc("Address of the grakn cluster (default: localhost:48555)")
                    .required(false)
                    .type(String.class)
                    .build();

            Option keyspaceOption = Option.builder("k")
                    .longOpt("keyspace")
                    .required(false)
                    .hasArg(true)
                    .desc("Specific keyspace to utilize (default: `name` in config yaml")
                    .type(String.class)
                    .build();
            Option noDataGenerationOption = Option.builder("ng")
                    .longOpt("no-data-generation")
                    .required(false)
                    .desc("Disable data generation")
                    .type(Boolean.class)
                    .build();
            Option noSchemaLoadOption = Option.builder("ns")
                    .longOpt("no-schema-load")
                    .required(false)
                    .desc("Disable loading a schema")
                    .type(Boolean.class)
                    .build();
            Option executionNameOption = Option.builder("n")
                    .longOpt("execution-name")
                    .hasArg(true)
                    .required(false)
                    .desc("Name for specific execution of the config file")
                    .type(String.class)
                    .build();
            Options options = new Options();
            options.addOption(configFileOption);
            options.addOption(graknAddressOption);
            options.addOption(keyspaceOption);
            options.addOption(noDataGenerationOption);
            options.addOption(noSchemaLoadOption);
            options.addOption(executionNameOption);
            return options;
        }
    }
}
