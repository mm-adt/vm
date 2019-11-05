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

package org.mmadt.machine.object.impl.composite.inst.util;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.type.Bindings;

import java.util.Optional;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class InstructionHelper {

    private InstructionHelper() {
        // static helper class
    }

    public static <S extends Obj> Optional<S> rewrite(final Obj obj, final Inst inst) {
        final Optional<Inst> optional = obj.inst(new Bindings(), inst); // in the future, bindings with path data
        if (optional.isPresent()) {
            S temp = (S) obj;
            for (Inst i : optional.get().iterable()) {
                temp = temp.append(i);
            }
            return Optional.of(temp);
        } else
            return Optional.empty();
    }
}
