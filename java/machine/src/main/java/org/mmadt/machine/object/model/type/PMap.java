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

package org.mmadt.machine.object.model.type;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.util.StringFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class PMap<K extends Obj, V extends Obj> extends LinkedHashMap<K, V> implements Pattern {

    private static final PMap<Obj, Obj> EMPTY_MAP = new PMap<>(0);

    public PMap() {
        super();
    }

    public PMap(final int size) {
        super(size);
    }

    public PMap(final Map<K, V> map) {
        super(map);
    }

    public static <K extends Obj, V extends Obj> PMap<K, V> of() {
        return (PMap<K, V>) EMPTY_MAP;
    }

    @Override
    public boolean test(final Obj object) {
        if (!(object.get() instanceof Map))
            return false;
        final Map<K, V> other = object.get();
        for (final Map.Entry<K, V> entry : this.entrySet()) {
            final Obj otherValue = other.getOrDefault(entry.getKey(), (V) TObj.none());
            if (null != otherValue.type() && otherValue.type().symbol().equals(entry.getValue().symbol()))
                continue;
            if (!entry.getValue().test(otherValue))
                return false;
        }
        return true;
    }

    @Override
    public boolean match(final Bindings bindings, final Obj object) {
        bindings.start();
        final TRec<K, V> other = object.asObj();
        for (final Map.Entry<K, V> entry : this.entrySet()) {
            final V thisValue = entry.getValue();
            final Obj otherValue = other.get(entry.getKey());
            if (null != otherValue.type() && otherValue.type().symbol().equals(thisValue.symbol())) {
                if (null != thisValue.label())
                    bindings.put(thisValue.label(), otherValue);
            } else if (!thisValue.match(bindings, otherValue)) {
                bindings.rollback();
                return false;
            }
        }
        bindings.commit();
        return true;
    }

    @Override
    public String toString() {
        return StringFactory.map(this);
    }

    @Override
    public final boolean constant() {
        for (final Map.Entry<? extends Obj, ? extends Obj> entry : this.entrySet()) {
            if (!entry.getValue().constant() || !entry.getKey().constant() || entry.getValue() instanceof Inst || entry.getKey() instanceof Inst)
                return false;
        }
        return true;
    }

    @Override
    public PMap<K, V> bind(Bindings bindings) {
        final PMap<K, V> newMap = new PMap<>();
        for (final Map.Entry<K, V> entry : this.entrySet()) {
            newMap.put((K) entry.getKey().bind(bindings), (V) entry.getValue().bind(bindings));
        }
        return newMap;

    }
}
