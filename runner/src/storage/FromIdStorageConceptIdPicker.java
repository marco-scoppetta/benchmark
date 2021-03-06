package grakn.benchmark.runner.storage;

import grakn.core.client.Grakn;
import grakn.core.concept.ConceptId;

import java.util.Random;
import java.util.stream.Stream;

public class FromIdStorageConceptIdPicker extends FromIdStoragePicker<ConceptId> {

    public FromIdStorageConceptIdPicker(Random rand, IdStoreInterface conceptStore, String typeLabel) {
        super(rand, conceptStore, typeLabel);
    }

    @Override
    public Stream<ConceptId> getStream(Grakn.Transaction tx) {
        Stream<Integer> randomUniqueOffsetStream = this.getStreamOfRandomOffsets(tx);
        return randomUniqueOffsetStream.map(randomOffset -> this.conceptStore.getConceptId(this.typeLabel, randomOffset));
    }

}
