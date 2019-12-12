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

package org.mmadt.machine.object.impl.ext.composite;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TType;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.ext.composite.Pair;
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.machine.object.model.ext.algebra.WithOrderedRing;
import org.mmadt.machine.object.model.util.StringFactory;

import java.util.Objects;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TPair<V extends WithOrderedRing<V>> extends TLst<V> implements Pair<V> {

    private TPair(final Object object) {
        super(object);
        this.type = TType.of(Tokens.PAIR);
    }

    public static <V extends WithOrderedRing<V>> Pair of(final Object first, final Object second) {
        return new TPair<>(PList.of(first, second));
    }

    @Override
    public V first() {
        return this.get(TInt.zeroInt());
    }

    @Override
    public V second() {
        return this.get(TInt.oneInt());
    }

    @Override
    public Lst<V> neg() {
        return this.set(PList.of(this.first().neg(), this.second().neg()));
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
    public boolean isZero() {
        return first().isZero() && second().isZero();
    }

    @Override
    public boolean isOne() {
        return first().isOne() && second().isOne();
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
        return this.first().gte(object.get(0)).and(this.second().gte(object.get(1)));
    }

    @Override
    public Bool gte(Lst<V> object) {
        return this.first().gte(object.get(0)).and(this.second().gte(object.get(1)));
    }

    @Override
    public Bool lte(Lst<V> object) {
        return this.first().lte(object.get(0)).and(this.second().lte(object.get(1)));
    }

    @Override
    public Bool lt(final Lst<V> object) {
        return this.first().lte(object.get(0)).and(this.second().lte(object.get(1)));
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

    @Override
    public int hashCode() {
        return Objects.hash(this.first(), this.second());
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Pair && this.first().equals(((Pair) other).first()) && this.second().equals(((Pair) other).second());
    }

    @Override
    public String toString() {
        return StringFactory.list(this);
    }
}
