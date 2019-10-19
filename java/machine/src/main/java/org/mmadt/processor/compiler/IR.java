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

package org.mmadt.processor.compiler;

import org.mmadt.machine.object.impl.TModel;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.processor.function.QFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * A Java representation of mm-ADT bytecode.
 * The bytecode instructions are represented as functions.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class IR<S extends Obj, E extends Obj> implements Cloneable {

    private final Inst bytecode;
    private final List<QFunction> functions;

    public IR(final Inst bytecode) {
        this(TModel.of("ex"), bytecode);
    }

    public IR(final Model model, final Inst bytecode) {
        this.bytecode = bytecode;
        this.functions = new ArrayList<>();
        for (final Inst inst : bytecode.iterable()) {
            this.functions.add(FunctionTable.function(model, inst));
        }
    }

    public List<QFunction> functions() {
        return this.functions;
    }

    public Inst bytecode() {
        return this.bytecode;
    }

    public IR<S, E> clone() {
        try {
            return (IR<S, E>) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public int hashCode() {
        return this.bytecode.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof IR && ((IR) object).bytecode.equals(this.bytecode);
    }

    @Override
    public String toString() {
        return this.functions.toString();
    }
}
