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
import org.mmadt.object.impl.composite.TQ;
import org.mmadt.process.mmproc.util.Reducer;
import org.mmadt.processor.function.ReduceFunction;
import org.mmadt.util.FastNoSuchElementException;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class ReduceStep<S extends Obj, E extends Obj> extends AbstractStep<S, E> {

    private final ReduceFunction<S, E> reduceFunction; // TODO: why was this needed?
    private final Reducer<S, E> reducer;
    private boolean done = false;

    ReduceStep(final Step<?, S> previousStep,
               final ReduceFunction<S, E> reduceFunction,
               final Reducer<S, E> reducer) {
        super(previousStep, reduceFunction);
        this.reduceFunction = reduceFunction;
        this.reducer = reducer;
    }

    @Override
    public E next() {
        if (this.done)
            throw FastNoSuchElementException.instance();
        while (this.previousStep.hasNext()) {
            this.reducer.add(this.previousStep.next());
        }
        this.done = true;
        return (E) this.reducer.get().q(TQ.one);
    }

    @Override
    public boolean hasNext() {
        return !this.done && this.previousStep.hasNext();
    }

    @Override
    public void reset() {
        this.reducer.reset();
        this.done = false;
    }
}