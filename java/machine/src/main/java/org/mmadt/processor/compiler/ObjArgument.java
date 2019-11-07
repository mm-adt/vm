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

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class ObjArgument<S extends Obj, E extends Obj> implements Argument<S, E> {

    private final E constant;

    ObjArgument(final E constant) {
        this.constant = constant;
    }

    @Override
    public E mapArg(final S object) {
        return this.constant;
    }

    @Override
    public Iterator<E> flatMapArg(final S object) {
        return (Iterator<E>) this.constant.iterable().iterator();
    }

    @Override
    public boolean filterArg(final S object) {
        return (Boolean) this.constant.get();
    }

    @Override
    public int hashCode() {
        return this.constant.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof ObjArgument && this.constant.equals(((ObjArgument) object).constant);
    }

    @Override
    public String toString() {
        return this.constant.toString();
    }
}