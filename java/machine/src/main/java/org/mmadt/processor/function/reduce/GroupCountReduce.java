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

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.type.algebra.WithMonoidPlus;
import org.mmadt.processor.compiler.Argument;
import org.mmadt.processor.function.AbstractFunction;
import org.mmadt.processor.function.ReduceFunction;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class GroupCountReduce<S extends Obj, E extends Obj, A extends WithMonoidPlus<A>> extends AbstractFunction implements ReduceFunction<S, Rec<E, A>> {

    private GroupCountReduce(final Q quantifier, final String label, final Argument<S, E> argument) {
        super(quantifier, label, argument);

    }

    @Override
    public Rec<E, A> apply(final S obj, final Rec<E, A> current) {
        final E object = this.<S, E>argument(0).mapArg(obj);
        current.put(object, ((A) object.q().peek()).plus(current.<PMap<E, A>>get().getOrDefault(object, (A) this.quantifier().zero().peek())));
        return current;
    }

    @Override
    public Rec<E, A> merge(final Rec<E, A> valueA, final Rec<E, A> valueB) {
        final Rec<E, A> rec = TRec.of();
        valueA.<PMap<E, A>>get().forEach(rec::put);
        valueB.<PMap<E, A>>get().forEach(rec::put);
        return rec;
    }

    @Override
    public Rec<E, A> getInitialValue() {
        return TRec.of().q(quantifier().one());
    }

    public static <S extends Obj, E extends Obj, A extends WithMonoidPlus<A>> GroupCountReduce<S, E, A> compile(final Inst inst) {
        return new GroupCountReduce<>(inst.q(), inst.label(), Argument.create(inst.args().isEmpty() ? TInst.of(Tokens.ID) : inst.args().get(0)));
    }

}