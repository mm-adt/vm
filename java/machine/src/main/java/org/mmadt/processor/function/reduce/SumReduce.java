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

package org.mmadt.processor.function.reduce;

import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.model.composite.Inst;
import org.mmadt.object.model.composite.Q;
import org.mmadt.object.model.type.feature.WithRing;
import org.mmadt.processor.function.AbstractFunction;
import org.mmadt.processor.function.ReduceFunction;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class SumReduce<S extends WithRing<S>> extends AbstractFunction implements ReduceFunction<S, S> {

    private SumReduce(final Q quantifier, final String label) {
        super(quantifier, label);
    }

    @Override
    public S apply(final S obj, final S current) {
        return current.plus(obj).mult(obj.<S>q().low());
    }

    @Override
    public S merge(final S valueA, final S valueB) {
        return valueA.plus(valueB);
    }

    @Override
    public S getInitialValue() {
        return (S) TInt.of(0);
    }


    public static <S extends WithRing<S>> SumReduce<S> compile(final Inst inst) {
        return new SumReduce<>(inst.q(), inst.variable());
    }
}