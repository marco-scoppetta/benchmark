package grakn.benchmark.profiler.generator.storage;

import grakn.core.client.Grakn;

import java.util.Random;
import java.util.stream.Stream;

public class FromIdStorageLongAttrPicker extends FromIdStoragePicker<Long> {

    public FromIdStorageLongAttrPicker(Random rand, IdStore conceptStore, String typeLabel) {
        super(rand, conceptStore, typeLabel);
    }

    public Stream<Long> getStream(Grakn.Transaction tx) {
        Stream<Integer> randomUniqueOffsetStream = this.getStreamOfRandomOffsets(tx);
        return randomUniqueOffsetStream.map(randomOffset -> this.conceptStore.getLong(this.typeLabel, randomOffset));
    }

}
