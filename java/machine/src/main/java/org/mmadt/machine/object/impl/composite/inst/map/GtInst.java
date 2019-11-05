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

package org.mmadt.machine.object.impl.composite.inst.map;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.inst.util.InstructionHelper;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.algebra.WithOrder;
import org.mmadt.machine.object.model.util.ObjectHelper;

import java.util.function.Supplier;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class GtInst<S extends WithOrder<S>> extends TInst implements MapInstruction<S, Bool> {

    private GtInst(final S arg) {
        super(PList.of(Tokens.GT, arg));
    }

    public Bool apply(final S obj) {
        return obj.gt(this.<S, S>argument(0).mapArg(obj));
    }

    public static <S extends WithOrder<S>> Bool create(final Supplier<Bool> compute, final S obj, final S arg) {
        return InstructionHelper.<Bool>rewrite(obj, new GtInst<>(arg)).orElse(
                ObjectHelper.allInstances(obj, arg) ?
                        compute.get() :
                        TBool.of().q(obj.q()).append(new GtInst<>(arg)));
    }
}
