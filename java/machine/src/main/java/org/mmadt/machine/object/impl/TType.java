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

package org.mmadt.machine.object.impl;

import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Type;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.type.PMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TType implements Type {

    private static Map<String, Type> BASE_TYPE_CACHE = new HashMap<>();

    private String symbol;                     // the symbol denoting objects of this type (e.g. bool, int, person, etc.)
    private Inst access;                       // access to its physical representation
    private PMap<Inst, Inst> instructions;     // rewrite rules for the vm instruction set (typically types)
    private PMap<Obj, Obj> members;

    public static Type of(final String symbol) {
        return BASE_TYPE_CACHE.computeIfAbsent(symbol, TType::new);
    }

    private TType(final String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String symbol() {
        return this.symbol;
    }

    @Override
    public Type symbol(final String symbol) {
        final TType clone = this.clone();
        clone.symbol = symbol;
        return clone;
    }

    @Override
    public Type access(final Inst access) {
        final TType clone = this.clone();
        clone.access = access;
        return clone;
    }

    @Override
    public Inst access() {
        return null == this.access ? TInst.none() : this.access;
    }

    @Override
    public PMap<Obj, Obj> members() {
        return this.members;
    }

    @Override
    public Type member(final Obj name, final Obj value) {
        final TType clone = this.clone();
        clone.members = new PMap<>();
        if (null != this.members)
            clone.members.putAll(this.members);
        clone.members.put(name, value);
        return clone;
    }

    @Override
    public Type inst(final Inst instA, final Inst instB) {
        final TType clone = this.clone();
        clone.instructions = new PMap<>();
        if (null != this.instructions)
            clone.instructions.putAll(this.instructions);
        clone.instructions.put(instA, instB);
        return clone;
    }

    @Override
    public PMap<Inst, Inst> instructions() {
        return this.instructions;
    }

    @Override
    public Type insts(final PMap<Inst, Inst> insts) {
        if (null == insts)
            return this;
        final TType clone = this.clone();
        clone.instructions = new PMap<>(insts);
        return clone;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.access, this.instructions);
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof Type &&
                Objects.equals(this.access(), ((TType) object).access()) &&
                Objects.equals(this.instructions, ((TType) object).instructions);

    }

    @Override
    public TType clone() {
        try {
            final TType clone = (TType) super.clone();
            return clone;
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
