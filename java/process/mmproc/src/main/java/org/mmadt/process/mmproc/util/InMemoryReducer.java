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

package org.mmadt.process.mmproc.util;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.inst.ReduceInstruction;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class InMemoryReducer<S extends Obj, E extends Obj> implements Reducer<S, E> {

    private final ReduceInstruction<S, E> reduceFunction;
    private E seed;

    public InMemoryReducer(final ReduceInstruction<S, E> reduceFunction) {
        this.reduceFunction = reduceFunction;
        this.seed = this.reduceFunction.getInitialValue();
    }

    @Override
    public E get() {
        return this.seed;
    }

    @Override
    public void add(final S obj) {
        this.seed = this.reduceFunction.apply(obj, this.seed);
    }

    @Override
    public void reset() {
        this.seed = this.reduceFunction.getInitialValue();
    }
}