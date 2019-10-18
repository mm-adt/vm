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

import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.atomic.Int;
import org.mmadt.object.model.type.feature.WithOrder;
import org.mmadt.object.model.type.feature.WithRing;
import org.mmadt.object.model.util.StringFactory;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class Quantifier<A extends WithRing<A>> implements Pattern { // TODO: this needs to be a thin wrapper that directs WithRing methods to underlying 2 stream

    public static final Quantifier zero = new Quantifier(0, 0);
    public static final Quantifier one = new Quantifier(1, 1);
    public static final Quantifier star = new Quantifier(0, Integer.MAX_VALUE);
    public static final Quantifier qmark = new Quantifier(0, 1);
    public static final Quantifier plus = new Quantifier(1, Integer.MAX_VALUE);

    private Supplier<A> obj;

    public static Quantifier<Int> of(final int low, final int high) {
        return new Quantifier<>(low, high);
    }

    public Quantifier(final int low, final int high) {
        assert low <= high;
        this.obj = () -> (A) TInt.of(low, high);
    }

    public Quantifier(final A obj) {
        this.obj = () -> obj;
    }

    public A object() {
        return this.obj.get();
    }

    public A low() {
        return this.obj.get().peak();
    }

    public A high() {
        return this.obj.get().last();
    }

    @Override
    public boolean constant() {
        return this.low().equals(this.high());
    }

    @Override
    public Pattern bind(final Bindings bindings) {
        return this;
    }

    @Override
    public boolean match(final Bindings bindings, final Obj object) {
        return this.test(object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.obj.get().toString());
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof Quantifier &&
                this.obj.get().equals(((Quantifier) object).obj.get());
    }

    @Override
    public String toString() {
        return StringFactory.quantifier(this);
    }

    public Quantifier<A> negate() {
        return new Quantifier<>(this.obj.get().negate());
    }

    public Quantifier<A> and(final Quantifier<A> quantifier) {
        // TODO: use [mult]
        int newLow = Quantifier.apply(() -> Math.multiplyExact(this.low().get(), quantifier.low().get()));
        int newHigh = Quantifier.apply(() -> Math.multiplyExact(this.high().get(), quantifier.high().get()));
        return new Quantifier<>(newLow, newHigh);
    }

    public Quantifier<A> or(final Quantifier<A> quantifier) {
        // TODO: use [plus]
        int newLow = Quantifier.apply(() -> Math.addExact(this.low().get(), quantifier.low().get()));
        int newHigh = Quantifier.apply(() -> Math.addExact(this.high().get(), quantifier.high().get()));
        return new Quantifier<>(newLow, newHigh);
    }

    private static Integer apply(final Supplier<Integer> function) {
        try {
            return function.get();
        } catch (ArithmeticException e) {
            return Integer.MAX_VALUE;
        }
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

}
