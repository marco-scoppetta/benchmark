package grakn.benchmark.profiler.generator.storage;

import grakn.core.client.Grakn;

import java.util.Random;
import java.util.stream.Stream;

public class FromIdStorageBooleanAttrPicker extends FromIdStoragePicker<Boolean> {

    public FromIdStorageBooleanAttrPicker(Random rand, IdStore conceptStore, String typeLabel) {
        super(rand, conceptStore, typeLabel);
    }

    public Stream<Boolean> getStream(Grakn.Transaction tx) {
        Stream<Integer> randomUniqueOffsetStream = this.getStreamOfRandomOffsets(tx);
        return randomUniqueOffsetStream.map(randomOffset -> this.conceptStore.getBoolean(this.typeLabel, randomOffset));
    }
}
