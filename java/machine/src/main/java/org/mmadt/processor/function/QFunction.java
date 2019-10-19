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

package org.mmadt.processor.function;

import org.mmadt.object.model.composite.Q;

import java.io.Serializable;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface QFunction extends Serializable, Cloneable {

    public Q quantifier();

    public String label();

    public QFunction clone();

    // TODO: reset() -- some functions do have state that needs resetting. E.g. nested compilation functions.
    //                  most internal compilations reset() the compilation on every traverser insert. Except branch compilations.
}