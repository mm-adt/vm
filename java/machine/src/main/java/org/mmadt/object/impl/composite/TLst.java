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

package org.mmadt.object.impl.composite;

import org.mmadt.object.impl.TObj;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.atomic.Int;
import org.mmadt.object.model.composite.Lst;
import org.mmadt.object.model.type.PList;
import org.mmadt.object.model.util.StringFactory;

import static org.mmadt.language.compiler.Tokens.LIST;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TLst<V extends Obj> extends TObj implements Lst<V> {

    private static final Lst SOME = new TLst<>(null);
    private static final Lst NONE = new TLst<>(null).q(0);
    private static final Lst ALL = new TLst<>(null).q(0, Integer.MAX_VALUE);

    protected TLst(final Object value) {
        super(value);
        this.symbol = LIST;
    }

    public static Lst all() {
        return ALL;
    }

    public static Lst none() {
        return NONE;
    }

    public static Lst some() {
        return SOME;
    }

    public static <V extends Obj> Lst<V> of(final Object... objects) {
        if (objects.length == 1 && objects[0] instanceof PList) {
            return new TLst<>(objects[0]);
        } else {
            final PList<V> value = new PList<>();
            for (final Object object : objects) {
                final V obj = (V) TObj.from(object);

                value.add(obj);
            }
            return new TLst<>(value);
        }
    }

    @Override
    public void add(final Int index, final V value) {
        ((PList<V>) this.value).add(index.get(), value);
    }

    @Override
    public void add(final V value) {
        ((PList<V>) this.value).add(value);
    }

    @Override
    public Lst<V> put(final Int key, final V value) {
        if (((PList<V>) this.value).size() <= key.<Integer>get())
            this.add(key, value);
        else
            ((PList<V>) this.value).set(key.get(), value);
        return this;
    }

    @Override
    public Lst<V> drop(final Int key) {
        ((PList<V>) this.value).remove(key.<Integer>get().intValue());
        return this;
    }

    @Override
    public String toString() {
        return StringFactory.list(this);
    }

    @Override
    public Lst<V> plus(final Lst<V> object) {
        final PList<V> list = new PList<>(this.<PList<V>>get());
        list.addAll(object.<PList<V>>get());
        return TLst.of(list);
    }

    @Override
    public Lst<V> minus(final Lst<V> object) {
        final PList<V> list = new PList<>(this.<PList<V>>get());
        list.removeAll(object.<PList<V>>get());
        return TLst.of(list);
    }

    @Override
    public Lst<V> negate() {
        return this; // TODO: need a good solution
    }

    @Override
    public Lst<V> zero() {
        return TLst.of();
    }

}
