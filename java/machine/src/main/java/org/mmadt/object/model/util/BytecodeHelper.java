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

package org.mmadt.object.model.util;

import org.mmadt.object.model.Model;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.composite.Inst;
import org.mmadt.object.model.type.Bindings;

import java.util.ArrayList;
import java.util.List;

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

    public static boolean isSubset(final Inst subBc, final Inst supBc) {
        final List<Inst> sub = new ArrayList<>();
        final List<Inst> sup = new ArrayList<>();
        subBc.iterable().forEach(sub::add);
        supBc.iterable().forEach(sup::add);
        if (sup.size() < sub.size())
            return false;
        else {
            for (int i = 0; i < sub.size() - 1; i++) {
                if (!sub.get(i).test(sup.get(i)))
                    return false;
            }
            return true;
        }
    }

    public static Inst inst(final Bindings bindings, final Model model, final Inst inst, final Obj currentType) {
        return currentType.inst(bindings, inst).orElse(model.has(currentType.symbol()) ? model.get(currentType.symbol()).inst(bindings, inst).orElse(inst.bind(bindings)) : inst.bind(bindings));
    }

    public static List<Object> domainRangeNested(final Inst bytecode) {
        final List<Object> list = new ArrayList<>();
        boolean first = true;
        for (final Inst inst : bytecode.iterable()) {
            if (first) {
                first = false;
                list.add(inst.domain());
            }
            final List<Object> oneDeep = new ArrayList<>();
            for (final Obj arg : inst.args()) {
                if (arg instanceof Inst) {
                    oneDeep.add(domainRangeNested((Inst) arg));
                }
            }
            if (!oneDeep.isEmpty())
                list.add(oneDeep);
            list.add(inst.range());
        }
        return list;
    }

}
