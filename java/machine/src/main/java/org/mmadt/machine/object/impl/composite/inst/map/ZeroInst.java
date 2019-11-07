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
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.inst.util.InstructionHelper;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.algebra.WithDiv;
import org.mmadt.machine.object.model.type.algebra.WithZero;
import org.mmadt.machine.object.model.util.ObjectHelper;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class ZeroInst<S extends WithZero<S>> extends TInst implements MapInstruction<S, S> {

    private ZeroInst() {
        super(PList.of(Tokens.ZERO));
    }

    public S apply(final S obj) {
        return obj.zero();
    }

    public static <S extends WithZero<S>> S create(final S obj, final S zero) {
        return InstructionHelper.<S>rewrite(obj, new ZeroInst<>()).orElse(
                ObjectHelper.allInstances(obj) ? // zero is a constant
                        zero :
                        obj.q().constant() ?
                                zero.q(obj.q()) :
                                zero.q(obj.q()).append(new ZeroInst<>()));
    }

    public static <S extends WithZero<S>> ZeroInst<S> create() {
        return new ZeroInst<>();
    }
}
