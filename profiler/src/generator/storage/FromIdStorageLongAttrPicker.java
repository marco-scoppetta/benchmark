package grakn.benchmark.profiler.generator.storage;

import java.util.Random;
import java.util.stream.Stream;

public class FromIdStorageLongAttrPicker extends FromIdStoragePicker<Long> {

    public FromIdStorageLongAttrPicker(Random rand, ConceptStore conceptStore, String typeLabel) {
        super(rand, conceptStore, typeLabel);
    }

    public Stream<Long> getStream() {
        Stream<Integer> randomUniqueOffsetStream = this.getStreamOfRandomOffsets();
        return randomUniqueOffsetStream.map(randomOffset -> this.conceptStore.getLong(this.typeLabel, randomOffset));
    }

}
