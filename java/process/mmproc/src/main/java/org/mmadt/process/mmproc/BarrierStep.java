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

import org.mmadt.object.model.Obj;
import org.mmadt.process.mmproc.util.Barrier;
import org.mmadt.process.mmproc.util.InMemoryBarrier;
import org.mmadt.processor.function.BarrierFunction;
import org.mmadt.util.EmptyIterator;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class BarrierStep<S extends Obj, E extends Obj, B> extends AbstractStep<S, E> {

    private final Barrier<B> barrier;
    private final BarrierFunction<S, E, B> barrierFunction;
    private boolean done = false;
    private Iterator<E> output = EmptyIterator.instance();

    BarrierStep(final Step<?, S> previousStep, final BarrierFunction<S, E, B> barrierFunction) {
        super(previousStep, barrierFunction);
        this.barrier = new InMemoryBarrier<>(barrierFunction.getInitialValue()); // TODO: move to strategy determination
        this.barrierFunction = barrierFunction;
    }

    @Override
    public E next() {
        if (!this.done) {
            while (this.previousStep.hasNext()) {
                this.barrier.update(this.barrierFunction.apply(super.previousStep.next(), this.barrier.get()));
            }
            this.done = true;
            this.output = this.barrierFunction.createIterator(this.barrier.get());
        }
        return this.output.next();
    }

    @Override
    public boolean hasNext() {
        return this.output.hasNext() || (!this.done && this.previousStep.hasNext());
    }

    @Override
    public void reset() {
        this.barrier.reset();
        this.output = EmptyIterator.instance();
        this.done = false;
    }
}