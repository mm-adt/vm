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
import org.mmadt.machine.object.impl.TStream;
import org.mmadt.machine.object.impl.TType;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.StringFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mmadt.language.compiler.Tokens.REC;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TRec<K extends Obj, V extends Obj> extends TObj implements Rec<K, V> {

    private static final Rec<Obj, Obj> SOME = new TRec<>(null);
    private static final Rec<Obj, Obj> ALL = new TRec<>(null).q(0, Integer.MAX_VALUE);
    private static final Rec<Obj, Obj> NONE = new TRec<>(null).q(0);

    private TRec(final Object value) {
        super(value);
        this.types = TType.of(REC);
    }

    public static Rec<?, ?> some() {
        return SOME;
    }

    public static Rec<?, ?> all() {
        return ALL;
    }

    public static Rec<?, ?> none() {
        return NONE;
    }

    public static <K extends Obj, V extends Obj> Rec<K, V> of(final PMap<K, V> map) {
        return new TRec<>(map);
    }

    public static <K extends Obj, V extends Obj> Rec<K, V> of(final Object... objects) {
        final PMap<K, V> map = new PMap<>();
        boolean constant = true;
        for (int i = 0; i < objects.length; i = i + 2) {
            final K key = (K) ObjectHelper.from(objects[i]);
            final V value = (V) ObjectHelper.from(objects[i + 1]);
            constant = constant && key.constant() && value.constant();
            map.put(key, value);
        }
        return new TRec<>(map);

    }

    @SafeVarargs
    public static <K extends Obj, V extends Obj> Rec<K, V> of(final Rec<K, V> rec, final Rec<K, V>... records) {
        final List<Rec<K, V>> list = new ArrayList<>();
        list.add(rec);
        Collections.addAll(list, records);
        return new TRec<>(TStream.of(list));
    }

    @Override
    public Rec<K, V> put(final K key, final V value) { // TODO: put() needs to account for PAnd.
        if (null == this.value) {
            ((PMap<K, V>) this.pattern).put(key, value);
        } else
            ((PMap<K, V>) this.value).put(key, value);
        return this;
    }

    @Override
    public Rec<K, V> drop(final K key) {
        ((PMap<K, V>) this.value).remove(key);
        return this;
    }

    @Override
    public String toString() {
        return StringFactory.record(this);
    }

}
