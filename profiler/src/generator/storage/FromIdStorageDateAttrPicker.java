package grakn.benchmark.profiler.generator.storage;

import java.util.Date;
import java.util.Random;
import java.util.stream.Stream;

public class FromIdStorageDateAttrPicker extends FromIdStoragePicker<Date> {

    public FromIdStorageDateAttrPicker(Random rand, ConceptStore conceptStore, String typeLabel) {
        super(rand, conceptStore, typeLabel);
    }

    public Stream<Date> getStream() {
        Stream<Integer> randomUniqueOffsetStream = this.getStreamOfRandomOffsets();
        return randomUniqueOffsetStream.map(randomOffset -> this.conceptStore.getDate(this.typeLabel, randomOffset));
    }

}
