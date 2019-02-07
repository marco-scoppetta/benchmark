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

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * @param <T>
 */
public class StandardStreamProvider<T> implements LimitedStreamProvider<T> {
    private Iterator<T> iterator;

    public StandardStreamProvider(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public void resetUniqueness() {
    }

    @Override
    public Stream<T> getStream(int streamLength) {

        // Return the unadjusted stream but with a limit
        return Stream.generate(() -> iterator.next()).limit(streamLength);
    }
}