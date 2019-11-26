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

package org.mmadt.machine.object.model;

import org.mmadt.language.Query;
import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TQ;
import org.mmadt.machine.object.impl.composite.inst.branch.BranchInst;
import org.mmadt.machine.object.impl.composite.inst.filter.IsInst;
import org.mmadt.machine.object.impl.composite.inst.initial.StartInst;
import org.mmadt.machine.object.impl.composite.inst.map.EnvInst;
import org.mmadt.machine.object.impl.composite.inst.map.FromInst;
import org.mmadt.machine.object.impl.composite.inst.map.MapInst;
import org.mmadt.machine.object.impl.composite.inst.map.ToInst;
import org.mmadt.machine.object.impl.composite.inst.reduce.CountInst;
import org.mmadt.machine.object.impl.composite.inst.reduce.SumInst;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.type.Bindings;
import org.mmadt.machine.object.model.type.PAnd;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.type.Pattern;
import org.mmadt.machine.object.model.type.algebra.WithAnd;
import org.mmadt.machine.object.model.type.algebra.WithMult;
import org.mmadt.machine.object.model.type.algebra.WithOr;
import org.mmadt.machine.object.model.type.algebra.WithOrderedRing;
import org.mmadt.machine.object.model.type.algebra.WithProduct;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.processor.util.FastProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mmadt.machine.object.model.composite.Q.Tag.zero;

/**
 * A Java representation of an mm-ADT {@code obj}.
 * This is the base structure for all mm-ADT objects.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Obj extends Pattern, Cloneable, WithAnd<Obj>, WithOr<Obj> {

    List<Object> TRAMPOLINE = new ArrayList<>(); // TODO: isolate this as its own tool (no more static)

    final Set<String> BASE_SYMBOLS = Set.of(Tokens.OBJ, Tokens.BOOL, Tokens.INT, Tokens.REAL, Tokens.STR, Tokens.LIST, Tokens.REC, Tokens.INST);

    public String symbol();

    public <B> B get();

    public default boolean named() {
        return null != this.symbol() && !BASE_SYMBOLS.contains(this.symbol());
    }

    public <B extends WithOrderedRing<B>> Q<B> q();

    public String label();

    public Inst access();

    public Map<Str, Obj> env();

    public PMap<Inst, Inst> instructions();

    public default <O extends Obj> O peek() {          // TODO: only Q and Inst are using these ... it because they are hybrid objs between struct/process :(
        return (O) this.iterable().iterator().next();
    }

    public default <O extends Obj> O last() {
        final Iterator<O> itty = (Iterator<O>) this.iterable().iterator();
        O o = (O) TObj.none();
        while (itty.hasNext()) {
            o = itty.next();
        }
        return o;
    }

    public <O extends Obj> O type(final O type);

    public Obj type();

    public default Iterable<? extends Obj> iterable() {
        return this.isInstance() ? List.of(this) : () -> FastProcessor.process(this);
    }

    public <O extends Obj> O set(final Object object);

    public <O extends Obj> O q(final Q quantifier);

    public <O extends Obj> O label(final String variable);

    public <O extends Obj> O access(final Inst access);

    public <O extends Obj> O inst(final Inst instA, final Inst instB);

    public <O extends Obj> O symbol(final String symbol);

    public <O extends Obj> O insts(final PMap<Inst, Inst> insts);

    public default <O extends Obj> O env(final Map<Str, Obj> env) {
        Obj obj = this;
        for (final Map.Entry<Str, Obj> entry : env.entrySet()) {
            obj = obj.env(entry.getKey(), entry.getValue());
        }
        return (O) obj;
    }

    public <O extends Obj> O env(final Str name, final Obj obj);

    public default <O extends Obj> O copy(final Obj obj) {
        return this.q(obj.q()).access(obj.access()).env(obj.env());
    }

    @Override
    public default Obj bind(final Bindings bindings) {
        if (bindings.has(this.label()))
            return bindings.get(this.label());
        return this.insts(null == this.instructions() ? null : this.instructions().bind(bindings)).
                set(this.get() instanceof Pattern ? ((Pattern) this.get()).bind(bindings) : this.get()).
                access(this.access().isOne() ? null : this.access().bind(bindings));
    }

    @Override
    public default boolean test(final Obj obj) {
        boolean root = TRAMPOLINE.isEmpty();
        try {
            if (TObj.none().equals(this) || TObj.none().equals(obj))
                return this.q().test(obj);

            if (this.isInstance())                                                              // INSTANCE CHECKING
                return obj.isInstance() && this.eq(obj).java();
            else if (this.isReference()) {                                                      // REFERENCE CHECKING
                if (!obj.isReference())
                    return false;
                else {
                    final Iterator<? extends Obj> ittyA = this.iterable().iterator();
                    final Iterator<? extends Obj> ittyB = obj.iterable().iterator();
                    while (ittyA.hasNext()) {
                        if (!ittyB.hasNext() || !(ittyB.next().test(ittyA.next())))
                            return false;
                    }
                    return !ittyB.hasNext();
                }
            } else {                                                                             // TYPE CHECKING
                assert this.isType(); // TODO: remove when proved
                if (null != ObjectHelper.getName(this) && ObjectHelper.getName(this).equals(ObjectHelper.getName(obj)))
                    return true;
                ////////////////////////////////////////////
                if (TRAMPOLINE.contains(List.of(this, obj)))
                    return true;
                else
                    TRAMPOLINE.add(List.of(this, obj));
                ////////////////////////////////////////////
                if (obj.get() instanceof Stream && !(this.get() instanceof Inst)) // TODO: only used by inst at this point (when inst is no longer stream-based, gut this)
                    return Stream.testStream(this, obj);
                else // testing pattern or if no pattern, check the raw class type (int/bool/str/list/etc)
                    return null != this.get() ? this.<Pattern>get().test(obj) : this.getClass().isAssignableFrom(obj.getClass());
            }
        } finally {
            if (root)
                TRAMPOLINE.clear();
        }
    }

    @Override
    public default boolean match(final Bindings bindings, final Obj object) {
        if (bindings.has(this.label()))
            return bindings.get(this.label()).test(object);
        else if (TObj.none().equals(this) || TObj.none().equals(object))
            return this.q().test(object);
        bindings.start();
        final Object current = this.get();
        if (null != current) {
            if (object.get() instanceof Stream) {
                if (!Stream.matchStream(bindings, this, object)) {
                    bindings.rollback();
                    return false;
                }
            } else if (current instanceof Pattern) {
                if (!((Pattern) current).match(bindings, object)) {
                    bindings.rollback();
                    return false;
                }
            } else if (!this.test(object)) {
                bindings.rollback();
                return false;
            }
        } else if (!this.getClass().isAssignableFrom(object.getClass())) {
            bindings.rollback();
            return false;
        }
        if (null != this.label())
            bindings.put(this.label(), object);
        if (this instanceof WithProduct)
            bindings.commit();
        return true;
    }

    public default Optional<Inst> inst(final Bindings bindings, final Inst inst) {
        if (null != this.instructions()) {
            for (final Map.Entry<Inst, Inst> entry : this.instructions().entrySet()) {
                if (entry.getKey().match(bindings, inst))
                    return Optional.of(entry.getValue().bind(bindings));
            }
        }
        if (null != this.type() && this.type().get() instanceof PAnd) // TODO: this is why having a specialized variant class is important (so the logic is more recursive)
            return ((PAnd) this.type().get()).inst(this, bindings, inst);
        else if (this.get() instanceof PAnd)
            return ((PAnd) this.get()).inst(this, bindings, inst);
        else
            return Optional.empty();
    }

    /////////////// DELETE WHEN PROPERLY MIXED
    private <O extends Obj> O append(final Inst inst) {
        final Obj range = inst.computeRange(this);
        return range.access(this.access().mult(inst.domainAndRange(this, range)));
    }
    /////////////// DELETE WHEN PROPERLY MIXED

    public default <O extends Obj> O mapFrom(final Obj obj) {
        return obj instanceof Inst ?
                this instanceof Inst ?
                        (O) ((Inst) this).mult((Inst) obj) :
                        this.append((Inst) obj) :
                this instanceof Inst ?
                        obj.access(((Inst) this)).append(obj.access()) :
                        this.q(this.q().mult(obj.q())).access(this.access()).append(IsInst.isA(this)).append(obj.access());

    }

    public default <O extends Obj> O mapTo(final Obj obj) {
        if (obj instanceof Inst) {
            O o = this.isInstance() ? this.set(null).access(StartInst.create(((Object) this))) : (O) this;
            for (final Inst inst : ((Inst) obj).iterable()) {
                o = o.q(o.q().mult(obj.q())).append(inst);
            }
            return o;
        } else
            return obj.mapFrom(this);
    }

    public Obj clone();

    //////////////

    public default boolean isType() {
        return !this.constant() && this.access().isOne();
    }

    public default boolean isReference() {
        return !this.constant() && !this.access().isOne();
    }

    public default boolean isInstance() {
        return this.constant();
    }

    public default <O extends Obj> O access(final Query access) {
        return this.access(access.bytecode());
    }

    public default <O extends Obj> O q(final Q.Tag tag) {
        return this.q(tag.apply(this.q()));
    }

    public default <O extends Obj> O q(final Object low, final Object high) {
        return this.q(new TQ<>((WithOrderedRing) ObjectHelper.from(low), (WithOrderedRing) ObjectHelper.from(high)));
    }

    public default <O extends Obj> O q(final Object count) {
        return this.q((WithOrderedRing) ObjectHelper.from(count));
    }

    public default <O extends Obj> O q(final WithOrderedRing count) {
        return this.q(new TQ<>(count));
    }


    //////////////


    public Bool a(final Obj obj);

    public default <O extends Obj> O as(final Str label) {
        return this.isInstance() && label.isInstance() ?
                this.env(label, this) :
                this.mapTo(EnvInst.create(label));
    }

    public default <O extends Obj> O count() {
        return this.q().constant() ?
                this.q().peek().q(q().one()) :
                this.mapFrom(CountInst.create());
    }

    public default <O extends Obj> O env(final Str symbol) {
        return (this.isInstance()) ?
                this.set(this.get()).env().getOrDefault(symbol, TObj.none()).copy(this) :
                TLst.some().copy(this).mapFrom(EnvInst.create(symbol));
    }

    public default <O extends Obj> O is(final Bool bool) {
        return this.isInstance() && bool.isInstance() ?
                bool.java() ? (O) this : this.q(zero) :
                this.mapTo(IsInst.create(bool));
    }

    public default <O extends Obj> O map(final O obj) {
        return this.isInstance() && obj.isInstance() ?
                obj.copy(this) :
                this.getClass().equals(obj.getClass()) ?
                        this.mapTo(MapInst.create(obj)) :
                        obj.copy(this).mapTo(MapInst.create(obj));
    }

    public default <O extends Obj> O sum() {
        return this.isInstance() ?
                this instanceof Q ?
                        (O) ((WithMult) this).mult(this.q()) :
                        (O) new TQ((WithOrderedRing) this).mult(this.q()) :
                this instanceof Q ?
                        this.mapFrom(SumInst.create()) :
                        new TQ((WithOrderedRing) this).mapFrom(SumInst.create());

    }

    public default <O extends Obj> O branch(final Object... branches) {
        return this.mapTo(BranchInst.create(branches));
    }

    public Bool eq(final Obj object);

    public Bool neq(final Obj object);

    public default <O extends Obj> O to(final Str label) {
        return this.isInstance() && label.isInstance() ?
                this.env(label, this) :
                this.mapTo(ToInst.create(label));
    }

    public default <O extends Obj> O from(final Str label) {
        return this.isInstance() && label.isInstance() ?
                this.env(label) :
                this.mapTo(FromInst.create(label));
    }

    /////////////////////////////////////////////////////////////////

    public default <O extends Obj> O is(final Object bool) {
        return this.is(ObjectHelper.create(TBool.of(), bool));
    }

    public default <O extends Obj> O map(final Inst inst) {
        return this.map((O) ObjectHelper.create(this, inst));
    }

    public default Int map(final Integer integer) {
        return this.map(TInt.of(integer));
    }

    public default <O extends Obj> O as(final String label) {
        return this.env(TStr.of(label), this);
    }

    public default <O extends Obj> O env(final Object symbol) {
        return this.env((Str) ObjectHelper.from(symbol));
    }

    public default <O extends Obj> O to(final Object label) {
        return this.to((Str) ObjectHelper.from(label));
    }

    public default <O extends Obj> O from(final Object label) {
        return this.from((Str) ObjectHelper.from(label));
    }
}
