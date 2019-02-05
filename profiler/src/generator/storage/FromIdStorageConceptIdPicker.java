package grakn.benchmark.profiler.generator.storage;

import grakn.core.concept.ConceptId;

import java.util.Random;
import java.util.stream.Stream;

public class FromIdStorageConceptIdPicker extends FromIdStoragePicker<ConceptId> {

    public FromIdStorageConceptIdPicker(Random rand, ConceptStore conceptStore, String typeLabel) {
        super(rand, conceptStore, typeLabel);
    }

    public Stream<ConceptId> getStream() {
        Stream<Integer> randomUniqueOffsetStream = this.getStreamOfRandomOffsets();
        return randomUniqueOffsetStream.map(randomOffset -> this.conceptStore.getConceptId(this.typeLabel, randomOffset));
    }

}
