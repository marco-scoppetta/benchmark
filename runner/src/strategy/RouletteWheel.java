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

package grakn.benchmark.runner.strategy;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * @param <T>
 */
public class RouletteWheel<T> implements PickableCollection {
    private final NavigableMap<Double, T> map = new TreeMap<Double, T>();
    private final Random random;
    private double total = 0;

    public RouletteWheel(Random random) {
        this.random = random;
    }

    public RouletteWheel<T> add(double weight, T result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public T next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }
}