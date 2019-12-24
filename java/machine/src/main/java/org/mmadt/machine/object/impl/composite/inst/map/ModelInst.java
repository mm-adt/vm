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
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Sym;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.composite.util.PList;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class ModelInst<S extends Obj, E extends Obj> extends TInst<S, E> implements MapInstruction<S, E> {

    private ModelInst(final String model, final Object symbols, final Object inst) {
        super(PList.of(Tokens.EQUALS, model, symbols, inst));
    }

    @Override
    public E apply(final S obj) {
        return obj.model(TModel.of(this.<Rec<Sym, Obj>>args().get(1).get(), this.<Inst>args().get(2)));
    }

    public static <S extends Obj, E extends Obj> ModelInst<S, E> create(final String model, final Object symbols, final Object inst) {
        return new ModelInst<>(model, symbols, inst);
    }
}
