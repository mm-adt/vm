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

package org.mmadt.machine.object.model.composite.inst;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.type.algebra.WithPlus;
import org.mmadt.util.IteratorUtils;

import java.util.Iterator;
import java.util.function.BiFunction;

import static org.mmadt.machine.object.model.composite.Q.Tag.one;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface ReduceInstruction<S extends Obj, E extends Obj> extends Inst, BarrierInstruction<S, E>, BiFunction<E, S, E> {

    public default E merge(final E objA, final E objB) {
        return (E) ((WithPlus) objA).plus((WithPlus) objB);
    }

    public default E getInitialValue() {
        return (E) this.q().zero();
    }

    public default Iterator<E> createIterator(final E reduction) {
        return IteratorUtils.of(reduction);
    }

    public default Obj computeRange(final Obj domain) {
        return domain.q(one);
    }

}