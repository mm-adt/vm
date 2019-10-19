/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.object.model.composite;

import org.mmadt.object.model.Obj;
import org.mmadt.object.model.type.feature.WithRing;

import java.util.function.UnaryOperator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Q<A extends WithRing<A>> extends Obj, WithRing<Q<A>> {

    public enum Tag implements UnaryOperator<Q> {
        zero, one, star, qmark, plus;

        @Override
        public Q apply(final Q quantifier) {
            switch (this) {
                case zero:
                    return (Q) quantifier.set(quantifier.<WithRing>peak().zero().clone().push(quantifier.<WithRing>last().zero().clone()));
                case one:
                    return (Q) quantifier.set(quantifier.<WithRing>peak().one().clone().push(quantifier.<WithRing>last().one().clone()));
                //  case star:
                //      return (Q) quantifier.<WithRing>peak().zero().clone().push(quantifier.<WithRing>last().().clone());
                case qmark:
                    return (Q) quantifier.set(quantifier.<WithRing>peak().zero().clone().push(quantifier.<WithRing>last().one().clone()));
                //  case plus:
                //      return (Q) quantifier.<WithRing>peak().zero().clone().push(quantifier.<WithRing>last().zero().clone());
                default:
                    throw new RuntimeException("Undefined short: " + this);
            }
        }
    }

    public A low();

    public A high();

    // this is necessary as the quantifier is really wrapped in a supplier to avoid stackoverflow during construction
    public A object();

    public Q<A> and(final Q<A> obj);

    public Q<A> or(final Q<A> obj);

}
