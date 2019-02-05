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

import grakn.benchmark.profiler.generator.strategy.EntityStrategy;
import grakn.core.graql.Graql;
import grakn.core.graql.InsertQuery;

import java.util.stream.Stream;

import static grakn.core.graql.internal.pattern.Patterns.var;

/**
 *
 */
public class EntityGenerator extends Generator<EntityStrategy> {
    public EntityGenerator(EntityStrategy strategy) {
        super(strategy);
    }

    /**
     * @return
     */
    @Override
    public Stream<InsertQuery> generate() {

        // TODO Can using toString be avoided? Waiting for TP task #20179
//        String entityTypeName = this.strategy.getType().label().getValue();

        String typeLabel = this.strategy.getTypeLabel();

        int numInstances = this.strategy.getNumInstancesPDF().sample();

        return Stream.generate(() -> Graql.insert(var("x").isa(typeLabel)))
                .limit(numInstances);
    }
}