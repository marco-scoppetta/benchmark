package grakn.benchmark.runner;

import org.junit.Test;

/**
 *
 */
public class BenchmarkStartupE2E {

    @Test
    public void whenProvidingAbsolutePathToConfig_benchmarkShouldStart(){
        GraknBenchmark.main(new String[]{"--config", "eeee"});
    }
}