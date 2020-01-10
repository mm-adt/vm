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

package org.mmadt.machine.object.model;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.inst.barrier.DedupInst;
import org.mmadt.machine.object.impl.composite.inst.branch.BranchInst;
import org.mmadt.machine.object.impl.composite.inst.branch.ChooseInst;
import org.mmadt.machine.object.impl.composite.inst.filter.IdInst;
import org.mmadt.machine.object.impl.composite.inst.filter.IsInst;
import org.mmadt.machine.object.impl.composite.inst.map.AInst;
import org.mmadt.machine.object.impl.composite.inst.map.AsInst;
import org.mmadt.machine.object.impl.composite.inst.map.BindInst;
import org.mmadt.machine.object.impl.composite.inst.map.EqInst;
import org.mmadt.machine.object.impl.composite.inst.map.MapInst;
import org.mmadt.machine.object.impl.composite.inst.map.NeqInst;
import org.mmadt.machine.object.impl.composite.inst.map.OrInst;
import org.mmadt.machine.object.impl.composite.inst.map.StateInst;
import org.mmadt.machine.object.impl.composite.inst.reduce.CountInst;
import org.mmadt.machine.object.impl.composite.inst.reduce.SumInst;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.ExplainInst;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.ProbeInst;
import org.mmadt.machine.object.impl.ext.composite.TPair;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.atomic.Real;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.ext.algebra.WithAnd;
import org.mmadt.machine.object.model.ext.algebra.WithOr;
import org.mmadt.machine.object.model.ext.algebra.WithOrderedRing;
import org.mmadt.machine.object.model.ext.algebra.WithProduct;
import org.mmadt.machine.object.model.util.InstHelper;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.QuantifierHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A Java representation of an mm-ADT {@code obj}.
 * This is the base structure for all mm-ADT objects.
 *
 * <code>type~obj[pred]{quant,ifier}~bind<=[ref]</code>
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Obj extends Pattern, Cloneable, WithAnd<Obj>, WithOr<Obj> {

    public String symbol();

    public <B> B get();

    public WithOrderedRing q();

    public String binding();

    public Inst ref();

    /*
    public Inst constraint();
    public <O extends Obj> O constraint(final Inst predicate);
     */

    public <O extends Obj> O set(final Object object);

    public <O extends Obj> O q(final WithOrderedRing quantifier);

    public <O extends Obj> O bind(final String variable);

    public <O extends Obj> O ref(final Inst access);

    public <O extends Obj> O symbol(final String symbol);

    public <O extends Obj> O model(final Model model);

    public Model model();

    public default <O extends Obj> O copy(final Obj obj) {
        return InstHelper.isId(this.ref()) ?
                this.ref(obj.ref()).model(obj.model()) :
                this.ref(obj.ref().mult(this.ref())).model(obj.model()); // removed q() copy -- no failing tests .. !?
    }

    public default <O extends Obj> O halt() {
        return this.q(this.q().zero());
    }

    public <O extends Obj> O type();

    @Override
    public default Obj or(final Obj object) {
        // return this.set(ChooseInst.create(this,object)); // use choose?
        return this.set(IsInst.create(OrInst.create(AInst.create(this), AInst.create(object)))); // use choose?
    }

    List<Object> TRAMPOLINE = new ArrayList<>(); // TODO: isolate this as its own tool (no more static)

    @Override
    public default boolean test(final Obj obj) {
        boolean root = TRAMPOLINE.isEmpty();
        try {
            if (!QuantifierHelper.within(this.q(), obj.q()))
                return false;
            else if (obj.q().isZero())
                return true;

            if (this.isInstance())                                                              // INSTANCE CHECKING
                return obj.isInstance() && this.eq(obj).java();
            else if (this.isReference()) {                                                      // REFERENCE CHECKING
                return Objects.equals(this.get(), obj.get()) && this.ref().equals(obj.ref());
            } else {                                                                             // TYPE CHECKING
                assert this.isType(); // TODO: remove when proved
                ////////////////////////////////////////////
                if (TRAMPOLINE.contains(List.of(this, obj)))
                    return true;
                else
                    TRAMPOLINE.add(List.of(this, obj));
                ////////////////////////////////////////////
                // testing pattern or if no pattern, check the raw class type (int/bool/str/list/etc)
                return null != this.get() ?
                        this.<Pattern>get().test(obj) :
                        this.getClass().isAssignableFrom(obj.getClass());
            }
        } finally {
            if (root)
                TRAMPOLINE.clear();
        }
    }

    @Override
    public default boolean match(final Bindings bindings, final Obj obj) {
        if (bindings.has(this.binding()))
            return bindings.get(this.binding()).test(obj);
        else if (!QuantifierHelper.within(this.q(), obj.q()))
            return false;
        else if (obj.q().isZero())
            return true;

        bindings.start();
        final Object current = this.get();
        if (null != current) {
            if (current instanceof Pattern) {
                if (!((Pattern) current).match(bindings, obj)) {
                    bindings.rollback();
                    return false;
                }
            } else if (!this.test(obj)) {
                bindings.rollback();
                return false;
            }
        } else if (!this.getClass().isAssignableFrom(obj.getClass())) {
            bindings.rollback();
            return false;
        }
        if (null != this.binding())
            bindings.put(this.binding(), obj);
        if (this instanceof WithProduct)
            bindings.commit();
        return true;
    }

    public default <O extends Obj> O mapFrom(final Obj obj) {
        return obj instanceof Inst ?
                (O) this.ref(this.ref().mult((Inst) obj)) :
                this.as(obj.ref(null)).mapFrom(obj.ref());
    }

    public default <O extends Obj> O mapTo(final Obj obj) {
        if (obj instanceof Inst) {
            Obj o = this;
            for (final Inst inst : ((Inst) obj).iterable()) {
                o = ((TInst) inst).attach(o);
            }
            return (O) o;
        } else
            return this.as((O) obj);

    }

    public Obj clone();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void typeCheck();

    public default boolean isNamed() {
        return !Tokens.BASE_SYMBOLS.contains(this.symbol());
    }

    public default boolean isType() {
        return !this.constant() && this.ref().isOne();
    }

    public default boolean isReference() {
        return !this.constant() && !this.ref().isOne();
    }

    public default boolean isInstances() {
        return this.isReference() && InstHelper.isInstanceStream(this.ref());
    }

    public default boolean isInstance() {
        return this.constant();
    }

    public default boolean isPredicate() {
        return this.get() instanceof Inst;
    }

    public default boolean isSym() {
        return this.getClass().equals(TObj.class) && this.isBound();
    }

    public default boolean isNone() {
        return this.q().isZero();
    }

    public default boolean isSome() {
        return this.q().isOne();
    }

    public default boolean isAtomic() {
        return this.isBool() || this.isInt() || this.isReal() || this.isStr() || this.isSym();
    }

    public default boolean isBool() {
        return this instanceof Bool;
    }

    public default boolean isInt() {
        return this instanceof Int;
    }

    public default boolean isReal() {
        return this instanceof Real;
    }

    public default boolean isStr() {
        return this instanceof Str;
    }

    public default boolean isProduct() {
        return this.isRec() || this.isLst() || this.isInst();
    }

    public default boolean isRec() {
        return this instanceof Rec;
    }

    public default boolean isLst() {
        return this instanceof Lst;
    }

    public default boolean isInst() {
        return this instanceof Inst;
    }

    public default boolean isBound() {
        return null != this.binding();
    }

    public default <O extends Obj> O q(final QuantifierHelper.Tag tag) {
        return this.q(tag.apply(this.q()));
    }

    public default <O extends Obj> O q(final Object low, final Object high) {
        return this.q(TPair.of(low, high));
    }

    public default <O extends Obj> O q(final Object count) {
        return this.q((WithOrderedRing) ObjectHelper.from(count));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////// FLUENT METHODS ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Bool a(final Obj obj);

    public default <O extends Obj> O count() {
        return CountInst.compute(this);
    }

    public default <O extends Obj> O dedup() {
        return DedupInst.compute((O) this);
    }

    public default <O extends Obj> O is(final Bool bool) {
        return IsInst.compute((O) this, bool);
    }

    public default <O extends Obj> O id() {
        return IdInst.compute((O) this);
    }

    public default <O extends Obj> O map(final O obj) {
        return MapInst.compute(this, obj);
    }

    public default <O extends WithOrderedRing<O>> O sum() {
        return SumInst.compute((O) this);
    }

    public default <O extends Obj> O branch(final Object... branches) {
        return (O) BranchInst.create(branches).attach(this);
    }

    public default <O extends Obj> O choose(final Object... choices) {
        return (O) ChooseInst.create(choices).attach(this);
    }

    public default <O extends Obj> O as(final O obj) {
        return AsInst.compute((O) this, obj);
    }

    public default <O extends Obj> O is(final Object bool) {
        return this.is(ObjectHelper.create(TBool.of(), bool));
    }

    public default <O extends Obj> O map(final Inst inst) {
        return this.map(ObjectHelper.<O>create(this, inst));
    }

    public default <O extends Obj> O model(final O obj) {
        final O temp = this.model().readOrGet(obj, obj);
        return temp.model(this.model());
    }

    public default <K extends Obj, V extends Obj> Rec<K, V> state() {
        return StateInst.compute(this);
    }

    public default Str bind() {
        return BindInst.compute(this);
    }

    public default Int map(final Integer integer) {
        return this.map(TInt.of(integer));
    }

    public default <O extends Obj> O probe(final Object object) {
        return ProbeInst.<O>create(object).attach((O) this);
    }

    public default <O extends Obj> O explain() {
        return ExplainInst.<O>create().attach((O) this);
    }

    public default Bool eq(final Obj obj) {
        return EqInst.compute(this, obj, TBool.via(this));
    }

    public default Bool eq(final Object object) {
        return this.eq(ObjectHelper.create(this, object));
    }

    public default Bool neq(final Obj obj) {
        return NeqInst.compute(this, obj, TBool.via(this));
    }

    public default Bool neq(final Object object) {
        return this.neq(ObjectHelper.create(this, object));
    }
}
