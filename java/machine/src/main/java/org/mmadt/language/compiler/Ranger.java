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

import org.mmadt.machine.object.impl.TSym;
import org.mmadt.machine.object.impl.TTModel;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.impl.composite.inst.initial.StartInst;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.type.algebra.WithOne;
import org.mmadt.machine.object.model.type.algebra.WithProduct;
import org.mmadt.machine.object.model.type.algebra.WithRing;
import org.mmadt.machine.object.model.type.algebra.WithZero;
import org.mmadt.machine.object.model.util.ModelCache;

import static org.mmadt.language.compiler.Tokens.A;
import static org.mmadt.language.compiler.Tokens.AND;
import static org.mmadt.language.compiler.Tokens.BRANCH;
import static org.mmadt.language.compiler.Tokens.COALESCE;
import static org.mmadt.language.compiler.Tokens.COUNT;
import static org.mmadt.language.compiler.Tokens.DB;
import static org.mmadt.language.compiler.Tokens.DEDUP;
import static org.mmadt.language.compiler.Tokens.DEFINE;
import static org.mmadt.language.compiler.Tokens.DIV;
import static org.mmadt.language.compiler.Tokens.DROP;
import static org.mmadt.language.compiler.Tokens.EQ;
import static org.mmadt.language.compiler.Tokens.ERROR;
import static org.mmadt.language.compiler.Tokens.FILTER;
import static org.mmadt.language.compiler.Tokens.GET;
import static org.mmadt.language.compiler.Tokens.GROUPCOUNT;
import static org.mmadt.language.compiler.Tokens.GT;
import static org.mmadt.language.compiler.Tokens.GTE;
import static org.mmadt.language.compiler.Tokens.ID;
import static org.mmadt.language.compiler.Tokens.IS;
import static org.mmadt.language.compiler.Tokens.LT;
import static org.mmadt.language.compiler.Tokens.LTE;
import static org.mmadt.language.compiler.Tokens.MAP;
import static org.mmadt.language.compiler.Tokens.MINUS;
import static org.mmadt.language.compiler.Tokens.MODEL;
import static org.mmadt.language.compiler.Tokens.MULT;
import static org.mmadt.language.compiler.Tokens.NEG;
import static org.mmadt.language.compiler.Tokens.NEQ;
import static org.mmadt.language.compiler.Tokens.ONE;
import static org.mmadt.language.compiler.Tokens.OR;
import static org.mmadt.language.compiler.Tokens.ORDER;
import static org.mmadt.language.compiler.Tokens.PLUS;
import static org.mmadt.language.compiler.Tokens.PUT;
import static org.mmadt.language.compiler.Tokens.Q;
import static org.mmadt.language.compiler.Tokens.RANGE;
import static org.mmadt.language.compiler.Tokens.REF;
import static org.mmadt.language.compiler.Tokens.START;
import static org.mmadt.language.compiler.Tokens.SUM;
import static org.mmadt.language.compiler.Tokens.ZERO;
import static org.mmadt.machine.object.model.composite.Q.Tag.one;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class Ranger {

    public static Obj getRange(final Inst inst, final Obj domain, final Model model) {
        final String op = inst.opcode().get();
        if (op.startsWith(Tokens.EQUALS))
            return ModelCache.CACHE.containsKey(op.substring(1)) ? ModelCache.CACHE.get(op.substring(1)) : domain;

        switch (op) {
            case A:
                return map(domain, TBool.some(), inst);
            case AND:
                return map(domain, TBool.some(), inst);
            case BRANCH:
                return map(domain, inst.range(), inst);
            case COALESCE:
                return domain; // TODO
            case DB:
                return model.has(DB) ? model.get(DB) : domain;
            case DEFINE:
                return sideEffect(domain);
            case DEDUP:
                return filter(domain, domain.q().one().peek(), inst);
            case DIV:
                return endoMap(domain, inst);
            case DROP:
                return sideEffect(domain);
            case COUNT:
                return reduce(domain.q(), domain.q().peek());
            case ERROR:
                throw new RuntimeException("Compilation error: " + domain + "::" + inst);
            case EQ:
                return map(domain, TBool.some(), inst);
            case FILTER:
                return filter(domain, inst);
            case GET:
                return map(domain, ((WithProduct) TSym.fetch(domain)).get(inst.get(TInt.oneInt())), inst);
            case GT:
                return map(domain, TBool.some(), inst);
            case GTE:
                return map(domain, TBool.some(), inst);
            case GROUPCOUNT:
                return TRec.of(arg(inst, 1).q(inst.q()), domain.q().peek().set(null)).q(one);
            case ID:
                return endoMap(domain, inst);
            case IS:
                return filter(domain, inst);
            case LT:
                return map(domain, TBool.some(), inst);
            case LTE:
                return map(domain, TBool.some(), inst);
            case PUT:
                return inst.get(TInt.twoInt());
            case REF:
                return map(domain, inst.get(TInt.oneInt()), inst);
            case MAP:
                return map(domain, arg(inst, 1), inst);
            case MODEL:
                return map(domain, TTModel.some(),inst);
                case MINUS:
                return endoMap(domain, inst);
            case MULT:
                return endoMap(domain, inst);
            case NEG:
                return endoMap(domain, inst);
            case NEQ:
                return map(domain, TBool.some(), inst);
            case ONE:
                return map(domain, ((WithOne) domain).one(), inst);
            case ORDER:
                return endoMap(domain, inst);
            case OR:
                return map(domain, TBool.some(), inst);
            case PLUS:
                return endoMap(domain, inst);
            case Q:
                return map(domain, domain.q(), inst);
            case RANGE: // TODO: none clip
                return domain.q(min((Int) inst.get(TInt.twoInt()), TInt.of(max(domain.<Int>q().peek(), (Int) inst.get(TInt.oneInt())))), min(domain.<Int>q().last(), (Int) inst.get(TInt.twoInt())));
            case START:
                return StartInst.create(inst.args().toArray(new Object[]{})).range();
            case SUM:
                return endoReduce(domain);
            case ZERO:
                return map(domain, ((WithZero) domain).zero(), inst);
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

    /////////////////

    private static Obj map(final Obj domain, final Obj range, final Inst inst) {
        return range.q(domain.q().mult(inst.q()));
    }

    private static Obj endoMap(final Obj domain, final Inst inst) {
        return map(domain, domain, inst);
    }

    private static Obj filter(final Obj domain, final Inst inst) {
        return filter(domain, domain.q().zero().peek(), inst);
    }


    private static Obj filter(final Obj domain, final WithRing low, final Inst inst) {
        return domain.q(low, domain.q().mult(inst.q()).last());
    }

    private static Obj reduce(final Obj domain, final Obj range) {
        return (domain.constant() ? range : range.set(null)).q(one);
    }

    private static Obj endoReduce(final Obj domain) {
        return reduce(domain, domain);
    }

    private static Obj sideEffect(final Obj domain) {
        return domain;
    }

    private static Obj arg(final Inst arg, final int index) {
        return arg.get(TInt.of(index)) instanceof Inst ? ((Inst) arg.get(TInt.of(index))).range() : arg.get(TInt.of(index));
    }
}