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

package org.mmadt.machine.object.impl.composite.inst.branch;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.inst.filter.IdInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.composite.inst.BranchInstruction;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.util.ObjectHelper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class BranchInst<S extends Obj, E extends Obj> extends TInst<Obj, Obj> implements BranchInstruction<S, E> {

    private Map<Obj, Obj> branches;

    private BranchInst(final Map<Obj, Obj> branches, final Object... args) {
        super(PList.of(args));
        this.<PList<Obj>>get().add(0, TStr.of(Tokens.BRANCH));
        this.branches = branches;
    }

    @Override
    public Map<Obj, Obj> getBranches() {
        return this.branches;
    }

    public static <S extends Obj, E extends Obj> BranchInst<S, E> create(final Object... branches) {
        final Map<Obj, Obj> branchMap = new LinkedHashMap<>();
        for (int i = 0; i < branches.length; i++) {
            if (branches[i] instanceof Rec) {
                for (final Map.Entry<Obj, Obj> entry : ((Rec) branches[i]).<Map<Obj, Obj>>get().entrySet()) {
                    branchMap.put(entry.getKey(), entry.getValue());
                }
            } else {
                branchMap.put(IdInst.create().label(i + "!!"), ObjectHelper.from(branches[i]));
            }
        }
        return new BranchInst<>(branchMap, branches);
    }
}
