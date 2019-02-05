package grakn.benchmark.profiler.generator;

import grakn.benchmark.profiler.generator.concept.AttributeGenerator;
import grakn.benchmark.profiler.generator.concept.EntityGenerator;
import grakn.benchmark.profiler.generator.concept.Generator;
import grakn.benchmark.profiler.generator.concept.RelationshipGenerator;
import grakn.benchmark.profiler.generator.schemaspecific.SchemaSpecificDefinition;
import grakn.benchmark.profiler.generator.strategy.AttributeStrategy;
import grakn.benchmark.profiler.generator.strategy.EntityStrategy;
import grakn.benchmark.profiler.generator.strategy.RelationshipStrategy;
import grakn.benchmark.profiler.generator.strategy.TypeStrategy;
import grakn.core.graql.InsertQuery;

import java.util.stream.Stream;

public class QueryGenerator {
    private final SchemaSpecificDefinition schemaSpecificDefinition;

    public QueryGenerator(SchemaSpecificDefinition schemaSpecificDefinition) {
        this.schemaSpecificDefinition = schemaSpecificDefinition;

    }

    public Stream<InsertQuery> nextQueryBatch(){
        Generator<? extends TypeStrategy> generator;
        TypeStrategy typeStrategy = schemaSpecificDefinition.getDefinition().next().next();
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
