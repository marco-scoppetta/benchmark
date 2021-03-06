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

package grakn.benchmark.runner.pick;

import grakn.core.client.Grakn;
import grakn.core.concept.ConceptId;
import grakn.core.graql.Graql;
import grakn.core.graql.QueryBuilder;
import grakn.core.graql.Var;
import java.util.stream.Stream;

/**
 *
 */
// TODO I think I change this to be specific "CheckNotInRelationship" since being abstract gives complexity
public class NotInRelationshipConceptIdStream implements StreamInterface<ConceptId> {

    private String relationshipLabel;
    private String roleLabel;
    private StreamInterface<ConceptId> conceptIdStreamer;
    private Integer numAttemptsLimit;

    private int requiredLength = 1;

    public NotInRelationshipConceptIdStream(String relationshipLabel,
                                            String roleLabel,
                                            Integer numAttemptsLimit,
                                            StreamInterface<ConceptId> conceptIdStreamer) {
        this.conceptIdStreamer = conceptIdStreamer;
        this.relationshipLabel = relationshipLabel;
        this.roleLabel = roleLabel;
        this.numAttemptsLimit = numAttemptsLimit;

    }


    @Override
    public boolean checkAvailable(int requiredLength, Grakn.Transaction tx) {
        return this.conceptIdStreamer.checkAvailable(requiredLength, tx);
    }

    /**
     * TODO remove this hack when have negation
     * @param requiredLength
     */
    public void setRequiredLength(int requiredLength) {
        this.requiredLength = requiredLength;
    }

    @Override
    public Stream<ConceptId> getStream(Grakn.Transaction tx) {

        Stream<ConceptId> stream = this.conceptIdStreamer.getStream(tx);

        QueryBuilder qb = tx.graql();

        // TODO remove hack when we have negation

        // TODO Ideally this should also terminate when it's no longer possible to find the number of concepts required.
        // TODO Shouldn't the limit for the number of attempts be before the filter? Otherwise it will always wait
        return stream.limit(this.numAttemptsLimit * this.requiredLength).filter(conceptId -> { //TODO Should probably be using stream.filter instead
                // Keep looking for a concept not in this kind of relationship, stop when one is found (by returning)
                // or the max number of attempts to find one has been reached

                Var x = Graql.var("x");
                Var r = Graql.var("r");
                // TODO To improve speed, here we could try to store a set of those conceptIds tried in the past and
                // found to be in a relationship, so that we don't have to check again. This amounts to doing some kind of storage
                boolean inRelationship = qb.match(x.id(conceptId), r.rel(this.roleLabel, x).isa(this.relationshipLabel)).get().execute().size() != 0;
                return !inRelationship;
        });
    }
}
