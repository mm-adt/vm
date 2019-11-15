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
import org.mmadt.processor.util.FastProcessor;

import java.util.List;

/**
 * A Java representation of the {@code int} object in mm-ADT.
 * An {@code int} is an ordered commutative ring with unity.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Int extends WithOrderedRing<Int> {

    @Override
    public Int one(); // {a,b} => {a,b}

    @Override
    public Int zero(); // {a,b} => {a,b}

    @Override
    public Int mult(final Int object); // {a,b} * {c,d} => {a,b}

    @Override
    public Int plus(final Int object); // {a,b} + {c,d} => {a,b}

    @Override
    public Int minus(final Int object); // {a,b} - {c,d} => {a,b}

    @Override
    public Int neg(); // {a,b} => {a,b}

    @Override
    public Int max(); // {a,b} => {a,b}

    @Override
    public Int min(); // {a,b} => {a,b}

    //@Override
    //public Int and(final Int object); // {a,b} & {c,d} => {a&c,b&d}

    //@Override
    //public Int or(final Obj object); // {a,b} | {c,d} => [{a,b},{c,d}]  (the branches are uncoupled -- variants)

    public default Integer java() {
        return this.get();
    }

    @Override
    public default Iterable<Int> iterable() {
        return this.isInstance() ? List.of(this) : () -> new FastProcessor<Int>().iterator(this);
    }

}
