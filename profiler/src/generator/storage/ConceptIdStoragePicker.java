package grakn.benchmark.profiler.generator.storage;

import grakn.benchmark.profiler.generator.pick.IteratorFactory;
import grakn.core.concept.ConceptId;

import java.util.Iterator;
import java.util.Random;

public class ConceptIdStoragePicker extends IteratorFactory<ConceptId> {

    ConceptStorage conceptStorage;
    protected String typeLabel;

    public ConceptIdStoragePicker(Random rand, ConceptStorage conceptStorage, String typeLabel) {
        super(rand);
        this.conceptStorage = conceptStorage;
        this.typeLabel = typeLabel;
    }

    public Iterator<ConceptId> getIterator() {
        Iterator<Integer> randomUniqueOffsetStream = this.getIteratorOfRandomOffsets();
        return new Iterator<ConceptId>() {
            @Override
            public boolean hasNext() {
                return randomUniqueOffsetStream.hasNext();
            }

            @Override
            public ConceptId next() {
                int randomOffset = randomUniqueOffsetStream.next();
                return conceptStorage.getConceptId(typeLabel, randomOffset);
            }
        };
    }

    protected Integer getConceptCount() {
        return this.conceptStorage.getConceptCount(this.typeLabel);
    }

}
