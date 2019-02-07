/*
 *  GRAKN.AI - THE KNOWLEDGE GRAPH
 *  Copyright (C) 2018 Grakn Labs Ltd
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package grakn.benchmark.profiler.generator.pick;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Consumer;

/**
 *
 */
public class RandomOffsetIterator implements Iterator<Integer> {

    private final Random rand;
    private final int offsetBound;
    private HashSet<Object> previousRandomOffsets = new HashSet<>();


    public RandomOffsetIterator(Random rand, int offsetBound) {
        this.rand = rand;
        this.offsetBound = offsetBound;
    }

    @Override
    public boolean hasNext() {
        return !(previousRandomOffsets.size() == offsetBound);
    }

    @Override
    public Integer next() {
        boolean foundUnique = false;

        int nextChoice = 0;
        while (!foundUnique) {
            nextChoice = rand.nextInt(offsetBound);
            foundUnique = previousRandomOffsets.add(nextChoice);
        }
        return nextChoice;
    }
}

