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
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.composite.util.PMap;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.StringFactory;

import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TRec<K extends Obj, V extends Obj> extends TObj implements Rec<K, V> {

    private TRec(final Object value) {
        super(value);
    }

    public static Rec<?, ?> some() {
        return new TRec<>(null);
    }

    public static Rec<?, ?> all() {
        return new TRec<>(null).q(0, Integer.MAX_VALUE);
    }

    public static Rec<?, ?> none() {
        return new TRec<>(null).q(0);
    }

    public static <K extends Obj, V extends Obj> Rec<K, V> of(final PMap<K, V> map) {
        return new TRec<>(map);
    }

    public static <K extends Obj, V extends Obj> Rec<K, V> of(final Object... objects) {
        if (objects.length > 0 && Stream.of(objects).allMatch(x -> x instanceof Rec)) {
            return ObjectHelper.make(TRec::new, objects);
        } else if (objects.length == 1) {
            final PMap<K, V> map = new PMap<>();
            for (final Map.Entry<K, V> entry : ((Map<K, V>) objects[0]).entrySet()) {
                final K key = (K) ObjectHelper.from(entry.getKey());
                final V value = (V) ObjectHelper.from(entry.getValue());
                map.put(key, value);
            }
            return new TRec<>(map);
        } else {
            final PMap<K, V> map = new PMap<>();
            for (int i = 0; i < objects.length; i = i + 2) {
                final K key = (K) ObjectHelper.from(objects[i]);
                final V value = (V) ObjectHelper.from(objects[i + 1]);
                map.put(key, value);
            }
            return new TRec<>(map);
        }
    }

    @Override
    public Rec<K, V> label(final String variable) {
        return super.label(variable);
    }

    @Override
    public Rec<K, V> zero() {
        return !this.isReference() ? this.set(PMap.of()) : ZeroInst.<Rec<K, V>>create().attach(this);
    }

    @Override
    public Rec<K, V> plus(final Rec<K, V> rec) {
        return PlusInst.compute(this, rec);
    }

    @Override
    public Rec<K, V> minus(final Rec<K, V> rec) {
        if (!this.isReference() && !rec.isReference()) {
            final PMap<K, V> map = new PMap<>(this.java());
            rec.java().forEach(map::remove);
            return this.set(map);
        } else
            return MinusInst.<Rec<K, V>>create(rec).attach(this);
    }

    @Override
    public Rec<K, V> neg() {
        return this; // this.isInstance() ? this : this.append(NegInst.create()); // TODO: if its an identity then you don't need [neg] appendage
    }

    @Override
    public Bool eq(final Obj obj) {
        return this.isInstance() ?
                TBool.via(this).set(obj instanceof Rec && this.java().equals(((Rec) obj).java())) :
                EqInst.create(obj).attach(this, TBool.via(this));
    }

    @Override
    public String toString() {
        return StringFactory.record(this);
    }
}
