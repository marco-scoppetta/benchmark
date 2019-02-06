package grakn.benchmark.profiler.generator;

import grakn.benchmark.profiler.generator.concept.AttributeGenerator;
import grakn.benchmark.profiler.generator.concept.EntityGenerator;
import grakn.benchmark.profiler.generator.concept.Generator;
import grakn.benchmark.profiler.generator.concept.RelationshipGenerator;
import grakn.benchmark.profiler.generator.schemaspecific.DataGeneratorDefinition;
import grakn.benchmark.profiler.generator.strategy.AttributeStrategy;
import grakn.benchmark.profiler.generator.strategy.EntityStrategy;
import grakn.benchmark.profiler.generator.strategy.RelationshipStrategy;
import grakn.benchmark.profiler.generator.strategy.TypeStrategy;
import grakn.core.graql.InsertQuery;

import java.util.stream.Stream;

public class QueryGenerator {
    private final DataGeneratorDefinition dataGeneratorDefinition;

    public QueryGenerator(DataGeneratorDefinition dataGeneratorDefinition) {
        this.dataGeneratorDefinition = dataGeneratorDefinition;

    }

    public Stream<InsertQuery> nextQueryBatch(){
        Generator<? extends TypeStrategy> generator;
        TypeStrategy typeStrategy = dataGeneratorDefinition.sampleNextStrategy();


        if (typeStrategy instanceof EntityStrategy) {
            generator = new EntityGenerator((EntityStrategy) typeStrategy);
        } else if (typeStrategy instanceof RelationshipStrategy) {
            generator = new RelationshipGenerator((RelationshipStrategy) typeStrategy);
        } else if (typeStrategy instanceof AttributeStrategy) {
            generator = new AttributeGenerator((AttributeStrategy) typeStrategy);
        } else {
            throw new RuntimeException("Couldn't find a matching Generator for this strategy");
        }
        return generator.generate();
    }

}
