package grakn.benchmark.profiler.generator.schemaspecific;


import grakn.benchmark.profiler.generator.strategy.RouletteWheel;
import grakn.benchmark.profiler.generator.strategy.TypeStrategy;

public interface SchemaSpecificDefinition {
    RouletteWheel<RouletteWheel<TypeStrategy>> getDefinition();
}
