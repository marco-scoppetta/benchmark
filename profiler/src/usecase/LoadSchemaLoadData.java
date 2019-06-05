package grakn.benchmark.profiler.usecase;

import grakn.benchmark.common.configuration.BenchmarkConfiguration;
import grakn.benchmark.profiler.ThreadedProfiler;
import grakn.benchmark.profiler.util.SchemaManager;
import grakn.client.GraknClient;

import java.util.Collections;
import java.util.List;

import static grakn.benchmark.profiler.util.ConcurrentDataLoader.concurrentDataImport;

public class LoadSchemaLoadData implements UseCase {

    private final BenchmarkConfiguration config;
    private final GraknClient client;
    private final SchemaManager schemaManager;

    LoadSchemaLoadData(BenchmarkConfiguration config, GraknClient client, SchemaManager schemaManager) {
        this.config = config;
        this.client = client;
        this.schemaManager = schemaManager;
    }

    @Override
    public void run() {
        schemaManager.loadSchema();
        List<String> keyspaces = Collections.singletonList(config.getKeyspace());

        int numConcepts = concurrentDataImport(client, keyspaces, config.staticDataImportQueries(), 8);

        ThreadedProfiler threadedProfiler = new ThreadedProfiler(client, keyspaces, config);
        threadedProfiler.processQueries(config.numQueryRepetitions(), numConcepts);
        threadedProfiler.cleanup();
    }
}
