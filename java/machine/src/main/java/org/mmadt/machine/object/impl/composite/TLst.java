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

package org.mmadt.machine.object.impl.composite;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.OperatorHelper;
import org.mmadt.machine.object.model.util.StringFactory;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TLst<V extends Obj> extends TObj implements Lst<V> {

    private static final Lst SOME = new TLst<>(null);
    private static final Lst NONE = new TLst<>(null).q(0);
    private static final Lst ALL = new TLst<>(null).q(0, Integer.MAX_VALUE);

    private TLst(final Object value) {
        super(value);
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
        if (objects.length > 0 && objects[0] instanceof Lst) {
            return ObjectHelper.make(TLst::new, objects);
        } else {
            final PList<V> value = new PList<>();
            for (final Object object : objects) {
                if (object instanceof PList)
                    value.addAll((PList<V>) object);
                else
                    value.add((V) ObjectHelper.from(object));
            }
            return new TLst<>(value);
        }
    }


    @Override
    public Lst<V> put(final V value) {
        return OperatorHelper.binary(Tokens.PUT, (x, y) -> {
            x.<PList<V>>get().add(value);
            return x;
        }, this, null); // TODO: should be based on PLUS
    }

    @Override
    public Lst<V> put(final Int key, final V value) {
        return OperatorHelper.binary(Tokens.PUT, (x, y) -> {
            final PList<V> list = new PList<>(x.<PList<V>>get());
            list.add(key.java(), value);
            return new TLst<>(list);
        }, this, null); // TODO: should be based on PLUS
    }

    @Override
    public Lst<V> drop(final Int key) {
        return OperatorHelper.binary(Tokens.DROP, (x, y) -> {
            final PList<V> list = new PList<>(x.<PList<V>>get());
            list.remove((int) key.java());
            return new TLst<>(list);
        }, this, null); // TODO: should be based on MINUS
    }

    @Override
    public String toString() {
        return StringFactory.list(this);
    }

    @Override
    public Lst<V> plus(final Lst<V> object) {
        return OperatorHelper.binary(Tokens.PLUS, (x, y) -> {
            final PList<V> list = new PList<>(this.<PList<V>>get());
            list.addAll(object.<PList<V>>get());
            return new TLst<>(list);
        }, this, object);
    }

    @Override
    public Lst<V> minus(final Lst<V> object) {
        return OperatorHelper.binary(Tokens.MINUS, (x, y) -> {
            final PList<V> list = new PList<>(this.<PList<V>>get());
            list.removeAll(object.<PList<V>>get());
            return new TLst<>(list);
        }, this, object);
    }

    @Override
    public Lst<V> negate() {
        return this; // TODO: need a good solution -- we need NOT to behave in a more standard way
    }

    @Override
    public Lst<V> zero() {
        return OperatorHelper.unary(Tokens.ZERO, x -> (TLst<V>) TLst.of(), this);
    }

}
