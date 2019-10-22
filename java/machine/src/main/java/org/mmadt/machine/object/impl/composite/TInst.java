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
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Stream;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.algebra.WithRing;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.StringFactory;

import java.util.List;

import static org.mmadt.language.compiler.Tokens.INST;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TInst extends TObj implements Inst {

    private static final Inst SOME = new TInst(PList.of(TStr.some(), TObj.all()));
    private static final Inst ALL = new TInst(null).q(0, Integer.MAX_VALUE);
    private static final Inst NONE = new TInst(null).q(0);

    private Obj domain = TObj.none();
    private Obj range = TObj.none();

    private TInst(final Object value) {
        super(value);
        this.symbol = INST;
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

    @Override
    public Str opcode() {
        return (Str) this.get(TInt.zeroInt());
    }

    @Override
    public Obj domain() {
        return this.<TInst>peak().domain;
    }

    @Override
    public Obj range() {
        return this.<TInst>last().range;
    }

    @Override
    public <Q extends WithRing<Q>> TQ<Q> q() {
        if (this.get() instanceof Stream) { // TODO: memoize this
            WithRing low = null;
            WithRing high = null;
            for (final Obj a : this.<Stream<Obj>>get()) {
                low = null == low ? a.q().low() : (Q) low.plus(a.q().low());
                high = null == high ? a.q().high() : (Q) high.plus(a.q().high());
            }
            this.quantifier = new TQ<>((Q) low.set(TStream.of(low, high)));
        }
        return null == this.quantifier ? (TQ<Q>) TQ.ONE : (TQ<Q>) this.quantifier;
    }

    @Override
    public String toString() {
        return StringFactory.inst(this);
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
    public Inst and(final Obj inst) {
        return this.operator(Tokens.AND, (Inst) inst);
    }

    @Override
    public Inst or(final Obj inst) {
        return this.operator(Tokens.OR, (Inst) inst);
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
                        object.q(object.q().and(this.q())) :
                        object.isOne() ?
                                this.q(this.q().and(object.q())) :
                                new TInst(TStream.of(this, object));
    }

    @Override
    public Inst negate() {
        return this.q(this.q().negate());
    }

    @Override
    public Inst zero() {
        return TInst.none();
    }

    @Override
    public Inst one() {
        return TInst.of(Tokens.ID);
    }

    @Override
    public Inst domainAndRange(final Obj domain, final Obj range) {
        final TInst clone = (TInst) this.clone();
        clone.domain = domain;
        clone.range = range;
        return clone;
    }

    private Inst operator(final String opcode, final Inst inst) {
        if (this.opcode().get().equals(opcode)) {
            final PList<Obj> list = new PList<>(this.<PList<Obj>>get());
            list.add(inst);
            return new TInst(list);
        } else
            return this.eq(inst).get() ? this.q(this.q().or(inst.q())) : TInst.of(opcode, this, inst);
    }

    @Override
    public Inst put(final Int key, final Obj value) {
        ((PList<Obj>) this.value).set(key.get(), value);
        return this;
    }

    @Override
    public Inst drop(final Int key) {
        ((PList<Obj>) this.value).remove(key.<Integer>get().intValue());
        return this;
    }

}
