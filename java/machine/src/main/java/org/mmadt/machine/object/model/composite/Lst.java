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

package org.mmadt.machine.object.model.composite;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.DropInst;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.PutInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.ext.algebra.WithGroupPlus;
import org.mmadt.machine.object.model.ext.algebra.WithProduct;
import org.mmadt.machine.object.model.util.ObjectHelper;

import java.util.List;

/**
 * A Java representation of the {@code lst} object in mm-ADT.
 * A {@code lst} is a semigroup over +.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Lst<V extends Obj> extends WithGroupPlus<Lst<V>>, WithProduct<Int, V> {

    public default List<V> java() {
        return this.get();
    }

    @Override
    public Lst<V> label(final String variable);

    public default Lst<V> put(final V value) {
        if (this.isInstance() || this.isType()) {
            this.java().add(value);
            return this;
        } else
            return (Lst<V>) PutInst.<Int, V>create(TInt.of(100), value).attach(this); // TODO: should have isolated put(value) (like add(value))
    }

    @Override
    public default Lst<V> put(final Int index, final V value) {
        if (this.isInstance() || this.isType()) {
            this.java().set(index.java(), value);
            return this;
        } else
            return (Lst<V>) PutInst.<Int, V>create(index, value).attach(this);
    }

    @Override
    public default Lst<V> drop(final Int index) {
        if (this.isInstance() || this.isType()) {
            this.java().remove((int) index.java());
            return this;
        } else
            return (Lst<V>) DropInst.<Int, V>create(index).attach(this);
    }

    @Override
    public default V get(final Int index) {
        // TODO: support multi-get if the argument matches multiple keys (returns a ref)
        // TODO: should we have special handling for ref (as it requires a derivaition)
        return (this.<List<V>>get().size() <= index.<Integer>get() ? (V) TObj.none() : this.<List<V>>get().get(index.get())).copy(this);
    }

    public default V get(final Object index) {
        return this.get(ObjectHelper.create(TInt.of(), index));
    }

    public default Lst<V> put(final Object index, final Object value) {
        return this.put(ObjectHelper.create(TInt.of(), index), ObjectHelper.create(TObj.single(), value));
    }

    @Override
    public default boolean test(final Obj obj) {
        if (obj instanceof Lst && this.get() != null && obj.get() != null) {
            final Lst list = (Lst) obj;
            if (list.java().size() < this.java().size())
                return false;
            for (int i = 0; i < this.java().size(); i++) {
                if (!this.get(i).test(list.get(i)))
                    return false;
            }
            return true;
        }
        return WithProduct.super.test(obj);
    }
}
