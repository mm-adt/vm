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
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.DropInst;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.PutInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.type.Bindings;
import org.mmadt.machine.object.model.type.PAnd;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.Pattern;
import org.mmadt.machine.object.model.type.algebra.WithGroupPlus;
import org.mmadt.machine.object.model.type.algebra.WithProduct;
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
            return this.mapTo(PutInst.create(TInt.of(100), value)); // TODO: should have isolated put(value) (like add(value))
    }

    @Override
    public default Lst<V> put(final Int index, final V value) {
        if (this.isInstance() || this.isType()) {
            this.java().add(index.java(), value);
            return this;
        } else
            return this.mapTo(PutInst.create(index, value));
    }

    @Override
    public default Lst<V> drop(final Int index) {
        if (this.isInstance() || this.isType()) {
            this.java().remove((int) index.java());
            return this;
        } else
            return this.mapTo(DropInst.create(index));
    }

    @Override
    public default V get(final Int index) {
        V v = (V) TObj.none();
        final Object object = this.get();
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
    public default Iterable<Lst<V>> iterable() {
        return this.isInstance() ? List.of(this) : () -> new FastProcessor<Lst<V>>().iterator(this);
    }

}
