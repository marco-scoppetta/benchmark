package grakn.benchmark.runner.usecase;

import grakn.benchmark.runner.util.BenchmarkConfiguration;
import grakn.benchmark.runner.executor.QueryExecutor;
import grakn.benchmark.runner.generator.DataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GenerateAndBenchmark {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateAndBenchmark.class);

    private final QueryExecutor queryExecutor;
    private final DataGenerator dataGenerator;
    private final BenchmarkConfiguration config;

    public GenerateAndBenchmark(QueryExecutor queryExecutor, DataGenerator dataGenerator, BenchmarkConfiguration config) {
        this.queryExecutor = queryExecutor;
        this.dataGenerator = dataGenerator;
        this.config = config;
    }

    /**
     * Given a list of database sizes to perform profiling at,
     * Populate the DB to a given size, then run the benchmark
     */
    public void start() {
        if (config.schemaLoad()) {
            dataGenerator.loadSchema();
        }
        List<Integer> numConceptsInRun = config.getConceptsToBenchmark();
        for (int numConcepts : numConceptsInRun) {
            LOG.info("Running queries with " + Integer.toString(numConcepts) + " concepts");
            dataGenerator.generate(numConcepts);
            queryExecutor.processStaticQueries(config.numQueryRepetitions(), numConcepts);
        }
    }
}
