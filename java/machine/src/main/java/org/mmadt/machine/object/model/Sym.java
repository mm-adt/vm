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

import org.mmadt.machine.object.impl.composite.inst.map.AsInst;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Sym extends Obj {

    // TODO: IllegalStateException all Obj methods save label() and (I believe) symbol().

    /*
     * This obj should be used for x and for person. One being a variable and the other being an extended type
     */

    public default <O extends Obj> O process(final O obj) {
        if (obj.isReference()) {
            // final O history = obj.state().read(this);
            final O clone = obj.model(obj.model().write(obj));
            return AsInst.<O>create(clone.access(null)).attach(clone);
        } else {
            final O history = obj.model().read(this);
            if (null == history)
                return obj.model(obj.model().write(obj)); // if the variable is unbound, bind it to the current obj
            else
                return obj.test(history) ? obj : obj.kill(); // test if the current obj is subsumed by the historic obj (if not, drop the obj's quantity to [zero])
        }
    }

}
