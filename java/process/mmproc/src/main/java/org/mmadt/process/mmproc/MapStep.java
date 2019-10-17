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
import org.mmadt.processor.function.MapFunction;
import org.mmadt.util.FunctionUtils;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class MapStep<S extends Obj, E extends Obj> extends AbstractStep<S, E> {

    private final MapFunction<S, E> mapFunction;

    MapStep(final Step<?, S> previousStep, final MapFunction<S, E> mapFunction) {
        super(previousStep, mapFunction);
        this.mapFunction = mapFunction;
    }

    @Override
    public boolean hasNext() {
        return this.previousStep.hasNext();
    }

    @Override
    public E next() {
        return FunctionUtils.map(this.mapFunction, this.previousStep.next());
    }

    @Override
    public void reset() {

    }
}