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

package org.mmadt.machine.object.impl.composite;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.composite.inst.map.EqInst;
import org.mmadt.machine.object.impl.composite.inst.map.MinusInst;
import org.mmadt.machine.object.impl.composite.inst.map.PlusInst;
import org.mmadt.machine.object.impl.composite.inst.map.ZeroInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.StringFactory;

import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TLst<V extends Obj> extends TObj implements Lst<V> {

    private TLst(final Object value) {
        super(value);
    }

    public static Lst all() {
        return new TLst<>(null).q(0, Integer.MAX_VALUE);
    }

    public static Lst none() {
        return new TLst<>(null).q(0);
    }

    public static Lst some() {
        return new TLst<>(null);
    }

    public static <V extends Obj> Lst<V> of(final Object... objects) {
        if (objects.length > 0 && (objects[0] instanceof Lst || objects[0] instanceof List)) {
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
    public Lst<V> zero() {
        return !this.isReference() ? this.set(PList.of()) : ZeroInst.<Lst<V>>create().attach(this);
    }

    @Override
    public Lst<V> plus(final Lst<V> lst) {
        if (lst.isInstance()) {
            final PList<V> list = new PList<>(this.java());
            list.addAll(lst.java());
            return this.set(list);
        } else
            return PlusInst.<Lst<V>>create(lst).attach(this);
    }

    @Override
    public Lst<V> minus(final Lst<V> lst) {
        if (this.isInstance() && lst.isInstance()) {
            final PList<V> list = new PList<>(this.java());
            list.removeAll(lst.java());
            return this.set(list);
        } else
            return MinusInst.<Lst<V>>create(lst).attach(this);
    }

    @Override
    public Lst<V> neg() {
        return this; // this.isInstance() ? this : this.append(NegInst.create()); // TODO: if its an identity then you don't need [neg] appendage
    }

    @Override
    public Bool eq(final Obj obj) {
        return this.isInstance() ?
                TBool.via(this).set(obj instanceof Lst && this.java().equals(((Lst) obj).java())) :
                EqInst.create(obj).attach(this, TBool.via(this));
    }

    @Override
    public String toString() {
        return StringFactory.list(this);
    }


}
