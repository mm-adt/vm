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

package org.mmadt.object.model.composite;

import org.mmadt.object.impl.TObj;
import org.mmadt.object.impl.TSym;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.atomic.Int;
import org.mmadt.object.model.type.Bindings;
import org.mmadt.object.model.type.PAnd;
import org.mmadt.object.model.type.PList;
import org.mmadt.object.model.type.PMap;
import org.mmadt.object.model.type.Pattern;

import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Struct<K extends Obj, V extends Obj> extends Obj {

    public Struct<K, V> put(final K key, final V value);

    public Struct<K, V> drop(final K key);

    public default V get(final K key) {
        V v = (V) TObj.none();
        final Object object = this.peak().get();
        if (object instanceof PMap)
            v = ((PMap<K, V>) object).getOrDefault(key, (V) TObj.none());
        else if (object instanceof PList)
            v = (((PList<V>) object).size() <= key.<Integer>get()) ? (V) TObj.none() : ((PList<V>) object).get(key.get());
        else if (object instanceof PAnd) {
            final List<Pattern> ps = ((PAnd) object).predicates(); // go backwards as recent AND has higher precedence
            for (int i = ps.size() - 1; i >= 0; i--) {
                final Pattern p = ps.get(i);
                if (p instanceof Rec) {
                    v = ((Rec<K, V>) p).get(key);
                    if (!TObj.none().equals(v)) break;
                } else if (p instanceof Lst) {
                    v = ((Lst<V>) p).get((Int) key);
                    if (!TObj.none().equals(v)) break;
                } else if (p instanceof TSym) {
                    final Struct<K, V> temp = ((TSym<Struct<K, V>>) p).getObject();
                    if (null != temp) {
                        v = temp.get(key);
                        if (!TObj.none().equals(v)) break;
                    }
                }
            }
        }
        return v;
    }

    @Override
    public default Struct<K, V> bind(final Bindings bindings) {
        return (Struct<K, V>) Obj.super.bind(bindings);
    }

    @Override
    public default Iterable<? extends Struct> iterable() {
        return (Iterable<? extends Struct>) Obj.super.iterable();
    }
}
