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

package org.mmadt.machine.object.model;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.composite.inst.map.AsInst;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Sym<E extends Obj> extends Obj {

    // TODO: IllegalStateException all Obj methods save label() and (I believe) symbol().

    /*
     * This obj should be used for x and for person. One being a variable and the other being an extended type
     */

    /**
     * The provided {@link Obj}'s {@link Model} is accessed to determine if the model's associated binding
     * is equal to the obj. If not, the {@code obj{0}}. If the model doesn't have an associated binding,
     * then the provided {@code obj} updates the model.
     */
    public default E match(final E obj) {
        if (!obj.isLabeled())
            return obj;
        else if (obj.isReference()) {
            final E clone = obj.model(obj.model().write(obj));
            return AsInst.<E>create(clone.access(null)).attach(clone);
        } else {
            final E history = obj.model().read(this);
            if (null == history)
                return obj.model(obj.model().write(obj)); // if the variable is unbound, bind it to the current obj
            else
                return obj.test(history) ? obj : obj.kill(); // test if the current obj is subsumed by the historic obj (if not, drop the obj's quantity to [zero])
        }
    }

    public default E obj(final Obj accessor) {
        final E obj = accessor.model().read(this);
        return null == obj ? (E) TObj.none() : obj;
    }

    // TODO: we need to decide when a symbol is dereferenced: at construction, at access, at use?
    public static Obj fetch(final Model model, final Obj obj) {
        return obj.isSym() ? model.readOrGet(obj, obj) : obj;
    }
}
