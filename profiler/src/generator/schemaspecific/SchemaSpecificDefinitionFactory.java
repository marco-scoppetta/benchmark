package grakn.benchmark.profiler.generator.schemaspecific;

import grakn.benchmark.profiler.generator.storage.ConceptStore;

import java.util.Random;

public class SchemaSpecificDefinitionFactory {

    public static SchemaSpecificDefinition getDefinition(String name, Random random, ConceptStore storage) {
        switch (name) {
            case "social_network":
                return new SocialNetworkGenerator(random, storage);
            case "road_network":
                return new RoadNetworkGenerator(random, storage);
            case "biochem_network":
                return new BiochemNetworkGenerator(random, storage);
            case "financial":
                return new FinancialTransactionsGenerator(random, storage);
            default:
                throw new RuntimeException("Unknown specific schema generation strategy name: " + name);
        }
    }
}
