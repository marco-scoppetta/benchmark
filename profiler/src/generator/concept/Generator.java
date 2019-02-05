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

package grakn.benchmark.profiler.generator.concept;

import grakn.benchmark.profiler.generator.strategy.TypeStrategy;
import grakn.core.graql.InsertQuery;

import java.util.stream.Stream;

/**
 * @param <T>
 */
// TODO Should generator have an interface to make it easy to pass generators of different types. This means passing a TypeStrategy as a parameter
public abstract class Generator<T extends TypeStrategy> {

    protected final T strategy;

    /**
     * @param strategy
     */
    public Generator(T strategy) {
        this.strategy = strategy;
    }

    /**
     * @return
     */
    public abstract Stream<InsertQuery> generate();

}
