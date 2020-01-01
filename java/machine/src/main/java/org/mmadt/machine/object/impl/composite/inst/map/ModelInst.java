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

package org.mmadt.machine.object.impl.composite.inst.map;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TModel;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.impl.composite.inst.filter.IdInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.composite.util.PList;

import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class ModelInst<S extends Obj, E extends Obj> extends TInst<S, E> implements MapInstruction<S, E> {

    private ModelInst(final String model, final Object symbols, final Object inst) {
        super(PList.of(Tokens.EQUALS, model, symbols, inst));
    }

    @Override
    public E apply(final S obj) {
        return (E) obj.model(TModel.of(this.<Rec<Obj, Obj>>args().get(1).get(), this.<Inst>args().get(2))).model().apply(obj);
    }

    public static <S extends Obj, E extends Obj> ModelInst<S, E> create(final Object... objects) {
        final List<Object> parameters = List.of(objects);
        final String name = parameters.get(0).toString();
        final Object bindings;
        final Object instruction;
        if (parameters.size() == 3) {
            bindings = parameters.get(1);
            instruction = parameters.get(2);
        } else if (parameters.size() == 2) {
            bindings = parameters.get(1) instanceof Inst ? TRec.of() : parameters.get(1);
            instruction = parameters.get(1) instanceof Inst ? parameters.get(1) : IdInst.create();
        } else {
            bindings = TRec.of();
            instruction = IdInst.create();
        }
        return new ModelInst<>(name, bindings, instruction);
    }
}
