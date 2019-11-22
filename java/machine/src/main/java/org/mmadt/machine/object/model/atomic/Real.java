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

import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.model.type.algebra.WithField;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.processor.util.FastProcessor;

import java.util.List;

/**
 * A Java representation of the {@code real} object in mm-ADT.
 * A {@code real} is an ordered commutative field with unity.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Real extends WithField<Real> {

    public default Float java() {
        return this.get();
    }

    @Override
    public default Iterable<Real> iterable() {
        return this.isInstance() ? List.of(this) : () -> FastProcessor.process(this);
    }

    /////////////////////////////////////// RAW OBJECT METHODS  (TODO: this can all be added to super interfaces given TXXX.some() can be accessed)

    public default Real mult(final Object object) {
        return this.mult(ObjectHelper.create(TReal.some(), object));
    }

    public default Real plus(final Object object) {
        return this.plus(ObjectHelper.create(TReal.some(), object));
    }

    public default Real minus(final Object object) {
        return this.minus(ObjectHelper.create(TReal.some(), object));
    }

    public default Bool gt(final Object object) {
        return this.gt(ObjectHelper.create(TReal.some(), object));
    }

    public default Bool gte(final Object object) {
        return this.gte(ObjectHelper.create(TReal.some(), object));
    }

    public default Bool lte(final Object object) {
        return this.lte(ObjectHelper.create(TReal.some(), object));
    }

    public default Bool lt(final Object object) {
        return this.lt(ObjectHelper.create(TReal.some(), object));
    }
}
