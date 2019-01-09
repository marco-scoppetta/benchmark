package grakn.benchmark.runner.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Helper class used to parse console arguments into predefined options
 */
public class BenchmarkArguments {

    public static CommandLine parse(String[] args) {
        Options options = buildOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine arguments = parser.parse(options, args);
            return arguments;
        } catch (ParseException e) {
            throw new RuntimeException("Exception while parsing arguments", e);
        }
    }

    private static Options buildOptions() {
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
