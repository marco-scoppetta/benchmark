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
import java.util.Objects;

/**
 * Contains the configuration for an execution of the benchmarking system
 */

public class BenchmarkConfiguration {

    private static final String DEFAULT_GRAKN_URI = "localhost:48555";
    private QueriesConfigurationFile queries;
    private List<String> schemaGraql;
    private boolean schemaLoad = true;
    private boolean dataGeneration = true;
    private BenchmarkConfigurationFile benchmarkConfigFile;
    private String keyspace;

    public String uri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    private String uri;

    public BenchmarkConfiguration(CommandLine arguments) {
        Path workingDirectory = Paths.get((System.getProperty("working.dir"));
        String configFileName = arguments.getOptionValue("config");
        Path configFilePath = Paths.get(configFileName);
        if(!configFilePath.isAbsolute()) configFilePath = workingDirectory.resolve(configFilePath);
        try {
            // parse config yaml file into object
            ObjectMapper benchmarkConfigMapper = new ObjectMapper(new YAMLFactory());
            this.benchmarkConfigFile = benchmarkConfigMapper.readValue(
                    configFilePath.toFile(),
                    BenchmarkConfigurationFile.class);

            // read the queries file string and use them to load further YAML
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Path queryFilePath = configFilePath.getParent().resolve(benchmarkConfigFile.getRelativeQueriesYamlFile());

            queries = mapper.readValue(queryFilePath.toFile(), QueriesConfigurationFile.class);
            Path schemaFilePath = configFilePath.getParent().resolve(benchmarkConfigFile.getRelativeSchemaFile());
            schemaGraql = Files.readAllLines(schemaFilePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // use given keyspace string if exists, otherwise use yaml file `name` tag
        String keyspace = arguments.hasOption("keyspace") ? arguments.getOptionValue("keyspace") : this.getConfigName();
        this.setKeyspace(keyspace);

        // loading a schema file, enabled by default
        boolean noSchemaLoad = arguments.hasOption("no-schema-load");
        this.setSchemaLoad(!noSchemaLoad);

        // generate data true/false, else default to do generate data
        boolean noDataGeneration = arguments.hasOption("no-data-generation");
        this.setDataGeneration(!noDataGeneration);

        String uri = (arguments.hasOption("uri")) ? arguments.getOptionValue("uri") : DEFAULT_GRAKN_URI;
        this.setUri(uri);
    }

    public String getConfigName() {
        return this.benchmarkConfigFile.getName();
    }


    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public Keyspace getKeyspace() {
        return Keyspace.of(this.keyspace);
    }

    public List<String> getSchemaGraql() {
        if (this.schemaLoad) {
            return null;
        } else {
            return this.schemaGraql;
        }
    }

    public List<String> getQueries() {
        return this.queries.getQueries();
    }

    public List<Integer> getConceptsToBenchmark() {
        if (this.dataGeneration) { return this.benchmarkConfigFile.getConceptsToBenchmark(); }
        return null;
    }

    public void setSchemaLoad(boolean loadSchema) {
        this.schemaLoad = loadSchema;
    }

    public boolean schemaLoad() {
        // we also don't load the schema
        // if the data generation is disabled
        return this.dataGeneration && this.schemaLoad;
    }

    public void setDataGeneration(boolean generateData) {
        this.dataGeneration = generateData;
    }


    public int numQueryRepetitions() {
        return this.benchmarkConfigFile.getRepeatsPerQuery();
    }

}
