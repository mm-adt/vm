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

package org.mmadt.object.impl.composite;

import org.mmadt.object.impl.TObj;
import org.mmadt.object.impl.TStream;
import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.atomic.Bool;
import org.mmadt.object.model.atomic.Int;
import org.mmadt.object.model.composite.Inst;
import org.mmadt.object.model.composite.Q;
import org.mmadt.object.model.type.Bindings;
import org.mmadt.object.model.type.PMap;
import org.mmadt.object.model.type.feature.WithOrder;
import org.mmadt.object.model.type.feature.WithRing;
import org.mmadt.object.model.util.StringFactory;

import java.util.function.Supplier;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TQ<A extends WithRing<A>> extends TObj implements Q<A> {

    public static final Q zero = new TQ(0, 0);
    public static final Q one = new TQ(1, 1);
    public static final Q star = new TQ(0, Integer.MAX_VALUE);
    public static final Q qmark = new TQ(0, 1);
    public static final Q plus = new TQ(1, Integer.MAX_VALUE);

    public static TQ<Int> of(final int low, final int high) {
        return new TQ<>(low, high);
    }

    public TQ(final int low, final int high) {
        super((Supplier) () -> TInt.of(low, high));
        assert low <= high;
    }

    public TQ(final A obj) {
        super((Supplier) () -> obj);
    }

    @Override
    public A object() {
        return ((Supplier<A>) this.value).get();
    }

    @Override
    public A low() {
        return this.object().peak();
    }

    @Override
    public A high() {
        return this.object().last();
    }

    @Override
    public boolean constant() {
        return this.low().equals(this.high());
    }

    @Override
    public String symbol() {
        return this.object().symbol();
    }

    @Override
    public <B> B get() {
        return this.object().get();
    }

    @Override
    public <B extends WithRing<B>> Q<B> q() {
        return this.object().q();
    }

    @Override
    public String variable() {
        return this.object().variable();
    }

    @Override
    public Inst access() {
        return this.object().access();
    }

    @Override
    public PMap<Inst, Inst> instructions() {
        return this.object().instructions();
    }

    @Override
    public PMap<Obj, Obj> members() {
        return this.object().members();
    }

    @Override
    public Bool eq(Obj object) {
        return this.object().eq(object);
    }

    @Override
    public <O extends Obj> O type(O type) {
        return this.object().type(type);
    }

    @Override
    public Obj type() {
        return this.object().type();
    }

    @Override
    public <O extends Obj> O push(O obj) {
        return this.object().push(obj);
    }

    @Override
    public <O extends Obj> O pop() {
        return this.object().pop();
    }

    @Override
    public <O extends Obj> O set(final Object object) {
        return super.set((Supplier) () -> object);
    }

    @Override
    public <O extends Obj> O q(final Q quantifier) {
        return this.object().q(quantifier);
    }

    @Override
    public <O extends Obj> O as(String variable) {
        return this.object().as(variable);
    }

    @Override
    public <O extends Obj> O access(Inst access) {
        return this.object().access(access);
    }

    @Override
    public <O extends Obj> O inst(Inst instA, Inst instB) {
        return null;
    }

    @Override
    public <O extends Obj> O symbol(String symbol) {
        return null;
    }

    @Override
    public <O extends Obj> O insts(PMap<Inst, Inst> insts) {
        return null;
    }

    @Override
    public Obj bind(final Bindings bindings) {
        return this;
    }

    @Override
    public boolean match(final Bindings bindings, final Obj object) {
        return this.test(object);
    }

    @Override
    public TQ<A> clone() {
        return new TQ<>((A) this.object().clone());
    }

    @Override
    public int hashCode() {
        return this.object().toString().hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof Q &&
                this.high().equals(((Q) object).high()) &&
                this.low().equals(((Q) object).low()); // TODO
    }

    @Override
    public String toString() {
        return StringFactory.quantifier(this);
    }

    @Override
    public Q<A> one() {
        return new TQ<>(this.object().one());
    }

    @Override
    public Q<A> zero() {
        return new TQ<>(this.object().zero());
    }

    @Override
    public Q<A> mult(final Q<A> object) {
        return new TQ<>(this.object().mult(object.object()));
    }

    @Override
    public Q<A> plus(final Q<A> object) {
        return new TQ<>(this.object().plus(object.object()));
    }

    public Q<A> negate() {
        return new TQ<>(this.object().negate());
    }

    @Override
    public boolean test(final Obj object) {
        return null == object ?
                ((WithOrder<A>) this.low().get()).lte(this.low().zero()).get() : // TODO: need Order in the Interface
                (((WithOrder<A>) object.q().low()).gte(this.low()).<Boolean>get() && ((WithOrder<A>) object.q().high()).lte(this.high()).<Boolean>get());
    }

    @Override
    public boolean isZero() {
        return this.low().isZero() && this.high().isZero();
    }


    @Override
    public boolean isOne() {
        return this.low().isOne() && this.high().isOne();
    }

    @Override
    public Q<A> and(final Q<A> obj) {
        return new TQ<>(this.low().set(TStream.of(this.low().mult(obj.peak()), this.high().mult(obj.last()))));
    }

    @Override
    public Q<A> or(final Q<A> obj) {
        return new TQ<>(this.low().set(TStream.of(this.low().plus(obj.peak()), this.high().plus(obj.last()))));
    }
}
