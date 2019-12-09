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
import org.mmadt.machine.object.impl.TSym;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.DropInst;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.PutInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.algebra.WithGroupPlus;
import org.mmadt.machine.object.model.type.algebra.WithProduct;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.processor.util.FastProcessor;

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
        final PList<V> object = this.get();
        V v = object.size() <= index.<Integer>get() ? (V) TObj.none() : object.get(index.get());
        if (null != v.label())  // TODO: this is ghetto---need a general solution
            v = v.state().write(v);
        return v.copy(this);
    }

    public default <A extends Obj> A get(final Object index) {
        return (A) this.get(ObjectHelper.create(TInt.of(), index));
    }

    public default Lst<V> put(final Object index, final Object value) {
        return this.put(ObjectHelper.create(TInt.of(), index), (V) ObjectHelper.create(TObj.single(), value));
    }

    @Override
    public default Iterable<Lst<V>> iterable() {
        return this.isInstance() ? List.of(this) : () -> FastProcessor.process(this);
    }
}
