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

package org.mmadt.process.mmproc;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.inst.FilterInstruction;
import org.mmadt.util.FastNoSuchElementException;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class FilterStep<S extends Obj> extends AbstractStep<S, S, FilterInstruction<S>> {

    private S nextObj = null;

    FilterStep(final Step<?, S> previousStep, final FilterInstruction<S> filterInstruction) {
        super(previousStep, filterInstruction);
    }

    @Override
    public S next() {
        this.stageNextObj();
        if (null == this.nextObj)
            throw FastNoSuchElementException.instance();
        else {
            final S obj = this.nextObj;
            this.nextObj = null;
            return obj;
        }
    }

    @Override
    public boolean hasNext() {
        this.stageNextObj();
        return null != this.nextObj;
    }

    private void stageNextObj() {
        while (null == this.nextObj && this.previousStep.hasNext()) {
            final S temp = this.previousStep.next();
            this.nextObj = this.inst.apply(temp);
            if (this.nextObj.q().isZero())
                this.nextObj = null;
        }
    }

    @Override
    public void reset() {
        this.nextObj = null;
    }
}