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

package org.mmadt.machine.object.model;

import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.type.Pattern;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Type extends Cloneable {

    /*public Model model();

    public Type model(final Model model);*/

    public String symbol();

    public Type symbol(final String symbol);

    public Pattern pattern();

    public Type pattern(final Pattern pattern);

    public String label();

    public Type label(final String label);

    public Type accessFrom(final Inst access);

    public Type accessTo(final Inst access);

    public Inst accessFrom();

    public Inst accessTo();

    public PMap<Obj, Obj> members();

    public Type member(final Obj name, final Obj value);

    public Type inst(final Inst instA, final Inst instB);

    public Type insts(final PMap<Inst, Inst> insts);

    public PMap<Inst, Inst> instructions();

    public Type clone();
}
