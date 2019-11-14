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
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TQ;
import org.mmadt.machine.object.impl.composite.inst.filter.IsInst;
import org.mmadt.machine.object.impl.composite.inst.map.AInst;
import org.mmadt.machine.object.impl.composite.inst.map.MapInst;
import org.mmadt.machine.object.impl.composite.inst.reduce.CountInst;
import org.mmadt.machine.object.impl.composite.inst.reduce.SumInst;
import org.mmadt.machine.object.model.atomic.Bool;
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

    // public Model model();

    public String label();

    public Inst access();

    public PMap<Inst, Inst> instructions();

    public PMap<Obj, Obj> members();

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
        return this.isInstance() ? List.of(this) : () -> new FastProcessor<>(this.access()).iterator(this);
    }

    public <O extends Obj> O set(final Object object);

    public <O extends Obj> O q(final Q quantifier);

    public <O extends Obj> O label(final String variable);

    public <O extends Obj> O access(final Inst access);

    public default <O extends Obj> O append(final Inst inst) {
        final Inst compose = this.access().equals(TInst.ID()) ?
                IsInst.create(AInst.create(this.access((Inst) null))).mult(this.access()).mult(inst) : // DOMAIN SPECIFICATION THROUGH is(a(obj))
                this.access().mult(inst);
        return inst.computeRange(this).access(compose);
    }

    public <O extends Obj> O inst(final Inst instA, final Inst instB);

    public <O extends Obj> O symbol(final String symbol);

    public <O extends Obj> O insts(final PMap<Inst, Inst> insts);

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

            if (this.isInstance())                                                             // INSTANCE CHECKING
                return obj.isInstance() && this.eq(obj).java();
            else if (this.isReference()) {                                                      // REFERENCE CHECKING
                // if (!obj.isReference()) TODO: expose when type access is checked
                //    return false;
                // else {
                final Iterator<? extends Obj> ittyA = this.iterable().iterator();
                final Iterator<? extends Obj> ittyB = obj.iterable().iterator();
                while (ittyA.hasNext()) {
                    if (!ittyB.hasNext() || !(ittyB.next().test(ittyA.next())))
                        return false;
                }
                return !ittyB.hasNext();
                // }
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

    public Obj clone();

    //////////////

    public default boolean isType() {
        return !this.q().constant() || (!this.constant() && (this.access().isOne() || this.access().isType()));
    }

    public default boolean isReference() {
        return !this.constant() && !this.access().isOne() && !this.access().isType() && this.q().constant();
    }

    public default boolean isInstance() {
        return this.constant(); // && this.q().constant();
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

    public default <O extends Obj> O count() {
        return this.q().constant() ? (O) this.q().peek().q(q().one()) : this.append(CountInst.create());
    }

    public default <O extends Obj> O id() {
        return (O) this;
    }

    public default <O extends Obj> O is(final Bool bool) {
        return (this.isInstance() && bool.isInstance()) ?
                bool.java() ? (O) this : this.q(zero) :
                (O) this.append(IsInst.create(bool));
    }

    public default <O extends Obj> O map(final O obj) {
        return obj.isInstance() ? obj.q(this.q()) : this.append(MapInst.create(obj));
    }

    public default <O extends Obj> O sum() {
        return this.isInstance() ?
                (O) this.set(((WithMult) this).mult(this.q())) :
                this.append(SumInst.create());
    }

    public Bool eq(final Obj object);

    public Bool neq(final Obj object);

}
