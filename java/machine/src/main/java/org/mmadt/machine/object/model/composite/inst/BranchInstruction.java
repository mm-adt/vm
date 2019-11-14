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

package org.mmadt.machine.object.model.composite.inst;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.processor.util.FastProcessor;
import org.mmadt.util.MultiIterator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface BranchInstruction<S extends Obj, E extends Obj> extends Inst, Function<S, E> {

    public Map<Inst, List<Inst>> getBranches();

    public default Iterator<E> distribute(final S obj) {
        boolean found = false;
        final MultiIterator<E> itty = new MultiIterator<>();
        for (final Map.Entry<Inst, List<Inst>> entry : this.getBranches().entrySet()) {
            if (new FastProcessor<>(entry.getKey()).iterator(obj).hasNext()) {
                found = true;
                for (final Inst branch : entry.getValue()) {
                    itty.addIterator(new FastProcessor<S, E>(branch).iterator(obj)); // TODO: make sure this is global
                }
            }
        }
        if (!found && this.getBranches().containsKey(null)) {
            for (final Inst defaultBranch : this.getBranches().get(null)) {
                itty.addIterator(new FastProcessor<S, E>(defaultBranch).iterator(obj));
            }
        }
        return itty;
    }

    public default E apply(final S obj) {
        return TObj.none().set(distribute(obj));
    } // this should all be done through subscription semantics and then its just a lazy round-robin
}
