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

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.TSym;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.type.algebra.WithProduct;
import org.mmadt.machine.object.model.util.ObjectHelper;

import static org.mmadt.language.compiler.Tokens.COUNT;
import static org.mmadt.language.compiler.Tokens.DB;
import static org.mmadt.language.compiler.Tokens.DEDUP;
import static org.mmadt.language.compiler.Tokens.EQ;
import static org.mmadt.language.compiler.Tokens.ERROR;
import static org.mmadt.language.compiler.Tokens.FILTER;
import static org.mmadt.language.compiler.Tokens.GET;
import static org.mmadt.language.compiler.Tokens.GT;
import static org.mmadt.language.compiler.Tokens.ID;
import static org.mmadt.language.compiler.Tokens.IS;
import static org.mmadt.language.compiler.Tokens.LT;
import static org.mmadt.language.compiler.Tokens.MAP;
import static org.mmadt.language.compiler.Tokens.MINUS;
import static org.mmadt.language.compiler.Tokens.MULT;
import static org.mmadt.language.compiler.Tokens.ORDER;
import static org.mmadt.language.compiler.Tokens.PLUS;
import static org.mmadt.language.compiler.Tokens.PUT;
import static org.mmadt.language.compiler.Tokens.RANGE;
import static org.mmadt.language.compiler.Tokens.REF;
import static org.mmadt.language.compiler.Tokens.START;
import static org.mmadt.language.compiler.Tokens.SUM;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class Instructions {

    public static Obj getRange(final Inst inst, final Obj domain, final Model model) {
        final String op = inst.opcode().get();
        switch (op) {
            case DB:
                return model.get(DB);
            case DEDUP:
                return domain.isInstance() ? domain : domain.q(1, domain.q().high().get());
            case COUNT:
                return domain.q().constant() ? TInt.of(domain.q().low()) : TInt.some();
            case ERROR:
                throw new RuntimeException("Compilation error: " + domain + "::" + inst);
            case EQ:
                return TBool.some().q(domain.q());
            case FILTER:
                return  domain.q(0, domain.q().and(inst.q()).high().get());
            case GET:
                return ((WithProduct<Obj, Obj>) TSym.fetch(domain)).get(inst.get(TInt.oneInt()));
            case GT:
                return TBool.some().q(domain.q());
            case ID:
                return domain.q(domain.q().and(inst.q()));
            case IS:
                return domain.q(domain.q().<Q>set(TInt.of(0,domain.q().mult(inst.q()).high().get())));
            case LT:
                TBool.some().q(domain.q());
            case ORDER:
                return domain.isInstance() ? domain.q(domain.q().one()) : domain;
            case PUT:
                return inst.get(TInt.twoInt());
            case REF:
                return inst.get(TInt.oneInt());
            case MAP:
                return inst.get(TInt.oneInt()) instanceof Inst ? ((TInst) inst.get(TInt.oneInt())).range().q(domain.q()) : inst.get(TInt.oneInt());
            case MINUS:
                return domain.q(domain.q().mult(inst.q()));
            case MULT:
                return domain.q(domain.q().mult(inst.q()));
            case PLUS:
                return domain.q(domain.q().mult(inst.q()));
            case RANGE: // TODO: none clip
                return domain.q(min((Int) inst.get(TInt.twoInt()), TInt.of(max(domain.<Int>q().low(), (Int) inst.get(TInt.oneInt())))), min(domain.<Int>q().high(), (Int) inst.get(TInt.twoInt())));
            case START:
                return inst.args().isEmpty() ? TObj.none() :
                        1 == inst.args().size() ? inst.args().get(0) :
                                ObjectHelper.type(inst.args().get(0)).q(inst.args().size());
            case SUM:
                return domain.isInstance() ? domain : TInt.some();
            default:
                throw new RuntimeException("Unknown instruction: " + inst);
        }

    }

    private static int max(final Int a, Int b) {
        if (a.gt(b).get())
            return a.get();
        else
            return b.get();
    }

    private static int min(final Int a, Int b) {
        if (a.lt(b).get())
            return a.get();
        else
            return b.get();
    }
}