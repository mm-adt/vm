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
package org.mmadt.machine.object.model.type.algebra;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;

/**
 * An {@link org.mmadt.machine.object.model.Obj} with a + unity.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface WithZero<A extends WithZero<A>> extends Obj {

    public A zero();

    public default boolean isZero() {
        return this.isInstance() ?
                this.eq(this.zero()).java() :
                this.access().<Inst>last().opcode().java().equals(Tokens.ZERO); // need a cleaner way to analyze final inst
    }
}
