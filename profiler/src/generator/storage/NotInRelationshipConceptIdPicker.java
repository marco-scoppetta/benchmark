package grakn.benchmark.profiler.generator.storage;

import grakn.benchmark.profiler.generator.pick.IteratorFactory;
import grakn.core.concept.ConceptId;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class NotInRelationshipConceptIdPicker extends IteratorFactory<ConceptId> {

    private String relationshipLabel;
    private String roleLabel;
    private String typeLabel;
    ConceptStorage conceptStorage;

    public NotInRelationshipConceptIdPicker(Random rand,
                                            ConceptStorage conceptStorage,
                                            String rolePlayerTypeLabel,
                                            String relationshipLabel,
                                            String roleLabel
    ) {
        super(rand);
        this.typeLabel = rolePlayerTypeLabel;
        this.relationshipLabel = relationshipLabel;
        this.roleLabel = roleLabel;
        this.conceptStorage = conceptStorage;
    }

    public Iterator<ConceptId> getIterator() {

        return new Iterator<ConceptId>() {

            Iterator<Integer> randomUniqueOffsetStream = getIteratorOfRandomOffsets();
            List<String> notInRelationshipConceptIds = conceptStorage.getIdsNotPlayingRole(typeLabel, relationshipLabel, roleLabel);

            @Override
            public boolean hasNext() {
                return randomUniqueOffsetStream.hasNext();
            }

            @Override
            public ConceptId next() {
                int randomOffset = randomUniqueOffsetStream.next();
                return ConceptId.of(notInRelationshipConceptIds.get(randomOffset));
            }
        };

    }

    @Override
    protected Integer getConceptCount() {
        return conceptStorage.numIdsNotPlayingRole(typeLabel, relationshipLabel, roleLabel);
    }

}
