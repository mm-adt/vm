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
import org.mmadt.processor.function.InitialFunction;
import org.mmadt.util.EmptyIterator;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class InitialStep<S extends Obj> extends AbstractStep<S, S> {

    private Iterator<S> objs;

    InitialStep(final InitialFunction<S> initialFunction) {
        super(EmptyStep.instance(), initialFunction);
        this.objs = initialFunction.get();
    }

    @Override
    public boolean hasNext() {
        return this.objs.hasNext();
    }

    @Override
    public S next() {
        return this.objs.next().q(this.function.quantifier()); // TODO: quantifier mult via this.function
    }

    @Override
    public void reset() {
        this.objs = EmptyIterator.instance();
    }
}