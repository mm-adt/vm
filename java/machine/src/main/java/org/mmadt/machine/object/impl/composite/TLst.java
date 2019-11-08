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

package org.mmadt.machine.object.impl.composite;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.composite.inst.map.EqInst;
import org.mmadt.machine.object.impl.composite.inst.map.MinusInst;
import org.mmadt.machine.object.impl.composite.inst.map.NegInst;
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

    private static final Lst SOME = new TLst<>(null);
    private static final Lst NONE = new TLst<>(null).q(0);
    private static final Lst ALL = new TLst<>(null).q(0, Integer.MAX_VALUE);

    private TLst(final Object value) {
        super(value);
    }

    public static Lst all() {
        return ALL;
    }

    public static Lst none() {
        return NONE;
    }

    public static Lst some() {
        return SOME;
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
        return ZeroInst.create(this, TLst.of());
    }

    @Override
    public Lst<V> plus(final Lst<V> lst) {
        if (this.isInstance() && lst.isInstance()) {
            final PList<V> list = new PList<>(this.java());
            list.addAll(lst.java());
            return this.set(list);
        } else
            return this.append(PlusInst.create(lst));
    }

    @Override
    public Lst<V> minus(final Lst<V> object) {
        return MinusInst.create(() -> {
            final PList<V> list = new PList<>(this.java());
            list.removeAll(object.java());
            return new TLst<>(list);
        }, this, object);
    }

    @Override
    public Lst<V> neg() {
        return NegInst.create(() -> this, this); // TODO: What is a -list?
    }

    @Override
    public Bool eq(final Obj obj) {
        return EqInst.create(() -> TBool.of(obj instanceof Lst && this.java().equals(((Lst) obj).java())), this, obj);
    }

    @Override
    public String toString() {
        return StringFactory.list(this);
    }


}
