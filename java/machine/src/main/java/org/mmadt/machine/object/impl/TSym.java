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

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.type.Bindings;
import org.mmadt.machine.object.model.util.ObjectHelper;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TSym<A extends Obj> extends TObj {

    private final AtomicReference<A> object = new AtomicReference<>();

    public static <A extends Obj> TSym<A> of(final String symbol) {
        return new TSym<>(symbol, null);
    }

    public static <A extends Obj> TSym<A> of(final String symbol, final A object) {
        return new TSym<A>(symbol, object);
    }

    private TSym(final String symbol, final A object) {
        super(null);
        this.types = TType.of(symbol);
        this.object.set(object);
    }

    public A getObject() {
        return this.object.get();
    }

    public void setObject(final A object) {
        this.object.set(object);
    }

    public static Obj fetch(final Obj object) {
        if (object instanceof TSym) {
            assert !(((TSym) object).getObject() instanceof TSym);
            return ((TSym<?>) object).getObject();
        } else
            return object;
    }

    @Override
    public boolean test(final Obj object) {
        return this.getObject().test(object);
    }

    @Override
    public boolean match(final Bindings bindings, final Obj object) {
        if (!this.q().test(object))
            return false;
        else if (null != ObjectHelper.getName(this) &&
                ObjectHelper.getName(this).equals(ObjectHelper.getName(object))) {
            if (null != this.label())
                bindings.put(this.label(), object);
            return true;
        } else if (null == this.getObject())
            return true;
        else
            return this.getObject().match(bindings, object);
    }

    @Override
    public int hashCode() {
        return this.symbol().hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof TSym && Objects.equals(this.symbol(), ((TSym) object).symbol());
        //&&Objects.equals(this.getObject().symbol(), ((TSym) object).getObject().symbol());
    }
}
