package grakn.benchmark.profiler.generator.schemaspecific;

import grakn.benchmark.profiler.generator.pick.StreamProvider;
import grakn.benchmark.profiler.generator.pick.StringStreamGenerator;
import grakn.benchmark.profiler.generator.probdensity.FixedConstant;
import grakn.benchmark.profiler.generator.probdensity.FixedDiscreteGaussian;
import grakn.benchmark.profiler.generator.probdensity.ScalingBoundedZipf;
import grakn.benchmark.profiler.generator.probdensity.ScalingDiscreteGaussian;
import grakn.benchmark.profiler.generator.storage.ConceptStore;
import grakn.benchmark.profiler.generator.storage.FromIdStorageConceptIdPicker;
import grakn.benchmark.profiler.generator.strategy.AttributeStrategy;
import grakn.benchmark.profiler.generator.strategy.EntityStrategy;
import grakn.benchmark.profiler.generator.strategy.RelationshipStrategy;
import grakn.benchmark.profiler.generator.strategy.RolePlayerTypeStrategy;
import grakn.benchmark.profiler.generator.strategy.RouletteWheel;
import grakn.benchmark.profiler.generator.strategy.TypeStrategy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class SocialNetworkDefinition implements SchemaSpecificDefinition {

    private Random random;
    private ConceptStore storage;

    private RouletteWheel<TypeStrategy> entityStrategies;
    private RouletteWheel<TypeStrategy> relationshipStrategies;
    private RouletteWheel<TypeStrategy> attributeStrategies;
    private RouletteWheel<RouletteWheel<TypeStrategy>> metaTypeStrategies;

    public SocialNetworkDefinition(Random random, ConceptStore storage) {
        this.random = random;
        this.storage = storage;

        this.entityStrategies = new RouletteWheel<>(random);
        this.relationshipStrategies = new RouletteWheel<>(random);
        this.attributeStrategies = new RouletteWheel<>(random);
        this.metaTypeStrategies = new RouletteWheel<>(random);

        buildGenerator();
    }

    private void buildGenerator() {
        buildStrategies();
        this.metaTypeStrategies.add(1.0, entityStrategies);
        this.metaTypeStrategies.add(1.2, relationshipStrategies);
        this.metaTypeStrategies.add(1.0, attributeStrategies);
    }

    private void buildStrategies() {

        /*
        Entities
         */

        this.entityStrategies.add(
                1.0,
                new EntityStrategy(
                        "person",
                        new FixedDiscreteGaussian(this.random, 25, 10))
        );

        this.entityStrategies.add(
                1.0,
                new EntityStrategy(
                        "page",
                        new FixedDiscreteGaussian(this.random, 5, 1)
                )
        );

        /*
        Attributes
         */

        StringStreamGenerator nameStream = new StringStreamGenerator(random, 6);

        this.attributeStrategies.add(
                1.0,
                new AttributeStrategy<>(
                        "name",
                        new FixedDiscreteGaussian(this.random,18, 3),
                        new StreamProvider<>(nameStream)
                )
        );


        /*
        Relationships
         */


        // friendship
        RolePlayerTypeStrategy friendRoleFiller = new RolePlayerTypeStrategy(
                "friend",
                "friendship",
                new FixedConstant(2),
                new StreamProvider<>(
                    new FromIdStorageConceptIdPicker(
                        random,
                        this.storage,
                        "person")
                )
        );
        this.relationshipStrategies.add(
                1.0,
                new RelationshipStrategy(
                        "friendship",
                        new ScalingBoundedZipf(this.random, ()-> storage.getGraphScale(), 0.5, 2.3),
                        new HashSet<>(Arrays.asList(friendRoleFiller))
                )
        );


        // like
        RolePlayerTypeStrategy likedPageRole = new RolePlayerTypeStrategy(
                "liked",
                "like",
                new FixedConstant(1),
                new StreamProvider<>(new FromIdStorageConceptIdPicker(random, storage, "page"))
        );
        RolePlayerTypeStrategy likerPersonRole = new RolePlayerTypeStrategy(
                "liker",
                "like",
                new FixedConstant(1),
                new StreamProvider<>(new FromIdStorageConceptIdPicker(random, storage, "person"))
        );
        this.relationshipStrategies.add(
                1.0,
                new RelationshipStrategy(
                        "like",
                        new ScalingDiscreteGaussian(random, () -> storage.getGraphScale(), 0.05, 0.001),
                        new HashSet<>(Arrays.asList(likedPageRole, likerPersonRole))
                )
        );


        // @has-name
        RolePlayerTypeStrategy nameOwner = new RolePlayerTypeStrategy(
                "@has-name-owner",
                "@has-name",
                new FixedConstant(1),
                new StreamProvider<>(new FromIdStorageConceptIdPicker(random, storage, "person"))
        );
        RolePlayerTypeStrategy nameValue = new RolePlayerTypeStrategy(
                "@has-name-value",
                "@has-name",
                new FixedConstant(1),
                new StreamProvider<>(new FromIdStorageConceptIdPicker(random, storage, "name"))
        );
        this.relationshipStrategies.add(
                1.0,
                new RelationshipStrategy(
                        "@has-name",
                        new ScalingDiscreteGaussian(random, () -> storage.getGraphScale(), 0.1, 0.03),
                        new HashSet<>(Arrays.asList(nameOwner, nameValue))
                )
        );
    }

    @Override
    public RouletteWheel<RouletteWheel<TypeStrategy>> getDefinition() {
        return this.metaTypeStrategies;
    }

}
