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

package org.mmadt.machine.object.impl.composite.inst.model;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.type.PList;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class ObjInst<S extends Obj, E extends Obj> extends TInst implements MapInstruction<S, E> {


    private final String name;


    private ObjInst(final String name) {
        super(PList.of(Tokens.EQUALS.concat(name)));
        this.name = name;
    }

    public E apply(final S obj) {
        final Model model = Model.MODELS.get(this.name).get();
        if (model.has(this.name))
            return model.get(this.name).access(ObjInst.create(this.name));
        else
            return (E) obj;
    }

    public static <S extends Obj, E extends Obj> ObjInst<S, E> create(final String name) {
        return new ObjInst<>(name);
    }
}
