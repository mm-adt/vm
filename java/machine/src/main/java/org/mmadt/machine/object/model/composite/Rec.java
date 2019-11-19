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

package org.mmadt.machine.object.model.composite;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.TSym;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.DropInst;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.PutInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.type.Bindings;
import org.mmadt.machine.object.model.type.PAnd;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.type.Pattern;
import org.mmadt.machine.object.model.type.algebra.WithGroupPlus;
import org.mmadt.machine.object.model.type.algebra.WithProduct;
import org.mmadt.processor.util.FastProcessor;

import java.util.List;
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
            return this.mapTo(PutInst.create(key, value));
    }

    @Override
    public default Rec<K, V> drop(final K key) {
        if ((this.isInstance() || this.isType()) && !key.isReference()) {
            this.java().remove(key);
            return this;
        } else
            return this.mapTo(DropInst.create(key));
    }

    @Override
    public default V get(final K key) {
        V v = (V) TObj.none();
        final Object object = this.get();
        if (object instanceof PMap)
            v = ((PMap<K, V>) object).getOrDefault(key, (V) TObj.none());
        else if (object instanceof PAnd) {
            final List<Pattern> ps = ((PAnd) object).predicates(); // go backwards as recent AND has higher precedence
            for (int i = ps.size() - 1; i >= 0; i--) {
                final Pattern p = ps.get(i);
                if (p instanceof Rec) {
                    v = ((Rec<K, V>) p).get(key);
                    if (!TObj.none().equals(v)) break;
                } else if (p instanceof TSym) {
                    final Rec<K, V> temp = ((TSym<Rec<K, V>>) p).getObject();
                    if (null != temp) {
                        v = temp.get(key);
                        if (!TObj.none().equals(v)) break;
                    }
                }
            }
        }
        return v;
        // return v.isType() ? v.accessFrom(TInst.of(List.of(this.accessFrom(), GetInst.create(key)))).q(this.q()) : v;
    }

    @Override
    public default Rec<K, V> bind(final Bindings bindings) {
        return (Rec<K, V>) WithProduct.super.bind(bindings);
    }

    @Override
    public default Iterable<Rec<K, V>> iterable() {
        return this.isInstance() ? List.of(this) : () -> FastProcessor.process(this);
    }
}
