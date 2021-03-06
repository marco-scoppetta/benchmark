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

package grakn.benchmark.runner.generator;

import grakn.core.client.Grakn;
import grakn.core.concept.ConceptId;
import grakn.core.graql.Pattern;
import grakn.core.graql.Query;
import grakn.core.graql.QueryBuilder;
import grakn.core.graql.VarPattern;
import grakn.core.graql.Var;
import grakn.core.graql.Graql;
import grakn.benchmark.runner.strategy.RelationshipStrategy;
import grakn.benchmark.runner.strategy.RolePlayerTypeStrategy;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static grakn.core.graql.internal.pattern.Patterns.var;

/**
 *
 */
public class RelationshipGenerator extends Generator<RelationshipStrategy> {

    /**
     * @param strategy
     * @param tx
     */
    public RelationshipGenerator(RelationshipStrategy strategy, Grakn.Transaction tx) {
        super(strategy, tx);
    }

    /**
     * @return
     */
    @Override
    public Stream<Query> generate() {
        QueryBuilder qb = this.tx.graql();

        int numInstances = this.strategy.getNumInstancesPDF().sample();

        Set<RolePlayerTypeStrategy> rolePlayerTypeStrategies = this.strategy.getRolePlayerTypeStrategies();
        for (RolePlayerTypeStrategy rolePlayerTypeStrategy : rolePlayerTypeStrategies) {
            // Reset the roleplayer pickers to cater for the case where they are central
            rolePlayerTypeStrategy.getPicker().reset();
        }

        String relationshipTypeLabel = strategy.getTypeLabel();

        return Stream.generate(() -> {
            /*
            Process:
            Find roleplayer types according to the RelationshipRoleStrategy objects
            Get a stream of conceptIds that can play that role, according to the picking strategy. This stream may be
            empty for one role, in which case, a decision has to be made whether to make the relationship anyway or abort
             */

            Pattern matchVarPattern = null;  //TODO It will be faster to use a pure insert, supplying the ids for the roleplayers' variables
            VarPattern insertVarPattern = var("r").isa(relationshipTypeLabel);

            boolean foundAnyRoleplayers = false;

            // For each role type strategy
            for (RolePlayerTypeStrategy rolePlayerTypeStrategy : rolePlayerTypeStrategies) {
                String roleLabel = rolePlayerTypeStrategy.getRoleLabel();

                // Find random role-players matching this type
                // Pick ids from the list of concept ids
                Stream<ConceptId> conceptIdStream = rolePlayerTypeStrategy.getPicker().getStream(rolePlayerTypeStrategy.getNumInstancesPDF(), tx);

                Iterator<ConceptId> iter = conceptIdStream.iterator();

                // Build the match insert query
                while (iter.hasNext()) {
                    foundAnyRoleplayers = true;
                    ConceptId conceptId = iter.next();
                    // Add the concept to the query
                    Var v = Graql.var().asUserDefined();
                    if (matchVarPattern == null) {
                        matchVarPattern = v.id(conceptId);
                    } else {
                        Pattern varPattern = v.id(conceptId);
                        matchVarPattern = matchVarPattern.and(varPattern);
                    }
                    insertVarPattern = insertVarPattern.rel(roleLabel, v);
                }
            }

            if (foundAnyRoleplayers) {
                // Assemble the query
                return (Query) qb.match(matchVarPattern).insert(insertVarPattern);
            } else {
                System.out.println("Couldn't find any existing roleplayers for any roles in \"" + relationshipTypeLabel + "\" relationship.");
                return null;
            }

        }).limit(numInstances).filter(Objects::nonNull);
    }
}
