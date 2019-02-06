package grakn.benchmark.profiler.generator.schemaspecific;


import grakn.benchmark.profiler.generator.strategy.RouletteWheel;
import grakn.benchmark.profiler.generator.strategy.TypeStrategy;


/**
 * This describes the way Benchmark will generate data for a specific schema
 */

public abstract class DataGeneratorDefinition {

    protected abstract RouletteWheel<RouletteWheel<TypeStrategy>> getDefinition();

    public TypeStrategy sampleNextStrategy(){
        return getDefinition().next().next();
    }

}
