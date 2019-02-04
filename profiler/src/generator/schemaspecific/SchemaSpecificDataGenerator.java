package grakn.benchmark.profiler.generator.schemaspecific;

import grakn.benchmark.profiler.generator.storage.ConceptStore;
import grakn.benchmark.profiler.generator.strategy.RouletteWheel;
import grakn.benchmark.profiler.generator.strategy.TypeStrategyInterface;


public interface SchemaSpecificDataGenerator {
    RouletteWheel<RouletteWheel<TypeStrategyInterface>> getStrategy();

    ConceptStore getConceptStore();
    default int getGraphScale() {
        ConceptStore storage = getConceptStore();
        int entities = storage.totalEntities();
        int attributes = storage.totalAttributes();
        int relationships = storage.totalExplicitRelationships();
        return entities + attributes + relationships;
    }
}
