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

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.State;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.processor.util.FastProcessor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class TState implements State {

    private Map<String, Obj> objs = new LinkedHashMap<>();
    private final Inst insts = null;

    @Override
    public Inst apply(final Inst inst) {
        return null == this.insts ? null : (Inst) FastProcessor.process(inst.access(this.insts)).next();
    }

    @Override
    public <O extends Obj> O read(final Obj key) {
        return (O) this.objs.getOrDefault(key.label(), TObj.none());
    }

    @Override
    public State write(final Obj value) {
        final TState clone = (TState) this.clone();
        clone.objs.put(value.label(), value);
        return clone;
    }

    @Override
    public String toString() {
        return this.objs.values().toString();
    }

    @Override
    public State clone() {
        try {
            final TState clone = (TState) super.clone();
            clone.objs = new LinkedHashMap<>(this.objs);
            return clone;
        } catch (final CloneNotSupportedException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
