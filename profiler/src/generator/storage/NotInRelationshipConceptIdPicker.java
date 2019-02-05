package grakn.benchmark.profiler.generator.storage;

import grakn.core.concept.ConceptId;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class NotInRelationshipConceptIdPicker extends FromIdStoragePicker<ConceptId> {

    private String relationshipLabel;
    private String roleLabel;

    public NotInRelationshipConceptIdPicker(Random rand,
                                            ConceptStore conceptStore,
                                            String rolePlayerTypeLabel,
                                            String relationshipLabel,
                                            String roleLabel
                                            ) {
        super(rand, conceptStore, rolePlayerTypeLabel);
        this.typeLabel = rolePlayerTypeLabel;
        this.relationshipLabel = relationshipLabel;
        this.roleLabel = roleLabel;

    }

    public Stream<ConceptId> getStream() {
        Stream<Integer> randomUniqueOffsetStream = this.getStreamOfRandomOffsets();
        List<String> notInRelationshipConceptIds = conceptStore.getIdsNotPlayingRole(typeLabel, relationshipLabel, roleLabel);
        return randomUniqueOffsetStream.map(randomOffset -> ConceptId.of(notInRelationshipConceptIds.get(randomOffset)));
    }

    @Override
    public Integer getConceptCount() {
        return conceptStore.numIdsNotPlayingRole(typeLabel, relationshipLabel, roleLabel);
    }

}
