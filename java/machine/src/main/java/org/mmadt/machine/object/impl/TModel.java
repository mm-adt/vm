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

import org.mmadt.machine.object.impl.composite.inst.filter.IdInst;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.processor.util.FastProcessor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TModel implements Model {

    private Map<String, Obj> objs = new LinkedHashMap<>();
    private Inst insts = null;

    public static Model of(final Map<String, Obj> state) {
        return TModel.of(state, IdInst.create());
    }

    public static Model of(final Map<String, Obj> state, final Inst inst) {
        final TModel temp = new TModel();
        temp.objs = new LinkedHashMap<>(state);
        temp.insts = inst;
        return temp;
    }

    @Override
    public Inst apply(final Inst inst) {
        return null == this.insts ? null : (Inst) FastProcessor.process(inst.access(this.insts)).next();
    }

    @Override
    public <O extends Obj> O read(final Obj key) {
        return (O) this.objs.get(key.label());
    }

    @Override
    public Model write(final Obj value) {
        final TModel clone = (TModel) this.clone();
        clone.objs.put(value.label(), value);
        return clone;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Model && ((TModel) other).objs.equals(this.objs);
    }

    @Override
    public int hashCode() {
        return this.objs.hashCode();
    }

    @Override
    public String toString() {
        return this.objs.values().toString();
    }

    @Override
    public Model clone() {
        try {
            final TModel clone = (TModel) super.clone();
            clone.objs = new LinkedHashMap<>(this.objs);
            return clone;
        } catch (final CloneNotSupportedException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
