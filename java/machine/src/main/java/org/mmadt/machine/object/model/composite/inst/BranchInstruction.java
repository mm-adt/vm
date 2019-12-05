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
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.inst.branch.BranchInst;
import org.mmadt.machine.object.impl.composite.inst.branch.ChooseInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.processor.util.FastProcessor;
import org.mmadt.util.MultiIterator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface BranchInstruction<S extends Obj, E extends Obj> extends Inst, Function<S, E> {

    @Override
    default E apply(final S obj) {
        final MultiIterator<E> itty = new MultiIterator<>();
        for (final Map.Entry<Obj, Obj> entry : this.args().get(0).<Map<Obj, Obj>>get().entrySet()) {
            if (FastProcessor.process(obj.mapTo(entry.getKey())).hasNext()) {
                itty.addIterator(FastProcessor.process(obj.mapTo(entry.getValue()))); // TODO: make sure this is global
                if (this instanceof ChooseInst) // first pattern match
                    break;
            }
        }
        return TObj.none().set(itty);
    } // this should all be done through subscription semantics and then its just a append round-robin

    public default E quantifyRange(final S domain) {
        return domain.q(domain.q().mult(this.<Map<Obj, Obj>>get().values().stream().map(Obj::q).reduce((Q) domain.q().zero(), Q::plus)));
    }

    public static Map<Obj, Obj> buildBranchMap(final boolean choose, final Object... branches) {
        final Map<Obj, Obj> branchMap = new LinkedHashMap<>();
        for (final Object branch : branches) {
            if (branch instanceof Rec)
                branchMap.putAll(((Rec) branch).get());
            else if ((choose && branch instanceof ChooseInst) || (!choose && branch instanceof BranchInst))
                branchMap.putAll(((BranchInstruction<?, ?>) branch).<Rec<Obj, Obj>>args().get(0).get());
            else if (branch instanceof Inst)
                branchMap.put(KeyToken.mint(), ObjectHelper.from(branch));
            else
                branchMap.put(ObjectHelper.from(branch), ObjectHelper.from(branch));
        }
        return branchMap;
    }

    // TODO: this is not a smart idea -- but it will work for now
    public static class KeyToken<S extends Obj> extends TInst<S, S> implements FilterInstruction<S> {

        private KeyToken() {
            super(UUID.randomUUID());
        }

        @Override
        public String toString() {
            return "";
        }

        public S apply(final S obj) {
            return obj;
        }

        static KeyToken mint() {
            return new KeyToken();
        }
    }
}
