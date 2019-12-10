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
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.DropInst;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.PutInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.type.algebra.WithGroupPlus;
import org.mmadt.machine.object.model.type.algebra.WithProduct;
import org.mmadt.machine.object.model.util.ObjectHelper;

import java.util.Map;

/**
 * A Java representation of the {@code rec} object in mm-ADT.
 * A {@code rec} is a ... TODO: full define record algebra
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Rec<K extends Obj, V extends Obj> extends WithGroupPlus<Rec<K, V>>, WithProduct<K, V> {

    public default Map<K, V> java() {
        return this.get();
    }

    @Override
    public default Rec<K, V> put(final K key, final V value) {
        if ((this.isInstance() || this.isType()) && !key.isReference() && !value.isReference()) {
            this.java().put(key, value);
            return this;
        } else
            return (Rec<K, V>) PutInst.<K, V>create(key, value).attach(this);
    }

    @Override
    public default Rec<K, V> drop(final K key) {
        if ((this.isInstance() || this.isType()) && !key.isReference()) {
            this.java().remove(key);
            return this;
        } else
            return (Rec<K, V>) DropInst.<K, V>create(key).attach(this);
    }

    @Override
    public default V get(final K key) {
        /*if (null == this.get())
            return GetInst.<K, V>create(key).attach(this);*/ // TODO: decide on what a rec.all pattern match is first
        final PMap<K, V> object = this.get();
        V v = object.getOrDefault(key, (V) TObj.none());
        if (null != v.label())  // TODO: this is ghetto---need a general solution
            v = v.state().write(v);
        return v.copy(this);
    }

    public default V get(final Object index) {
        return this.get((K) ObjectHelper.create(TInt.of(), index));
    }

    @Override
    public default <O extends Obj> O as(final O obj) {
        final Rec<K, V> map = TRec.of(Map.of());
        for (final Map.Entry<K, V> entry : ((Rec<K, V>) obj).java().entrySet()) {
            final V value = this.get(entry.getKey()).as(entry.getValue());
            if (value.q().isZero())
                return obj.q(obj.q().zero());
            map.put(entry.getKey(), value);
        }
        return map.symbol(obj.symbol()).access(obj.access()).label(obj.label());
    }
}
