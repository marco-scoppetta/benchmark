package grakn.benchmark.profiler.generator.storage;

import java.util.Random;
import java.util.stream.Stream;

public class FromIdStorageDoubleAttrPicker extends FromIdStoragePicker<Double> {

    public FromIdStorageDoubleAttrPicker(Random rand, ConceptStore conceptStore, String typeLabel) {
        super(rand, conceptStore, typeLabel);
    }

    public Stream<Double> getStream() {
        Stream<Integer> randomUniqueOffsetStream = this.getStreamOfRandomOffsets();
        return randomUniqueOffsetStream.map(randomOffset -> this.conceptStore.getDouble(this.typeLabel, randomOffset));
    }
}
