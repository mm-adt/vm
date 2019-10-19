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

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class SourceStep<S extends Obj> implements Step<S, S> {

    private S obj = null;

    void addStart(final S obj) {
        if (null != this.obj)
            throw new IllegalStateException("This shouldn't happen"); // TODO: verify fully and then remove
        this.obj = obj;
    }

    @Override
    public boolean hasNext() {
        return null != this.obj;
    }

    @Override
    public S next() {
        final S temp = this.obj;
        this.obj = null;
        return temp;
    }

    @Override
    public void reset() {
        this.obj = null;
    }

    @Override
    public String toString() {
        return SourceStep.class.getSimpleName();
    }
}