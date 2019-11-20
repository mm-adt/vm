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

package org.mmadt.language.compiler;

import org.mmadt.machine.object.impl.composite.inst.map.DivInst;
import org.mmadt.machine.object.impl.composite.inst.map.MapInst;
import org.mmadt.machine.object.impl.composite.inst.map.MultInst;
import org.mmadt.machine.object.impl.composite.inst.map.NegInst;
import org.mmadt.machine.object.impl.composite.inst.map.PlusInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.type.algebra.WithAnd;
import org.mmadt.machine.object.model.type.algebra.WithDiv;
import org.mmadt.machine.object.model.type.algebra.WithMinus;
import org.mmadt.machine.object.model.type.algebra.WithMult;
import org.mmadt.machine.object.model.type.algebra.WithOrder;
import org.mmadt.machine.object.model.type.algebra.WithPlus;

import static org.mmadt.machine.object.impl.composite.TInst.ID;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class OperatorHelper {

    private OperatorHelper() {
        // static helper class
    }

    public static <A extends Obj> A applyBinary(final String operator, final A lhs, final A rhs) {
        switch (operator) {
            case (Tokens.ASTERIX):
                return (A) ((WithMult) lhs).mult((WithMult) rhs);
            case (Tokens.CROSS):
                return (A) ((WithPlus) lhs).plus((WithPlus) rhs);
            case (Tokens.BACKSLASH):
                return (A) ((WithDiv) lhs).div((WithDiv) rhs);
            case (Tokens.DASH):
                return (A) ((WithMinus) lhs).minus((WithMinus) rhs);
            case (Tokens.AMPERSAND):
                return (A) ((WithAnd) lhs).and(rhs);
            case (Tokens.BAR):
                return (A) lhs.or(rhs);
            case (Tokens.RANGLE):
                return (A) ((WithOrder) lhs).gt(rhs);
            case (Tokens.LANGLE):
                return (A) ((WithOrder) lhs).lt(rhs);
            case (Tokens.REQUALS):
                return (A) ((WithOrder) lhs).gte(rhs);
            //case (Tokens.LEQUALS):
            //    return (A) ((WithOrder) lhs).gte(rhs);
            case (Tokens.DEQUALS):
                return (A) lhs.eq(rhs);
            case (Tokens.MAPSTO):
                return rhs instanceof Inst ? lhs.mapTo(rhs) : rhs.mapFrom(lhs);
            case (Tokens.MAPSFROM):
                return lhs.mapFrom(rhs);
            default:
                throw new RuntimeException("Unknown operator: " + operator);
        }
    }

    public static Obj applyUnary(final String operator, final Obj rhs) {
        switch (operator) {
            case (Tokens.ASTERIX):
                return MultInst.create(rhs);
            case (Tokens.CROSS):
                return PlusInst.create(rhs);
            case (Tokens.DASH):
                return rhs instanceof WithMinus ? ((WithMinus) rhs).neg() : MapInst.create(rhs).mult(NegInst.create());
            case (Tokens.BACKSLASH):
                return DivInst.create(rhs);

            default:
                throw new RuntimeException("Unknown operator: " + operator);
        }
    }
}
