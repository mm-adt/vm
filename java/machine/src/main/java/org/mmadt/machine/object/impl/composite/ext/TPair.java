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

package org.mmadt.machine.object.impl.composite.ext;

import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.ext.Pair;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.algebra.WithOrderedRing;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TPair<V extends WithOrderedRing<V>> extends TLst<V> implements Pair<V> {

    private TPair(final Object object) {
        super(object);
    }

    private V first() {
        return this.<PList<V>>get().get(0);
    }

    private V second() {
        return this.<PList<V>>get().get(1);
    }

    public static <V extends WithOrderedRing<V>> Pair<V> of(final V first, final V second) {
        return new TPair<>(PList.of(first, second));
    }

    @Override
    public Lst<V> one() {
        final V one = first().one();
        return this.set(PList.of(one, one));
    }

    @Override
    public Lst<V> zero() {
        final V zero = first().zero();
        return this.set(PList.of(zero, zero));
    }


    @Override
    public Lst<V> mult(final Lst<V> object) {
        final V first = first().mult(object.get(TInt.zeroInt()));
        final V second = second().mult(object.get(TInt.oneInt()));
        return this.set(PList.of(first, second));
    }

    @Override
    public Lst<V> plus(final Lst<V> object) {
        final V first = first().plus(object.get(TInt.zeroInt()));
        final V second = second().plus(object.get(TInt.oneInt()));
        return this.set(PList.of(first, second));
    }

    @Override
    public Bool gt(final Lst<V> object) {
        return null;
    }

    @Override
    public Bool lt(final Lst<V> object) {
        return null;
    }

    @Override
    public Lst<V> max() {
        final V max = first().max();
        return this.set(PList.of(max, max));
    }

    @Override
    public Lst<V> min() {
        final V min = first().min();
        return this.set(PList.of(min, min));
    }
}
