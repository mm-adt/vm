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

package org.mmadt.object.model;

import org.mmadt.object.impl.TSym;
import org.mmadt.object.model.composite.Inst;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Model extends Cloneable {

    public String symbol();

    public <A extends Obj> TSym<A> sym(final String symbol);

    public void define(final String symbol, final Obj object);

    public boolean has(final String symbol);

    public <A extends Obj> A get(final String symbol);

    public Inst query(final Inst bytecode);

    public Model model(final Inst bytecode);

    public Model clone();

}
