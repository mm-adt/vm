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

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.type.algebra.WithRing;
import org.mmadt.processor.compiler.Argument;
import org.mmadt.processor.function.AbstractFunction;
import org.mmadt.processor.function.ReduceFunction;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class ReduceReduce<S extends WithRing<S>> extends AbstractFunction implements ReduceFunction<S, S> {

    private ReduceReduce(final Q quantifier, final String label, final Argument<S, S> seed, final Argument<S, S> reduce) {
        super(quantifier, label, seed, reduce);
    }

    @Override
    public S apply(final S obj, final S current) {
        return (S) argument(1).mapArg(obj);
    }

    @Override
    public S merge(final S valueA, final S valueB) {
        return valueA.plus(valueB);
    }

    @Override
    public S getInitialValue() {
        return (S) argument(0).mapArg(TObj.none());
    }


    public static <S extends WithRing<S>> ReduceReduce<S> compile(final Inst inst) {
        return new ReduceReduce<>(inst.q(), inst.variable(), Argument.<S, S>create(inst.args().get(0)), Argument.<S, S>create(inst.args().get(1)));
    }
}