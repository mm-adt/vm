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

package org.mmadt.machine.object.model.atomic;

import org.mmadt.machine.object.model.type.algebra.WithOrderedRing;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.processor.util.FastProcessor;

import java.util.List;

/**
 * A Java representation of the {@code int} object in mm-ADT.
 * An {@code int} is an ordered commutative ring with unity.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Int extends WithOrderedRing<Int> {

    public default Integer java() {
        return this.get();
    }

    @Override
    public default Iterable<Int> iterable() {
        return this.isInstance() ? List.of(this) : () -> FastProcessor.process(this);
    }

    @Override
    public Int mult(final Int object);

    @Override
    public Int plus(final Int object);

    /////////////////////////////////////// RAW OBJECT METHODS

    public default Int mult(final Object object) {
        return this.mult((Int) ObjectHelper.from(object));
    }

    public default Int plus(final Object object) {
        return this.plus((Int) ObjectHelper.from(object));
    }

    public default Int minus(final Object object) {
        return this.minus((Int) ObjectHelper.from(object));
    }

    public default Bool gt(final Object object) {
        return this.gt((Int) ObjectHelper.from(object));
    }

    public default Bool gte(final Object object) {
        return this.gte((Int) ObjectHelper.from(object));
    }

    public default Bool lte(final Object object) {
        return this.lte((Int) ObjectHelper.from(object));
    }

    public default Bool lt(final Object object) {
        return this.lt((Int) ObjectHelper.from(object));
    }

}
