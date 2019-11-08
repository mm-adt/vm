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

package org.mmadt.machine.object.impl.composite.inst.reduce;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.composite.inst.ReduceInstruction;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.type.algebra.WithMonoidPlus;

import static org.mmadt.machine.object.model.composite.Q.Tag.one;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class GroupCountInst<S extends Obj, E extends Obj, A extends WithMonoidPlus<A>> extends TInst implements ReduceInstruction<S, Rec<E, A>> {

    private GroupCountInst(final Object arg) {
        super(PList.of(Tokens.GROUPCOUNT, arg));
    }

    @Override
    public Rec<E, A> apply(final Rec<E, A> current, final S obj) {
        final E object = this.<S, E>argument(0).mapArg(obj);
        final E objectOne = object.q(one);
        current.put(objectOne, ((A) object.q()).plus(current.<PMap<E, A>>get().getOrDefault(objectOne, (A) this.q().zero())));
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
        return TRec.of().q(this.q().one());
    }

    public static <S extends Obj, E extends Obj, A extends WithMonoidPlus<A>> GroupCountInst<S, E, A> create(final Object arg) {
        return new GroupCountInst<>(arg);
    }
}