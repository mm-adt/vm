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

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Pattern;
import org.mmadt.machine.object.model.Type;
import org.mmadt.machine.object.model.composite.Inst;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TType implements Type {

    protected static Map<String, Type> BASE_TYPE_CACHE = new HashMap<>();

    private String symbol;                     // the label denoting objects of this type (e.g. bool, int, person, etc.)
    private Pattern pattern;                   // a predicate for testing an instance of the type
    private Inst access;                       // access to the manifestations of this form

    public static Type of(final String symbol) {
        return BASE_TYPE_CACHE.computeIfAbsent(symbol, TType::new);
    }

    private TType(final String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String label() {
        return this.symbol;
    }

    @Override
    public Type label(final String symbol) {
        final TType clone = this.clone();
        if (this.pattern instanceof Obj && !(((Obj) this.pattern).isInst()))
            clone.pattern = ((Obj) this.pattern).label(symbol);
        else
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
    public Type access(final Inst access) {
        final TType clone = this.clone();
        clone.access = null == access || access.opcode().java().equals(Tokens.ID) ? null : access;
        return clone;
    }

    @Override
    public Inst access() {
        return this.access;
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.label(), this.access, this.pattern);
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof Type &&
                Objects.equals(this.label(), ((Type) object).label()) &&
                Objects.equals(this.access(), ((Type) object).access());// &&
        // Objects.equals(this.pattern, ((Type) object).pattern());
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
