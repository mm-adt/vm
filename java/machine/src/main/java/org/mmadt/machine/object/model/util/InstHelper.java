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
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.util.PList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class InstHelper {

    private InstHelper() {
        // static help class
    }

    public static List<Object> domainRangeNested(final Inst bytecode) {
        final List<Object> list = new ArrayList<>();
        boolean first = true;
        for (final Inst inst : bytecode.iterable()) {
            if (first) {
                first = false;
                list.add(inst.domain().access(null));
            }
            final List<Object> oneDeep = new ArrayList<>();
            for (final Obj arg : inst.args()) {
                if (!arg.access().isOne()) {
                    oneDeep.add(domainRangeNested(arg.access()));
                }
            }
            if (!oneDeep.isEmpty())
                list.add(oneDeep);
            list.add(inst.range().access(null));
        }
        return list;
    }

    public static Inst first(final Inst inst) {
        return singleInst(inst) ? inst : inst.<PList<Inst>>get().get(0);
    }

    public static Inst last(final Inst inst) {
        return singleInst(inst) ? inst : inst.<PList<Inst>>get().get(inst.<PList<Inst>>get().size() - 1);
    }

    public static boolean singleInst(final Inst inst) {
        return inst.<PList>get().get(0) instanceof Str;
    }

    public static List<Inst> list(final List<Inst> list) {
        final PList<Inst> temp = new PList<>();
        temp.addAll(list);
        return temp;
    }

    public static List<Inst> chain(final Inst instA, final Inst instB) {
        final PList<Inst> list = new PList<>();
        if (singleInst(instA))
            list.add(instA);
        else
            list.addAll(instA.<PList<Inst>>get());
        if (singleInst(instB))
            list.add(instB);
        else
            list.addAll(instB.<PList<Inst>>get());
        return list;

    }
}
