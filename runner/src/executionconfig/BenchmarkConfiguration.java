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

package grakn.benchmark.runner.executionconfig;


import grakn.core.Keyspace;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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

    private QueriesConfigurationFile queries;
    private List<String> schemaGraql;
    private boolean schemaLoad = true;
    private boolean dataGeneration = true;
    private BenchmarkConfigurationFile benchmarkConfigFile;
    private Path configFilePath;
    private String keyspace;

    public BenchmarkConfiguration(CommandLine arguments) {

        String configFileName = arguments.getOptionValue("config");
        Path configFilePath = Paths.get(configFileName);
        try {
            // parse config yaml file into object
            ObjectMapper benchmarkConfigMapper = new ObjectMapper(new YAMLFactory());
            BenchmarkConfigurationFile config = benchmarkConfigMapper.readValue(
                    configFilePath.toFile(),
                    BenchmarkConfigurationFile.class);

            this.configFilePath = configFilePath;
            this.benchmarkConfigFile = config;


            // read the queries file string and use them to load further YAML
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Path queryFilePath = this.configFilePath.getParent().resolve(config.getRelativeQueriesYamlFile());

            queries = mapper.readValue(queryFilePath.toFile(), QueriesConfigurationFile.class);
            Path schemaFilePath = this.configFilePath.getParent().resolve(config.getRelativeSchemaFile());
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
    }

    public String getConfigName() {
        return this.benchmarkConfigFile.getName();
    }


    public void setKeyspace(String keyspace) {
        String name = keyspace;
        // remove spaces
        name = name.replace(' ', '_');
        if (name.length() > 48) {
            name = name.substring(0, 48);
        }
        this.keyspace = name;
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
        return this.dataGeneration || this.schemaLoad;
    }

    public void setDataGeneration(boolean generateData) {
        this.dataGeneration = generateData;
    }

    public boolean dataGeneration() {
        return this.dataGeneration;
    }

    public int numQueryRepetitions() {
        return this.benchmarkConfigFile.getRepeatsPerQuery();
    }

}
