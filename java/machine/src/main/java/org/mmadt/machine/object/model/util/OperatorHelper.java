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
import org.mmadt.machine.object.model.composite.Inst;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class OperatorHelper {

    private OperatorHelper() {
        // static helper class
    }

    public static <A extends Inst> A operation(final String operator, final A lhs, final A rhs) {
        switch (operator) {
            case ("*"):
                return (A) lhs.mult(rhs);
            case ("+"):
                return (A) lhs.plus(rhs);
            case ("&"):
                return (A) lhs.and(rhs);
            case ("|"):
                return (A) lhs.or(rhs);
            case ("-"):
                return (A) lhs.plus((A) rhs.neg());
            default:
                throw new RuntimeException("Unknown operator: " + operator);
        }
    }

    public static <A extends Obj, B extends Obj> B bifunction(final String opcode, final BiFunction<A, A, B> operator, final A objA, final A objB, final B type) {
        if (objA.isInstance() || objA.isType())
            return operator.apply(objA, objB);
        else
            return type.access(objA.access().mult(TInst.of(opcode, objB)));

    }

    public static <A extends Obj> A binary(final String opcode, final BinaryOperator<A> operator, final A objA, final A objB) {
        if (objA.isInstance() || objA.isType())
            return operator.apply(objA, objB);
        else
            return objA.access(objA.access().mult(TInst.of(opcode, objB)));
    }

    public static <A extends Obj> A unary(final String opcode, final UnaryOperator<A> operator, final A objA) {
        if (objA.isInstance() || objA.isType())
            return operator.apply(objA);
        else
            return objA.access(objA.access().mult(TInst.of(opcode)));
    }

    public static Object tryCatch(final Supplier function, final Object failValue) {
        try {
            return function.get();
        } catch (final ArithmeticException e) {
            return failValue;
        }
    }
}
