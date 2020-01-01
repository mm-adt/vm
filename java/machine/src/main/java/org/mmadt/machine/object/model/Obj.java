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
import org.mmadt.machine.object.impl.composite.inst.map.EqInst;
import org.mmadt.machine.object.impl.composite.inst.map.MapInst;
import org.mmadt.machine.object.impl.composite.inst.map.OrInst;
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
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.QuantifierHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A Java representation of an mm-ADT {@code obj}.
 * This is the base structure for all mm-ADT objects.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Obj extends Pattern, Cloneable, WithAnd<Obj>, WithOr<Obj> {

    List<Object> TRAMPOLINE = new ArrayList<>(); // TODO: isolate this as its own tool (no more static)

    final Set<String> BASE_SYMBOLS = Set.of(Tokens.OBJ, Tokens.BOOL, Tokens.INT, Tokens.REAL, Tokens.STR, Tokens.LST, Tokens.REC, Tokens.INST);

    public default boolean named() {
        return !BASE_SYMBOLS.contains(this.symbol());
    }

    public String symbol();

    public <B> B get();

    public WithOrderedRing q();

    public String label();

    public Inst access();

    public <O extends Obj> O set(final Object object);

    public <O extends Obj> O q(final WithOrderedRing quantifier);

    public <O extends Obj> O label(final String variable);

    public <O extends Obj> O access(final Inst access);

    public <O extends Obj> O symbol(final String symbol);

    public <O extends Obj> O model(final Model model);

    public Model model();

    public default <O extends Obj> O copy(final Obj obj) {
        return this.access(obj.access()).model(obj.model()); // removed q() copy -- no failing tests .. !?
    }

    public default <O extends Obj> O kill() {
        return this.q(this.q().zero());
    }

    public <O extends Obj> O type();

    @Override
    public default Obj or(final Obj object) {
        return this.set(IsInst.create(OrInst.create(AInst.create(this), AInst.create(object)))); // use choose?
    }

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
                return Objects.equals(this.get(), obj.get()) && this.access().equals(obj.access());
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
        if (bindings.has(this.label()))
            return bindings.get(this.label()).test(obj);
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
        if (null != this.label())
            bindings.put(this.label(), obj);
        if (this instanceof WithProduct)
            bindings.commit();
        return true;
    }

    public default <O extends Obj> O mapFrom(final Obj obj) {
        if (obj instanceof Inst)
            return (O) this.access(this.access().mult((Inst) obj));
        else
            return this.as(obj.access(null)).mapFrom(obj.access());

    }

    public default <O extends Obj> O mapTo(final Obj obj) {
        if (obj instanceof Inst) {
            Obj o = this;
            for (final Inst inst : ((Inst) obj).iterable()) {
                o = ((TInst) inst).attach(o);
            }
            return (O) o;
        } else if (!obj.access().isOne())
            return this.mapTo(obj.access()).label(obj.label());
        else
            return this.as((O) obj);

    }

    public Obj clone();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public default boolean isType() {
        return !this.constant() && this.access().isOne();
    }

    public default boolean isReference() {
        return !this.constant() && !this.access().isOne();
    }

    public default boolean isInstance() {
        return this.constant();
    }

    public default boolean isSym() {
        return this.getClass().equals(TObj.class) && this.isLabeled();
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

    public default boolean isRec() {
        return this instanceof Rec;
    }

    public default boolean isLst() {
        return this instanceof Lst;
    }

    public default boolean isInst() {
        return this instanceof Inst;
    }

    public default boolean isLabeled() {
        return null != this.label();
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

    public Bool eq(final Obj object);

    public Bool neq(final Obj object);

    public default <O extends Obj> O as(final O obj) {
        return AsInst.compute((O) this, obj);
    }

    public default <O extends Obj> O is(final Object bool) {
        return Obj.this.is(ObjectHelper.create(TBool.via(this), bool));
    }

    public default <O extends Obj> O map(final Inst inst) {
        return this.map(ObjectHelper.<O>create(this, inst));
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

    public default Bool eq(final Object object) {
        return EqInst.create(object).attach(this);
    }
}
