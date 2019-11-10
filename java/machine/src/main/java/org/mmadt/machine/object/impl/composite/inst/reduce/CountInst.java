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
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.composite.inst.ReduceInstruction;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.algebra.WithOrderedRing;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class CountInst<S extends Obj, E extends WithOrderedRing<E>> extends TInst implements ReduceInstruction<S, Q<E>> {

    private CountInst() {
        super(PList.of(Tokens.COUNT));
    }

    @Override
    public Q<E> apply(final Q<E> current, final S obj) {
        return current.plus(obj.q());
    }

    @Override
    public Q<E> merge(final Q<E> valueA, final Q<E> valueB) {
        return valueA.plus(valueB);
    }

    @Override
    public Q<E> getInitialValue() {
        return (Q<E>) this.q().zero();
    }

    public static <S extends Obj, E extends WithOrderedRing<E>> CountInst<S, E> create() {
        return new CountInst<>();
    }
}