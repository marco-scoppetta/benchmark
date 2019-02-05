package grakn.benchmark.profiler.generator.schemaspecific;

import grakn.benchmark.profiler.generator.pick.CountingStreamGenerator;
import grakn.benchmark.profiler.generator.pick.StreamProvider;
import grakn.benchmark.profiler.generator.probdensity.FixedConstant;
import grakn.benchmark.profiler.generator.probdensity.FixedDiscreteGaussian;
import grakn.benchmark.profiler.generator.probdensity.ScalingDiscreteGaussian;
import grakn.benchmark.profiler.generator.storage.ConceptStore;
import grakn.benchmark.profiler.generator.storage.FromIdStorageConceptIdPicker;
import grakn.benchmark.profiler.generator.storage.NotInRelationshipConceptIdPicker;
import grakn.benchmark.profiler.generator.strategy.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class FinancialTransactionsDefinition implements SchemaSpecificDefinition {

    private Random random;
    private ConceptStore storage;

    private RouletteWheel<TypeStrategy> entityStrategies;
    private RouletteWheel<TypeStrategy> relationshipStrategies;
    private RouletteWheel<TypeStrategy> attributeStrategies;
    private RouletteWheel<RouletteWheel<TypeStrategy>> operationStrategies;

    public FinancialTransactionsDefinition(Random random, ConceptStore storage) {
        this.random = random;
        this.storage = storage;


        this.entityStrategies = new RouletteWheel<>(random);
        this.relationshipStrategies = new RouletteWheel<>(random);
        this.attributeStrategies = new RouletteWheel<>(random);
        this.operationStrategies = new RouletteWheel<>(random);

        buildGenerator();
    }

    private void buildGenerator() {
        buildStrategies();
        this.operationStrategies.add(1.0, entityStrategies);
        this.operationStrategies.add(1.0, relationshipStrategies);
        this.operationStrategies.add(1.0, attributeStrategies);
    }

    private void buildStrategies() {

        /*
        Entities
         */

        // SCALING number of entities added!
        this.entityStrategies.add(
                1.0,
                new EntityStrategy(
                        "trader",
                        new ScalingDiscreteGaussian(this.random, () -> storage.getGraphScale(), 0.02, 0.01))
        );


        /*
        Attributes
         */

        // FIXED number of attributes added
        CountingStreamGenerator idGenerator = new CountingStreamGenerator(0);
        this.attributeStrategies.add(
                1.0,
                new AttributeStrategy<>(
                        "quantity",
                        new FixedDiscreteGaussian(this.random,5,3),
                        new StreamProvider<>(idGenerator)
                )
        );


        /*
        Relationships
         */

        // increasingly large interactions (increasing number of role players)
        RolePlayerTypeStrategy transactorRolePlayer = new RolePlayerTypeStrategy(
                "transactor",
                "transaction",
                // high variance in the number of role players
                new ScalingDiscreteGaussian(random, () -> storage.getGraphScale(), 0.01, 0.01),
                new StreamProvider<>(
                        new FromIdStorageConceptIdPicker(
                                random,
                                 this.storage,
                                "trader")
                )
        );
        this.relationshipStrategies.add(
                1.0,
                new RelationshipStrategy(
                        "transaction",
                        new FixedDiscreteGaussian(this.random, 50, 10), // but fixed number of rels added per iter
                        new HashSet<>(Arrays.asList(transactorRolePlayer))
                )
        );


        // @has-quantity on the transaction relationship (1 quantity per transaction)
        RolePlayerTypeStrategy quantityOwner = new RolePlayerTypeStrategy(
                "@has-quantity-owner",
                "@has-quantity",
                new FixedConstant(1),
                new StreamProvider<>(
                        new NotInRelationshipConceptIdPicker(
                                random,
                                storage,
                                "transaction", "@has-quantity", "@has-quantity-owner"
                        )
                )
        );
        RolePlayerTypeStrategy quantityValue = new RolePlayerTypeStrategy(
                "@has-quantity-value",
                "@has-quantity",
                new FixedConstant(1),
                new StreamProvider<>(
                        new FromIdStorageConceptIdPicker(
                                random,
                                this.storage,
                                "quantity"
                        )
                )
        );
        this.relationshipStrategies.add(
                1.0,
                new RelationshipStrategy(
                        "@has-quantity",
                        new ScalingDiscreteGaussian(random, () -> storage.getGraphScale(), 0.01, 0.005), // more than number of entities being created to compensate for being picked less
                        new HashSet<>(Arrays.asList(quantityOwner, quantityValue))
                )
        );

    }

    @Override
    public RouletteWheel<RouletteWheel<TypeStrategy>> getDefinition() {
        return this.operationStrategies;
    }

}
