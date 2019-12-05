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

package org.mmadt.process.mmproc;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.composite.inst.BranchInstruction;
import org.mmadt.util.EmptyIterator;
import org.mmadt.util.MultiIterator;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class BranchStep<S extends Obj, E extends Obj, M> extends AbstractStep<S, E, BranchInstruction<S, E>> {


    private final Map<Obj, Obj> branches;
    private Iterator<E> nextObjs = EmptyIterator.instance();

    BranchStep(final Step<?, S> previousStep, final BranchInstruction<S, E> branchFunction) {
        super(previousStep, branchFunction);
        this.branches = branchFunction.<Rec>args().get(0).get();
        this.branches.remove(null);
    }

    @Override
    public boolean hasNext() {
        this.stageOutput();
        return this.nextObjs.hasNext();
    }

    @Override
    public E next() {
        this.stageOutput();
        return this.nextObjs.next();
    }

    private void stageOutput() {
        while (!this.nextObjs.hasNext() && this.previousStep.hasNext()) {
            this.nextObjs = new MultiIterator<>();
            final S obj = this.previousStep.next();
            for (final Map.Entry<Obj, Obj> entry : this.branches.entrySet()) {
                if (new Proc<>(obj.mapTo(entry.getKey())).iterator(obj).hasNext()) {
                    ((MultiIterator<E>) this.nextObjs).addIterator(new Proc(obj.mapTo(entry.getValue())).iterator(obj)); // TODO: make sure this is global
                }
            }
        }
    }

    @Override
    public void reset() {
        this.nextObjs = EmptyIterator.instance();
    }
}