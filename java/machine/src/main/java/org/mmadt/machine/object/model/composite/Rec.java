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
import org.mmadt.machine.object.impl.composite.inst.map.GetInst;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.DropInst;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.PutInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.ext.algebra.WithGroupPlus;
import org.mmadt.machine.object.model.ext.algebra.WithProduct;
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
    public Rec<K, V> label(final String variable);

    @Override
    public default Rec<K, V> put(final K key, final V value) {
        if (!this.isReference() && !key.isReference()) {// && !value.isReference()) {
            this.java().put(key, value);
            return this;
        } else
            return (Rec<K, V>) PutInst.<K, V>create(key, value).attach(this);
    }

    @Override
    public default Rec<K, V> drop(final K key) {
        if (!this.isReference() && !key.isReference()) {
            this.java().remove(key);
            return this;
        } else
            return (Rec<K, V>) DropInst.<K, V>create(key).attach(this);
    }

    @Override
    public default V get(final K key) {
        return GetInst.compute(this, key);
    }

    public default V get(final Object index) {
        return this.get(ObjectHelper.create(this, index));
    }
}
