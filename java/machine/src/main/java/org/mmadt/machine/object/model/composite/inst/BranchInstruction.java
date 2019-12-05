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

package org.mmadt.machine.object.model.composite.inst;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.composite.inst.branch.BranchInst;
import org.mmadt.machine.object.impl.composite.inst.branch.ChooseInst;
import org.mmadt.machine.object.impl.composite.inst.filter.IdInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.processor.util.FastProcessor;
import org.mmadt.util.MultiIterator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface BranchInstruction<S extends Obj, E extends Obj> extends Inst, Function<S, E> {

    public Map<Obj, Obj> getBranches();

    @Override
    default E apply(final S obj) {
        final MultiIterator<E> itty = new MultiIterator<>();
        for (final Map.Entry<Obj, Obj> entry : this.getBranches().entrySet()) {
            if (FastProcessor.process(obj.mapTo(entry.getKey())).hasNext()) {
                itty.addIterator(FastProcessor.process(obj.mapTo(entry.getValue()))); // TODO: make sure this is global
                if (this instanceof ChooseInst)
                    break;
            }
        }
        return TObj.none().set(itty);
    } // this should all be done through subscription semantics and then its just a append round-robin

    public default E quantifyRange(final S domain) {
        return domain.q(domain.q().mult(getBranches().values().stream().map(Obj::q).reduce((Q) domain.q().zero(), Q::plus)));
    }

    public static Map<Obj, Obj> buildBranchMap(final boolean choose, final Object... branches) {
        final Map<Obj, Obj> branchMap = new LinkedHashMap<>();
        int counter = 0;
        for (final Object branch : branches) {
            if (branch instanceof Rec) {
                for (final Map.Entry<Obj, Obj> entry : ((Rec) branch).<Map<Obj, Obj>>get().entrySet()) {
                    branchMap.put(entry.getKey(), entry.getValue());
                }
            } else if ((choose && branch instanceof ChooseInst) || (!choose && branch instanceof BranchInst))
                branchMap.putAll(((BranchInstruction<?, ?>) branch).getBranches());
            else if (branch instanceof Inst)
                branchMap.put(IdInst.create().label("" + counter++), ObjectHelper.from(branch)); // TODO: is it bad to use ID keys.
            else
                branchMap.put(ObjectHelper.from(branch), ObjectHelper.from(branch));
        }
        return branchMap;
    }
}
