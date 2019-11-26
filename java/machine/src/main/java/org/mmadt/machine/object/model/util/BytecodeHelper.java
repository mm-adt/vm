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

package org.mmadt.machine.object.model.util;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;

import java.util.ArrayList;
import java.util.List;

import static org.mmadt.machine.object.impl.composite.TInst.ID;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class BytecodeHelper {

    private BytecodeHelper() {
        // static help class
    }

    public static Obj reference(final Inst ref) {
        return ref.<List<Obj>>get().get(1);
    }

    public static List<Object> domainRangeNested(final Inst bytecode) {
        final List<Object> list = new ArrayList<>();
        boolean first = true;
        for (final Inst inst : bytecode.iterable()) {
            if (first) {
                first = false;
                list.add(inst.domain().access(ID()));
            }
            final List<Object> oneDeep = new ArrayList<>();
            for (final Obj arg : inst.args()) {
                if (!arg.access().isOne()) {
                    oneDeep.add(domainRangeNested(arg.access()));
                }
            }
            if (!oneDeep.isEmpty())
                list.add(oneDeep);
            list.add(inst.range().access(ID()));
        }
        return list;
    }
}
