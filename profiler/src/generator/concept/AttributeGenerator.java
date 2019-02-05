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

import grakn.core.graql.Graql;
import grakn.core.graql.InsertQuery;
import grakn.core.graql.Var;
import grakn.benchmark.profiler.generator.probdensity.FixedConstant;
import grakn.benchmark.profiler.generator.pick.StreamProviderInterface;
import grakn.benchmark.profiler.generator.strategy.AttributeStrategy;

import java.util.Objects;
import java.util.stream.Stream;


/**
 * @param <ValueDatatype>
 */
public class AttributeGenerator< ValueDatatype> extends Generator<AttributeStrategy<ValueDatatype>> {
    /**
     * @param strategy
     */
    public AttributeGenerator(AttributeStrategy<ValueDatatype> strategy) {
        super(strategy);
    }

    /**
     * @return
     */
    @Override
    public Stream<InsertQuery> generate() {
        int numInstances = this.strategy.getNumInstancesPDF().sample();

        StreamProviderInterface<ValueDatatype> valuePicker = this.strategy.getPicker();
        valuePicker.reset();
        FixedConstant unityPDF = new FixedConstant(1);

        String attributeTypeLabel = this.strategy.getTypeLabel();

        return Stream.generate(() -> {
            Var attr = Graql.var().asUserDefined();
            Stream<ValueDatatype> valueStream = valuePicker.getStream(unityPDF);
            ValueDatatype value = valueStream.findFirst().get();
            return Graql.insert(attr.isa(attributeTypeLabel), attr.val(value));
        }).limit(numInstances).filter(Objects::nonNull);
    }
}
