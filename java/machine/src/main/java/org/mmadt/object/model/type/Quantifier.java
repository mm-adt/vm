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

package org.mmadt.object.model.type;

import org.mmadt.object.impl.TObj;
import org.mmadt.object.impl.TStream;
import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.atomic.Bool;
import org.mmadt.object.model.atomic.Int;
import org.mmadt.object.model.composite.Inst;
import org.mmadt.object.model.type.feature.WithOrder;
import org.mmadt.object.model.type.feature.WithRing;
import org.mmadt.object.model.util.StringFactory;

import java.util.function.Supplier;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class Quantifier<A extends WithRing<A>> extends TObj implements WithRing<Quantifier<A>> {

    public static final Quantifier zero = new Quantifier(0, 0);
    public static final Quantifier one = new Quantifier(1, 1);
    public static final Quantifier star = new Quantifier(0, Integer.MAX_VALUE);
    public static final Quantifier qmark = new Quantifier(0, 1);
    public static final Quantifier plus = new Quantifier(1, Integer.MAX_VALUE);

    public static Quantifier<Int> of(final int low, final int high) {
        return new Quantifier<>(low, high);
    }

    public Quantifier(final int low, final int high) {
        super((Supplier) () -> TInt.of(low, high));
        assert low <= high;
    }

    public Quantifier(final A obj) {
        super((Supplier) () -> obj);
    }

    public WithRing<A> object() {
        return ((Supplier<WithRing<A>>) this.value).get();
    }

    public A low() {
        return this.object().peak();
    }

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
    public <Q extends WithRing<Q>> Quantifier<Q> q() {
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
    public <O extends Obj> O set(Object object) {
        return this.object().set(object);
    }

    @Override
    public <O extends Obj> O q(Quantifier quantifier) {
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
    public Quantifier clone() {
        return new Quantifier<>((WithRing) this.object().clone());
    }

    @Override
    public int hashCode() {
        return this.object().hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof Quantifier &&
                this.object().equals(((Quantifier) object).object());
    }

    @Override
    public String toString() {
        return StringFactory.quantifier(this);
    }

    @Override
    public Quantifier<A> one() {
        return new Quantifier<>(this.object().one());
    }

    @Override
    public Quantifier<A> zero() {
        return new Quantifier<>(this.object().zero());
    }

    @Override
    public Quantifier<A> mult(final Quantifier<A> object) {
        return new Quantifier<>(this.object().mult((A) object.object()));
    }

    @Override
    public Quantifier<A> plus(Quantifier<A> object) {
        return new Quantifier<>(this.object().plus((A) object.object()));
    }

    public Quantifier<A> negate() {
        return new Quantifier<>(this.object().negate());
    }

    @Override
    public boolean test(final Obj object) {
        return null == object ?
                ((WithOrder<A>) this.low().get()).lte(this.low().zero()).get() : // TODO: need Order in the Interface
                (((WithOrder<A>) object.q().low()).gte(this.low()).<Boolean>get() && ((WithOrder<A>) object.q().high()).lte(this.high()).<Boolean>get());
    }

    public boolean isZero() {
        return this.low().isZero() && this.high().isZero();
    }

    @Override
    public Quantifier<A> and(final Obj obj) {
        return new Quantifier<>(this.low().set(TStream.of(this.low().mult(obj.peak()), this.high().mult(obj.last()))));
    }

    @Override
    public Quantifier<A> or(final Obj obj) {
        return new Quantifier<>(this.low().set(TStream.of(this.low().plus(obj.peak()), this.high().plus(obj.last()))));
    }
}
