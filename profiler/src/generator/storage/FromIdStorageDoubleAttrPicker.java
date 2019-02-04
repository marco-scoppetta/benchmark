package grakn.benchmark.profiler.generator.storage;

import grakn.core.client.Grakn;

import java.util.Random;
import java.util.stream.Stream;

public class FromIdStorageDoubleAttrPicker extends FromIdStoragePicker<Double> {

    public FromIdStorageDoubleAttrPicker(Random rand, IdStore conceptStore, String typeLabel) {
        super(rand, conceptStore, typeLabel);
    }

    public Stream<Double> getStream(Grakn.Transaction tx) {
        Stream<Integer> randomUniqueOffsetStream = this.getStreamOfRandomOffsets(tx);
        return randomUniqueOffsetStream.map(randomOffset -> this.conceptStore.getDouble(this.typeLabel, randomOffset));
    }
}
