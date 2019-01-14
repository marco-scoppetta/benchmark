package grakn.benchmark.runner;

import grakn.benchmark.runner.exception.BootupException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Path;
import java.nio.file.Paths;


/**
 *
 */
public class GraknBenchmarkTest {

    private final static Path WEB_CONTENT_CONFIG_PATH = Paths.get("runner/test/resources/web_content/web_content_config_test.yml");

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void whenProvidingAbsolutePathToExistingConfig_benchmarkShouldStart() {
        GraknBenchmark graknBenchmark = new GraknBenchmark(new String[]{"--config", WEB_CONTENT_CONFIG_PATH.toAbsolutePath().toString()});
    }

    @Test
    public void whenProvidingRelativePathToExistingConfig_benchmarkShouldStart() {
        System.setProperty("working.dir", WEB_CONTENT_CONFIG_PATH.getParent().toString());
        GraknBenchmark graknBenchmark = new GraknBenchmark(new String[]{"--config", "web_content_config_test.yml"});
    }

    @Test
    public void whenProvidingAbsolutePathToNonExistingConfig_throwException() {
        expectedException.expect(BootupException.class);
        expectedException.expectMessage("The provided config file [nonexistingpath] does not exist");
        GraknBenchmark graknBenchmark = new GraknBenchmark(new String[]{"--config", "nonexistingpath"});
    }

    @Test
    public void whenConfigurationArgumentNotProvided_throwException() {
        expectedException.expect(BootupException.class);
        expectedException.expectMessage("Missing required option: c");
        GraknBenchmark graknBenchmark = new GraknBenchmark(new String[]{});
    }
}