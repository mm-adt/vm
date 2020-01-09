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

package org.mmadt.machine.object.model.atomic;

import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.ext.algebra.WithCommutativeRing;
import org.mmadt.machine.object.model.util.ObjectHelper;

/**
 * A Java representation of the {@code bool} object in mm-ADT.
 * A {@code bool} is a commutative ring with unity.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Bool extends WithCommutativeRing<Bool> {

    public default Boolean java() {
        return this.get();
    }

    @Override
    public Bool bind(final String variable);

    public default Bool and(final Bool bool) {
        return this.constant() ? this.set(this.java() && bool.java()) : (Bool) this.and((Obj) bool); // TODO: Bool.Type class with respective overloading
    }

    public default Bool is(final Inst bool) {
        return this.is(ObjectHelper.<Bool>create(TBool.of(), bool));
    }
}
