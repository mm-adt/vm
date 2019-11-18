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

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.inst.filter.IdInst;
import org.mmadt.machine.object.model.Type;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.type.Pattern;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TType implements Type {

    protected static Map<String, Type> BASE_TYPE_CACHE = new HashMap<>();

    // private Model model = Model.MODELS.get("mm").get();
    private String symbol;                     // the symbol denoting objects of this type (e.g. bool, int, person, etc.)
    private String label;                      // the ~bind string (if retrieved via a bind)
    private Pattern pattern;                   // a predicate for testing an instance of the type
    private Inst accessTo;                     // accessTo to the manifestations of this form
    private Inst accessFrom;                   // accessFrom to the manifestations of this form
    private PMap<Inst, Inst> instructions;     // rewrite rules for the vm instruction set (this is the "FPGA" aspect of the VM)

    public static Type of(final String symbol) {
        return BASE_TYPE_CACHE.computeIfAbsent(symbol, TType::new);
    }

    private TType(final String symbol) {
        this.symbol = symbol;
    }

    /*@Override
    public Model model() {
        return this.model;
    }

    @Override
    public Type model(final Model model) {
        final TType clone = this.clone();
        clone.model = model;
        return clone;
    }*/

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
    public Pattern pattern() {
        return this.pattern;
    }

    @Override
    public Type pattern(final Pattern pattern) {
        final TType clone = this.clone();
        clone.pattern = pattern;
        return clone;
    }

    @Override
    public String label() {
        return this.label;
    }

    @Override
    public Type label(final String label) {
        final TType clone = this.clone();
        clone.label = label;
        return clone;
    }

    @Override
    public Type accessFrom(final Inst access) {
        final TType clone = this.clone();
        clone.accessFrom = null == access || (access.get() instanceof PList && access.<PList<Str>>get().get(0).java().equals(Tokens.ID)) ? null : access;
        return clone;
    }

    @Override
    public Type accessTo(final Inst access) {
        final TType clone = this.clone();
        clone.accessTo = null == access || (access.get() instanceof PList && access.<PList<Str>>get().get(0).java().equals(Tokens.ID)) ? null : access;
        return clone;
    }

    @Override
    public Inst accessFrom() {
        return null == this.accessFrom ? IdInst.create() : this.accessFrom;
    }

    @Override
    public Inst accessTo() {
        return null == this.accessTo ? IdInst.create() : this.accessTo;
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
        return Objects.hash(this.symbol, this.label, this.accessFrom, this.accessTo, this.instructions, this.pattern);
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof Type &&
                Objects.equals(this.symbol, ((Type) object).symbol()) &&
                Objects.equals(this.accessFrom(), ((Type) object).accessFrom()) &&
                Objects.equals(this.accessTo(), ((Type) object).accessTo()) &&
                Objects.equals(this.label, ((Type) object).label()) &&
                Objects.equals(this.instructions, ((Type) object).instructions()) &&
                Objects.equals(this.pattern, ((Type) object).pattern());
    }

    @Override
    public TType clone() {
        try {
            return (TType) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
