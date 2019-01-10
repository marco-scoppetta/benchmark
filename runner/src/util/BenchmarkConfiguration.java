/*
 *  GRAKN.AI - THE KNOWLEDGE GRAPH
 *  Copyright (C) 2018 Grakn Labs Ltd
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package grakn.benchmark.runner.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import grakn.core.Keyspace;
import org.apache.commons.cli.CommandLine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Contains the configuration for an execution of the benchmarking system
 */

public class BenchmarkConfiguration {

    private static final String DEFAULT_GRAKN_URI = "localhost:48555";
    private List<String> queries;
    private List<String> graqlSchema;
    private boolean schemaLoad;
    private BenchmarkConfigurationFile benchmarkConfigFile;
    private String keyspace;
    private String uri;

    public BenchmarkConfiguration(CommandLine arguments) {
        Path configFilePath = getConfigFilePath(arguments);

        this.benchmarkConfigFile = parseConfigurationFile(configFilePath);

        this.queries = parseQueriesFile(configFilePath).getQueries();

        this.graqlSchema = parseGraqlSchema(configFilePath);


        // use given keyspace string if exists, otherwise use yaml file `name` tag
        this.keyspace = arguments.hasOption("keyspace") ? arguments.getOptionValue("keyspace") : this.getConfigName();

        this.uri = (arguments.hasOption("uri")) ? arguments.getOptionValue("uri") : DEFAULT_GRAKN_URI;

        // loading a schema file, enabled by default
        boolean noSchemaLoad = arguments.hasOption("no-schema-load");
        this.schemaLoad = !noSchemaLoad;
    }

    public String getConfigName() {
        return this.benchmarkConfigFile.getName();
    }

    public String uri() {
        return uri;
    }

    public Keyspace getKeyspace() {
        return Keyspace.of(this.keyspace);
    }

    public List<String> getGraqlSchema() {
        return this.graqlSchema;
    }

    public List<String> getQueries() {
        return this.queries;
    }

    public List<Integer> getConceptsToBenchmark() {
        return this.benchmarkConfigFile.getConceptsToBenchmark();
    }

    public boolean schemaLoad() {
        return this.schemaLoad;
    }

    public int numQueryRepetitions() {
        return this.benchmarkConfigFile.getRepeatsPerQuery();
    }


    /**
     * Compute configuration file path, prepending path to working dir if relative path provided.
     *
     * @param arguments command line arguments
     * @return absolute path to configuration file
     */
    private Path getConfigFilePath(CommandLine arguments) {
        Path workingDirectory = Paths.get(System.getProperty("working.dir"));
        String configFileName = arguments.getOptionValue("config");
        Path configFilePath = Paths.get(configFileName);
        if (!configFilePath.isAbsolute()) configFilePath = workingDirectory.resolve(configFilePath);
        return configFilePath;
    }

    /**
     * Parse configuration file to object
     * @param configFilePath absolute path to configuration file
     * @return Object representing yaml file
     */
    private BenchmarkConfigurationFile parseConfigurationFile(Path configFilePath) {
        ObjectMapper benchmarkConfigMapper = new ObjectMapper(new YAMLFactory());
        try {
            return benchmarkConfigMapper.readValue(configFilePath.toFile(), BenchmarkConfigurationFile.class);
        } catch (IOException e) {
            throw new RuntimeException("Exception parsing Benchmark configuration file", e);
        }
    }

    /**
     * Parse queries file to object
     * @param configFilePath absolute path to configuration file
     * @return Object that holds reference to array of queries
     */
    private QueriesConfigurationFile parseQueriesFile(Path configFilePath){
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Path queryFilePath = configFilePath.getParent().resolve(benchmarkConfigFile.getQueriesFilePath());
        try {
            return mapper.readValue(queryFilePath.toFile(), QueriesConfigurationFile.class);
        } catch (IOException e) {
            throw new RuntimeException("Exception parsing queries file", e);
        }
    }

    /**
     * Parse Graql schema file into a list of Strings
     * @param configFilePath absolute path to configuration file
     * @return List of string representing Graql schema declaration statements
     */
    private List<String> parseGraqlSchema(Path configFilePath){
        Path schemaFilePath = configFilePath.getParent().resolve(benchmarkConfigFile.getRelativeSchemaFile());
        try {
            return Files.readAllLines(schemaFilePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Exception parsing Graql schema file", e);
        }
    }
}
