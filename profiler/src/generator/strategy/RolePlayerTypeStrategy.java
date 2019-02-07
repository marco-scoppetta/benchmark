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

package grakn.benchmark.profiler.generator.strategy;

import grakn.benchmark.profiler.generator.pick.LimitedStreamProvider;
import grakn.benchmark.profiler.generator.probdensity.ProbabilityDensityFunction;
import grakn.core.concept.ConceptId;

import java.util.stream.Stream;

/**
 *
 */
public class RolePlayerTypeStrategy extends TypeStrategy {

    private final String roleLabel;
    private LimitedStreamProvider<ConceptId> streamProvider;

    public RolePlayerTypeStrategy(String roleLabel, String relationshipLabel, ProbabilityDensityFunction numInstancesPDF, LimitedStreamProvider<ConceptId> streamProvider) {
        super(relationshipLabel, numInstancesPDF);
        this.roleLabel = roleLabel;
        this.streamProvider = streamProvider;
    }

    public LimitedStreamProvider<ConceptId> getStreamProvider() {
         return streamProvider;
    }

    public String getRoleLabel() {
        return this.roleLabel;
    }

    public Stream<ConceptId> getConceptIds(){
        return getStreamProvider().getStream(getNumInstancesPDF().sample());
    }

}

