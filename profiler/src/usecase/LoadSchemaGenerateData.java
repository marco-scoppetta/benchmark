package grakn.benchmark.profiler.usecase;

import grakn.benchmark.common.configuration.BenchmarkConfiguration;
import grakn.benchmark.common.timer.BenchmarkingTimer;
import grakn.benchmark.generator.DataGenerator;
import grakn.benchmark.generator.definition.DataGeneratorDefinition;
import grakn.benchmark.generator.definition.DefinitionFactory;
import grakn.benchmark.generator.query.QueryProvider;
import grakn.benchmark.generator.storage.ConceptStorage;
import grakn.benchmark.generator.storage.IgniteConceptStorage;
import grakn.benchmark.generator.util.IgniteManager;
import grakn.benchmark.profiler.ThreadedProfiler;
import grakn.benchmark.profiler.util.SchemaManager;
import grakn.client.GraknClient;
import grakn.core.concept.type.AttributeType;
import org.apache.ignite.Ignite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class LoadSchemaGenerateData implements UseCase {
    private static final Logger LOG = LoggerFactory.getLogger(LoadSchemaGenerateData.class);

    private final BenchmarkConfiguration config;
    private final GraknClient client;
    private final SchemaManager schemaManager;

    LoadSchemaGenerateData(BenchmarkConfiguration config, GraknClient client, SchemaManager schemaManager) {
        this.config = config;
        this.client = client;
        this.schemaManager = schemaManager;
    }

    @Override
    public void run() {
        schemaManager.loadSchema();

        ThreadedProfiler threadedProfiler = new ThreadedProfiler(client, Collections.singletonList(config.getKeyspace()), config);
        Ignite ignite = IgniteManager.initIgnite();

        BenchmarkingTimer timer = new BenchmarkingTimer();
        DataGenerator dataGenerator = initDataGenerator(client, config.getKeyspace(), timer);
        List<Integer> numConceptsInRun = config.scalesToProfile();

        try {
            timer.startGenerateAndTrack();
            for (int numConcepts : numConceptsInRun) {
                LOG.info("\n Generating graph to scale... " + numConcepts);
                dataGenerator.generate(numConcepts);
                timer.startQueryTimeTracking();
                threadedProfiler.processQueries(config.numQueryRepetitions(), numConcepts);
                timer.endQueryTimeTracking();
                timer.printTimings();
            }
        } finally {
            threadedProfiler.cleanup();
            ignite.close();
        }
    }

    /**
     * Connect a data generator to pre-prepared keyspace
     */
    private DataGenerator initDataGenerator(GraknClient client, String keyspace, BenchmarkingTimer timer) {
        int randomSeed = 0;
        String dataGenerator= config.dataGenerator();
        GraknClient.Session session = client.session(keyspace);
        GraknClient.Transaction tx = session.transaction().read();
        Set<String> entityTypeLabels = getEntityTypes(tx);
        Set<String> relationshipTypeLabels = getRelationTypes(tx);
        Map<String, AttributeType.DataType<?>> attributeTypeLabels = getAttributeTypes(tx);
        tx.close();
        session.close();
        ConceptStorage storage = new IgniteConceptStorage(entityTypeLabels, relationshipTypeLabels, attributeTypeLabels);

        DataGeneratorDefinition dataGeneratorDefinition = DefinitionFactory.getDefinition(dataGenerator, new Random(randomSeed), storage);

        QueryProvider queryProvider = new QueryProvider(dataGeneratorDefinition);

        return new DataGenerator(client, keyspace, storage, dataGenerator, queryProvider, timer);
    }

    private Set<String> getEntityTypes(GraknClient.Transaction tx) {
        return tx.getEntityType("entity")
                .subs()
                .map(type -> type.label().getValue())
                .collect(Collectors.toSet());
    }

    private Set<String> getRelationTypes(GraknClient.Transaction tx) {
        return tx.getRelationType("relation")
                .subs()
                .map(type -> type.label().getValue())
                .collect(Collectors.toSet());
    }

    private HashMap<String, AttributeType.DataType<?>> getAttributeTypes(GraknClient.Transaction tx) {
        HashMap<String, AttributeType.DataType<?>> typeLabels = new HashMap<>();
        HashSet<AttributeType<Object>> types = tx.getAttributeType("attribute").subs().collect(Collectors.toCollection(HashSet::new));
        for (AttributeType conceptType : types) {
            String label = conceptType.label().toString();
            AttributeType.DataType<?> datatype = conceptType.dataType();
            typeLabels.put(label, datatype);
        }
        return typeLabels;
    }
}
