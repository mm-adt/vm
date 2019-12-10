/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.machine.object.model.util;

import org.mmadt.machine.object.impl.ext.composite.TPair;
import org.mmadt.machine.object.model.ext.algebra.WithOrderedRing;
import org.mmadt.machine.object.model.ext.composite.Pair;

import java.util.function.UnaryOperator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class QuantifierHelper {

    public static final WithOrderedRing ONE = TPair.of(1, 1);

    private QuantifierHelper() {
        // static helper class
    }

    public static boolean isStar(final WithOrderedRing quantifier) {
        return quantifier instanceof Pair && ((Pair) quantifier).first().isZero() && ((Pair) quantifier).second().isMax();
    }

    public static boolean isQMark(final WithOrderedRing quantifier) {
        return quantifier instanceof Pair && ((Pair) quantifier).first().isZero() && ((Pair) quantifier).second().isOne();
    }

    public static boolean isPlus(final WithOrderedRing quantifier) {
        return quantifier instanceof Pair && ((Pair) quantifier).first().isOne() && ((Pair) quantifier).second().isMax();
    }

    public static boolean isSingle(final WithOrderedRing quantifier) {
        return !(quantifier instanceof Pair) || ((Pair) quantifier).first().equals(((Pair) quantifier).second());
    }

    public static <S extends WithOrderedRing<S>> S trySingle(final WithOrderedRing quantifier) {
        return (S) (isSingle(quantifier) && quantifier instanceof Pair ? ((Pair) quantifier).first() : quantifier);
    }

    public static WithOrderedRing toPair(final WithOrderedRing quantifier) {
        return quantifier instanceof Pair ? quantifier : TPair.of(quantifier, quantifier);
    }

    public static boolean within(final WithOrderedRing a, final WithOrderedRing b) {
        final Pair pairA = ((Pair) b);
        final Pair pairB = ((Pair) a);
        return pairA.first().gte(pairB.first()).java() && pairA.second().lte(pairB.second()).java();
    }

    public enum Tag implements UnaryOperator<WithOrderedRing<WithOrderedRing>> {
        zero, one, star, qmark, plus;

        @Override
        public WithOrderedRing<WithOrderedRing> apply(final WithOrderedRing<WithOrderedRing> quantifier) {
            final WithOrderedRing q = quantifier instanceof Pair ? ((Pair) quantifier).first() : quantifier;
            switch (this) {
                case zero:
                    return TPair.of(q.zero(), q.zero());
                case one:
                    return TPair.of(q.one(), q.one());
                case star:
                    return TPair.of(q.zero(), q.max());
                case qmark:
                    return TPair.of(q.zero(), q.one());
                case plus:
                    return TPair.of(q.one(), q.max());
                default:
                    throw new RuntimeException("Undefined shorthand: " + this);
            }
        }
    }
}
