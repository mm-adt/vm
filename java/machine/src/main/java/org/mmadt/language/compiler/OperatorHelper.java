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

package org.mmadt.language.compiler;

import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.impl.composite.inst.map.AndInst;
import org.mmadt.machine.object.impl.composite.inst.map.DivInst;
import org.mmadt.machine.object.impl.composite.inst.map.EqInst;
import org.mmadt.machine.object.impl.composite.inst.map.GetInst;
import org.mmadt.machine.object.impl.composite.inst.map.GtInst;
import org.mmadt.machine.object.impl.composite.inst.map.GteInst;
import org.mmadt.machine.object.impl.composite.inst.map.LtInst;
import org.mmadt.machine.object.impl.composite.inst.map.LteInst;
import org.mmadt.machine.object.impl.composite.inst.map.MapInst;
import org.mmadt.machine.object.impl.composite.inst.map.NegInst;
import org.mmadt.machine.object.impl.composite.inst.map.OrInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.ext.algebra.WithDiv;
import org.mmadt.machine.object.model.ext.algebra.WithMinus;
import org.mmadt.machine.object.model.ext.algebra.WithMult;
import org.mmadt.machine.object.model.ext.algebra.WithOrder;
import org.mmadt.machine.object.model.ext.algebra.WithPlus;

import static org.mmadt.machine.object.impl.__.get;
import static org.mmadt.machine.object.impl.__.map;
import static org.mmadt.machine.object.impl.__.mult;
import static org.mmadt.machine.object.impl.__.plus;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class OperatorHelper {

    private OperatorHelper() {
        // static helper class
    }

    public static Obj applyBinary(final String operator, final Obj lhs, final Object rhs) {
        // System.out.println(lhs + " " + operator + " " + rhs);
        switch (operator) {
            case (Tokens.ASTERIX):
                return lhs instanceof WithMult && !lhs.isInst() ?
                        ((WithMult) lhs).mult(rhs) :
                        lhs.isInst() && rhs instanceof Inst ?
                                ((Inst) lhs).mult(rhs) :
                                map(lhs).mult(mult(rhs));
            case (Tokens.CROSS):
                return lhs instanceof WithPlus && !lhs.isInst() ?
                        ((WithPlus) lhs).plus(rhs) :
                        lhs.isInst() && rhs instanceof Inst ?
                                ((Inst) lhs).plus(rhs) :
                                map(lhs).mult(plus(rhs));
            case (Tokens.BACKSLASH):
                return ((WithDiv) lhs).div(rhs);
            case (Tokens.DASH):
                return ((WithMinus) lhs).minus(rhs);
            case (Tokens.AMPERSAND):
                return lhs.and(rhs);
            case (Tokens.BAR):
                return lhs.or(rhs);
            case (Tokens.RANGLE):
                return ((WithOrder) lhs).gt(rhs);
            case (Tokens.LANGLE):
                return ((WithOrder) lhs).lt(rhs);
            case (Tokens.REQUALS):
                return ((WithOrder) lhs).gte(rhs);
            case (Tokens.LEQUALS):
                return ((WithOrder) lhs).lte(rhs);
            case (Tokens.DEQUALS):
                return lhs.eq(rhs);
            case (Tokens.MAPSTO):
                return lhs.mapTo((Obj) rhs);
            case (Tokens.MAPSFROM):
                return lhs.mapFrom((Obj) rhs);
            case (Tokens.LPACK):
                return TRec.of(rhs, lhs);
            case Tokens.RPACK:
                return TRec.of(lhs, rhs);
            case Tokens.PERIOD:
                return lhs.isInst() ?
                        ((Inst) lhs).mult(get(rhs)) :
                        map(lhs).mult(get(rhs));
            default:
                throw new RuntimeException("Unknown operator: " + operator);
        }
    }

    public static Obj applyUnary(final String operator, final Obj rhs) {
        switch (operator) {
            case (Tokens.ASTERIX):
                return mult(rhs);
            case (Tokens.CROSS):
                return plus(rhs);
            case (Tokens.DASH):
                return rhs instanceof WithMinus && !rhs.isInst() ? ((WithMinus) rhs).neg() : MapInst.create(MapInst.create(rhs).mult(NegInst.create())).attach(rhs);
            case (Tokens.BACKSLASH):
                return DivInst.create(rhs);
            case (Tokens.AMPERSAND):
                return AndInst.create(rhs);
            case (Tokens.BAR):
                return OrInst.create(rhs);
            case (Tokens.RANGLE):
                return GtInst.create(rhs);
            case (Tokens.LANGLE):
                return LtInst.create(rhs);
            case (Tokens.REQUALS):
                return GteInst.create(rhs);
            case (Tokens.LEQUALS):
                return LteInst.create(rhs);
            case (Tokens.DEQUALS):
                return EqInst.create(rhs);
            case Tokens.PERIOD:
                return GetInst.create(rhs);
            default:
                throw new RuntimeException("Unknown operator: " + operator);
        }
    }
}
