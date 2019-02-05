package grakn.benchmark.profiler.generator.storage;

import java.util.Random;
import java.util.stream.Stream;

public class FromIdStorageBooleanAttrPicker extends FromIdStoragePicker<Boolean> {

    public FromIdStorageBooleanAttrPicker(Random rand, ConceptStore conceptStore, String typeLabel) {
        super(rand, conceptStore, typeLabel);
    }

    public Stream<Boolean> getStream() {
        Stream<Integer> randomUniqueOffsetStream = this.getStreamOfRandomOffsets();
        return randomUniqueOffsetStream.map(randomOffset -> this.conceptStore.getBoolean(this.typeLabel, randomOffset));
    }
}
