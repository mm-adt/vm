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
import org.mmadt.object.model.type.Pattern;
import org.mmadt.object.model.type.algebra.WithProduct;
import org.mmadt.object.model.type.algebra.WithGroupPlus;

import java.util.List;

/**
 * A Java representation of the {@code lst} object in mm-ADT.
 * A {@code lst} is a semigroup over +.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Lst<V extends Obj> extends WithGroupPlus<Lst<V>>, WithProduct<Int,V> {

    public void add(final Int index, final V value);

    public void add(final V value);

    @Override
    public Lst<V> put(final Int index, final V value);

    @Override
    public Lst<V> drop(final Int index);

    @Override
    public default V get(final Int index) {
        V v = (V) TObj.none();
        final Object object = this.peak().get();
        if (object instanceof PList)
            v = (((PList<V>) object).size() <= index.<Integer>get()) ? (V) TObj.none() : ((PList<V>) object).get(index.get());
        else if (object instanceof PAnd) {
            final List<Pattern> ps = ((PAnd) object).predicates(); // go backwards as recent AND has higher precedence
            for (int i = ps.size() - 1; i >= 0; i--) {
                final Pattern p = ps.get(i);
                if (p instanceof Lst) {
                    v = ((Lst<V>) p).get(index);
                    if (!TObj.none().equals(v)) break;
                } else if (p instanceof TSym) {
                    final Lst<V> temp = ((TSym<Lst<V>>) p).getObject();
                    if (null != temp) {
                        v = temp.get(index);
                        if (!TObj.none().equals(v)) break;
                    }
                }
            }
        }
        return v;
    }

    @Override
    public default Lst<V> bind(final Bindings bindings) {
        return (Lst<V>) WithProduct.super.bind(bindings);
    }

    @Override
    public default Iterable<? extends Lst> iterable() {
        return (Iterable<? extends Lst>) WithProduct.super.iterable();
    }
}
