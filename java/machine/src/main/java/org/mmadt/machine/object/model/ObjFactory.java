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

package org.mmadt.machine.object.model;

import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.atomic.Real;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.type.algebra.WithOrderedRing;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface ObjFactory<A extends WithOrderedRing<A>> {

    public A quantifier();

    public BoolFactory<A> bools();

    public interface BoolFactory<A extends WithOrderedRing<A>> {
        public A quantifier();

        public Bool of(final Object... bools);

        public default Bool star() {
            return of().q(quantifier().zero(), quantifier().max());
        }

        public default Bool one() {
            return of().q(quantifier().one());
        }

        public default Bool maybe() {
            return of().q(quantifier().zero(), quantifier().one());
        }

        public default Bool none() {
            return of().q(quantifier().zero());
        }
    }

    public Int ints(final Object... ints);

    public Real reals(final Object... reals);

    public Str strs(final Object... strs);

}
