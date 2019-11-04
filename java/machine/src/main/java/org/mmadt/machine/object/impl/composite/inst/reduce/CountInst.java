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
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.inst.ReduceInstruction;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.algebra.WithMonoidPlus;
import org.mmadt.machine.object.model.util.ObjectHelper;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class CountInst<S extends Obj, E extends WithMonoidPlus<E>> extends TInst implements ReduceInstruction<S, E> {

    private CountInst() {
        super(PList.of(Tokens.COUNT));
    }

    @Override
    public E apply(final E current, final S obj) {
        return current.plus((E) obj.q().peek()); // TODO: objects must not have range quantification?
    }

    @Override
    public E merge(final E valueA, final E valueB) {
        return valueA.plus(valueB);
    }

    @Override
    public E getInitialValue() {
        return (E) this.q().peek().zero();
    }

    public static <S extends Obj> S create(final S source) {
        return ObjectHelper.allInstances(source) ?
                (S) source.q().peek() :
                source.q().peek().one().set(null).access(source.access().mult(new CountInst<>()));
    }
}