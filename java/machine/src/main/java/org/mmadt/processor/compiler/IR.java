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

/**
 * A Java representation of mm-ADT bytecode.
 * The bytecode instructions are represented as functions.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class IR<S extends Obj, E extends Obj> {

    private final Inst bytecode;

    public IR(final Inst bytecode) {
        this(TModel.of("ex"), bytecode);
    }

    public IR(final Model model, final Inst bytecode) {
        this.bytecode = bytecode;
    }

    public Inst bytecode() {
        return this.bytecode;
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
        return this.bytecode.toString();
    }
}