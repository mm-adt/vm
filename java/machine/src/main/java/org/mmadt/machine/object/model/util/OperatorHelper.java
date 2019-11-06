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

package org.mmadt.machine.object.model.util;

import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.type.algebra.WithAnd;
import org.mmadt.machine.object.model.type.algebra.WithDiv;
import org.mmadt.machine.object.model.type.algebra.WithMinus;
import org.mmadt.machine.object.model.type.algebra.WithMult;
import org.mmadt.machine.object.model.type.algebra.WithPlus;

import java.util.function.Supplier;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class OperatorHelper {

    private OperatorHelper() {
        // static helper class
    }

    public static <A extends Obj> A operation(final String operator, final A lhs, final A rhs) {
        switch (operator) {
            case ("*"):
                return (A) ((WithMult) lhs).mult((WithMult) rhs);
            case ("+"):
                return (A) ((WithPlus) lhs).plus((WithPlus) rhs);
            case ("/"):
                return (A) ((WithDiv) lhs).div((WithDiv) rhs);
            case ("-"):
                return (A) ((WithMinus) lhs).minus((WithMinus) rhs);
            case ("&"):
                return (A) ((WithAnd) lhs).and(rhs);
            case ("|"):
                return (A) lhs.or(rhs);
            default:
                throw new RuntimeException("Unknown operator: " + operator);
        }
    }

    public static <A extends Obj> A unary(final String opcode, final Supplier<A> operator, final A objA) {
        if (objA.isInstance() || objA.isType()) // TODO: on the unary requires type (this will not be true when AND/OR move up the stack
            return operator.get();
        else
            return objA.access(objA.access().mult(TInst.of(opcode)));
    }
}
