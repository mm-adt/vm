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

package org.mmadt.machine.object.impl;

import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TModel implements Model {

    public Map<Obj, Obj> bindings = null;

    public static Model of(final Map<Obj, Obj> state) {
        final TModel model = new TModel();
        state.forEach((x, y) -> model.bindings().put(TStr.of(x.binding()), y));
        return model;
    }

    @Override
    public Obj apply(final Obj obj) {
        return this.bindings().getOrDefault(TStr.of(obj.binding()), TObj.none());
    }

    @Override
    public Model write(final Obj value) {
        final TModel clone = (TModel) this.clone();
        clone.bindings().put(TStr.of(value.binding()), value);
        return clone;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Model && Objects.equals(this.bindings(), ((Model) other).bindings());
    }

    @Override
    public int hashCode() {
        return this.bindings().hashCode();
    }

    @Override
    public String toString() {
        return this.bindings().values().toString();
    }

    @Override
    public Model clone() {
        try {
            final TModel clone = (TModel) super.clone();
            clone.bindings = new LinkedHashMap<>(this.bindings());
            return clone;
        } catch (final CloneNotSupportedException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public Map<Obj, Obj> bindings() {
        if (null == this.bindings) {
            this.bindings = new LinkedHashMap<>();
            STORAGES.forEach(storage -> this.bindings.put(TStr.of(storage.name()), storage.open()));
        }
        return this.bindings;
    }
}
