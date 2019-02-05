package grakn.benchmark.profiler.generator.storage;

import java.util.Random;
import java.util.stream.Stream;

public class FromIdStorageStringAttrPicker extends FromIdStoragePicker<String> {

    public FromIdStorageStringAttrPicker(Random rand, ConceptStore conceptStore, String typeLabel) {
        super(rand, conceptStore, typeLabel);
    }

    public Stream<String> getStream() {
        Stream<Integer> randomUniqueOffsetStream = this.getStreamOfRandomOffsets();
        return randomUniqueOffsetStream.map(randomOffset -> this.conceptStore.getString(this.typeLabel, randomOffset));
    }
}
