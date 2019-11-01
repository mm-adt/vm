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

package org.mmadt.machine.object.impl;

import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.model.ObjFactory;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.atomic.Real;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.type.algebra.WithOrderedRing;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TObjFactory<A extends WithOrderedRing<A>> implements ObjFactory {

    protected final A q;

    private TObjFactory(final A quantifier) {
        this.q = quantifier;
    }

    public static TObjFactory<Int> of() {
        return new TObjFactory<>(TInt.of());
    }

    public static <A extends WithOrderedRing<A>> TObjFactory<A> of(final A quantifier) {
        return new TObjFactory<>(quantifier);
    }

    @Override
    public A quantifier() {
        return q;
    }

    @Override
    public BoolFactory<A> bools() {
        return new BoolFactory<>() {
            @Override
            public A quantifier() {
                return q;
            }

            @Override
            public Bool of(Object... bools) {
                return TBool.of(bools);
            }
        };
    }

    @Override
    public Int ints(final Object... ints) {
        return TInt.of(ints);
    }

    @Override
    public Real reals(final Object... reals) {
        return TReal.of(reals);
    }

    @Override
    public Str strs(final Object... strs) {
        return TStr.of(strs);
    }
}
