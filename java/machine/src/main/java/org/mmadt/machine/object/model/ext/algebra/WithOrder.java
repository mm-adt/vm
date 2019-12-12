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

package org.mmadt.machine.object.model.ext.algebra;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.util.ObjectHelper;

/**
 * An {@link org.mmadt.machine.object.model.Obj} that supports >, <, >=, and <=.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface WithOrder<A extends Obj> extends Obj {

    public Bool gt(final A object);

    public Bool gte(final A object);

    public Bool lte(final A object);

    public Bool lt(final A object);

    public A max();

    public A min();

    public default boolean isMax() {
        return this.eq(this.max()).get();
    }

    public default boolean isMin() {
        return this.eq(this.min()).get();
    }

    ///////////////////////////////////////

    public default Bool gt(final Object object) {
        return this.gt(ObjectHelper.create(this, object));
    }

    public default Bool gte(final Object object) {
        return this.gte(ObjectHelper.create(this, object));
    }

    public default Bool lte(final Object object) {
        return this.lte(ObjectHelper.create(this, object));
    }

    public default Bool lt(final Object object) {
        return this.lt(ObjectHelper.create(this, object));
    }
}
