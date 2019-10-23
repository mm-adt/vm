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

package org.mmadt.processor.function.branch;

import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.util.StringFactory;
import org.mmadt.processor.compiler.IR;
import org.mmadt.processor.function.AbstractFunction;
import org.mmadt.processor.function.BranchFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class BranchBranch<S extends Obj, E extends Obj> extends AbstractFunction implements BranchFunction<S, E> {

    private Map<IR<S, ?>, List<IR<S, E>>> branches; // TODO: why aren't these just arguments?

    private BranchBranch(final Q quantifier, final String label, final Map<IR<S, ?>, List<IR<S, E>>> branches) {
        super(quantifier, label);
        this.branches = branches;
    }

    @Override
    public Map<IR<S, ?>, List<IR<S, E>>> getBranches() {
        return this.branches;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.branches.hashCode();
    }

    @Override
    public BranchBranch<S, E> clone() {
        final BranchBranch<S, E> clone = (BranchBranch<S, E>) super.clone();
        clone.branches = new HashMap<>(this.branches.size());
        for (final Map.Entry<IR<S, ?>, List<IR<S, E>>> entry : this.branches.entrySet()) {
            final List<IR<S, E>> compilations = new ArrayList<>(entry.getValue().size());
            for (final IR<S, E> compilation : entry.getValue()) {
                compilations.add(compilation.clone());
            }
            clone.branches.put(null == entry.getKey() ? null : entry.getKey().clone(), compilations);
        }
        return clone;
    }

    public static <S extends Obj, E extends Obj> BranchBranch<S, E> compile(final Inst inst) {
        final List<Inst> args = inst.args();
        final Map<IR<S, ?>, List<IR<S, E>>> branches = new LinkedHashMap<>();
        for (int i = 0; i < args.size(); i = i + 2) {
            final IR<S, ?> predicate = TInst.none().equals(args.get(i).get()) ? null : new IR<>(args.get(i));
            final IR<S, E> branch = new IR<>(args.get(i + 1));
            branches.computeIfAbsent(predicate, p -> new ArrayList<>()).add(branch);
        }
        return new BranchBranch<>(inst.q(), inst.label(), branches);
    }

    @Override
    public String toString() {
        return StringFactory.function(this, this.branches);
    }
}