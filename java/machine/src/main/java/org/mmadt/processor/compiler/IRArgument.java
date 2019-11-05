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

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.processor.util.FastProcessor;
import org.mmadt.processor.util.MinimalProcessor;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class IRArgument<S extends Obj, E extends Obj> implements Argument<S, E> {

    private final Inst bytecode;

    public IRArgument(final Inst bytecode) {
        this.bytecode = bytecode;
    }

    @Override
    public E mapArg(final S object) {
        return new MinimalProcessor<S, E>(this.bytecode).iterator(object).next();
    }

    @Override
    public Iterator<E> flatMapArg(final S object) {
        return new MinimalProcessor<S, E>(this.bytecode).iterator(object);
    }

    @Override
    public boolean filterArg(final S object) {
        return new MinimalProcessor<S, E>(this.bytecode).iterator(object).hasNext(); // TODO: this should not be hardcoded to a processor
    }

    @Override
    public IRArgument<S, E> clone() {
        try {
            return (IRArgument<S, E>) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return new IR<>(this.bytecode).toString();
    }
}
