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

package org.mmadt.machine.object.impl.composite;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.TStream;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Stream;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.algebra.WithOrderedRing;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.StringFactory;

import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TInst extends TObj implements Inst {

    private static final Inst SOME = new TInst(PList.of(TStr.some(), TObj.all()));
    private static final Inst ALL = new TInst(null).q(0, Integer.MAX_VALUE);
    private static final Inst NONE = new TInst(null).q(0);
    private static final Inst ONE = new TInst(PList.of(Tokens.ID));

    private Obj domain = TObj.none();
    private Obj range = TObj.none();

    private TInst(final Object value) {
        super(value);
    }

    public static Inst some() {
        return SOME;
    }

    public static Inst all() {
        return ALL;
    }

    public static Inst none() {
        return NONE;
    }

    public static Inst id() {
        return TInst.of(Tokens.ID);
    }

    public static Inst of(final String opcode, final Object... args) {
        return TInst.of(TStr.of(opcode), args);
    }

    public static Inst of(final Str opcode, final Object... args) {
        if (args.length == 1 && args[0] instanceof PList) {
            ((PList) args[0]).add(0, opcode);
            return new TInst(args[0]);
        } else {
            final PList<Obj> value = new PList<>();
            value.add(opcode);
            for (final Object arg : args) {
                value.add(ObjectHelper.from(arg));
            }
            return new TInst(value);
        }
    }

    public static Inst of(final List<Inst> insts) {
        return insts.isEmpty() ? TInst.none() : new TInst(TStream.of(insts));
    }

    @Override
    public Obj domain() {
        return this.<TInst>peek().domain;
    }

    @Override
    public Obj range() {
        return this.<TInst>last().range;
    }

    @Override
    public <A extends WithOrderedRing<A>> Q<A> q() {   // TODO: this might not make sense moving forward as an instruction can't have a quantifier and be quantified :|
        if (this.get() instanceof Stream) { // TODO: memoize this
            A low = null;
            A high = null;
            for (final Inst a : this.iterable()) {
                low = null == low ? a.<A>q().peek() : low.plus(a.<A>q().peek());
                high = null == high ? a.<A>q().last() : high.plus(a.<A>q().last());
            }
            return new TQ<>(low, high);
        }
        return super.q();
    }

    @Override
    public Inst and(final Obj obj) {
        return this.operator(Tokens.AND, obj);
    }

    @Override
    public Inst or(final Obj obj) {
        return this.operator(Tokens.OR, obj);
    }

    @Override
    public Inst plus(final Inst inst) {
        return inst.isZero() ? this : this.isZero() ? inst : this.operator(Tokens.BRANCH, inst);
    }

    @Override
    public Inst mult(final Inst object) { // TODO: optimize this nest
        return object.isZero() ?
                this.zero() :
                this.isOne() ?
                        object.q(object.q().mult(this.q())) :
                        object.isOne() ?
                                this.q(this.q().mult(object.q())) :
                                new TInst(TStream.of(List.of(this, object)));
    }

    @Override
    public Inst neg() {
        return this.q(this.q().neg());
    }

   /* @Override
    public Inst zero() {
        return OperatorHelper.unary(Tokens.ZERO, () -> NONE, this);
    }*/

    @Override
    public Inst zero() {
        return NONE; // TODO: need to make a zero instruction [none] (we are conflating absence of instruction with an instruction that represents * -> 0.
    }

    @Override
    public Inst one() {
        return ONE;
    }

    /*@Override
    public Bool eq(final Obj obj) {
        return OperatorHelper.binary(Tokens.EQ, () -> TBool.of(obj instanceof Inst && this.java().equals(((Inst) obj).java())), this, obj);
    }*/

    @Override
    public Inst domainAndRange(final Obj domain, final Obj range) {
        final TInst clone = (TInst) this.clone();
        clone.domain = domain;
        clone.range = range;
        return clone;
    }

    private Inst operator(final String opcode, final Obj obj) {
        final Inst inst = obj instanceof Inst ? (Inst) obj : TInst.of(Tokens.MAP, obj); // if the object isn't an instruction, make it one
        final Inst last = this.last();
        if (last.opcode().java().equals(opcode)) {
            final PList<Obj> list = new PList<>(last.java());
            list.add(inst);
            return new TInst(list);
        } else
            return this.eq(inst).java() ?
                    this.q(this.q().plus(inst.q())) :
                    TInst.of(opcode, this, inst); // e.g. [and,prev,curr] [or,prev,curr] [branch,prev,curr]
    }

    @Override
    public String toString() {
        return StringFactory.inst(this);
    }
}
