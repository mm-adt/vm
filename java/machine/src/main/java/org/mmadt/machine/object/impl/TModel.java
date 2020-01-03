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
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.processor.util.FastProcessor;
import org.mmadt.util.IteratorUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mmadt.machine.object.impl.__.a;
import static org.mmadt.machine.object.impl.__.choose;
import static org.mmadt.machine.object.impl.__.is;
import static org.mmadt.machine.object.impl.__.map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TModel implements Model {

    private Obj machine = null;
    private Map<Obj, Obj> bindings = new LinkedHashMap<>();

    public static Model of(final Map<Obj, Obj> state) {
        final TModel temp = new TModel();
        state.forEach((x, y) -> temp.bindings.put(TStr.of(x.label()), map(y)));
        temp.machine = choose(TRec.of(temp.bindings));
        return temp;
    }

    @Override
    public Obj apply(final Obj obj) {
        return null == this.machine ? TObj.none() : IteratorUtils.orElse(FastProcessor.process(TStr.of(obj.label()).mapTo(this.machine)), TObj.none());
    }

    @Override
    public Model write(final Obj value) {
        final TModel clone = (TModel) this.clone();
        clone.bindings.put(TStr.of(value.label()), map(value));
        clone.machine = choose(TRec.of(clone.bindings));
        return clone;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Model && ((TModel) other).bindings.equals(this.bindings);
    }

    @Override
    public int hashCode() {
        return this.bindings.hashCode();
    }

    @Override
    public String toString() {
        return this.bindings.values().toString();
    }

    @Override
    public Model clone() {
        try {
            final TModel clone = (TModel) super.clone();
            clone.bindings = new LinkedHashMap<>(this.bindings);
            return clone;
        } catch (final CloneNotSupportedException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
