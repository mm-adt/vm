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

package org.mmadt.processor.compiler;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Sym;

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
        return this.constant instanceof Sym ? ((Sym<E>) this.constant).obj(object) :
                object.isInstance() && !this.constant.isReference() ?
                        this.constant.copy(object).as(this.constant) :
                        this.constant;
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