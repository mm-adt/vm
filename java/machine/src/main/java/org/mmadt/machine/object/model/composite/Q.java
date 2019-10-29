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

package org.mmadt.machine.object.model.composite;

import org.mmadt.machine.object.impl.composite.TQ;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.type.algebra.WithOrder;
import org.mmadt.machine.object.model.type.algebra.WithRing;

import java.util.function.UnaryOperator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Q<A extends WithRing<A>> extends Obj, WithRing<Q<A>> { // TODO: WithOrderedRing?

    public enum Tag implements UnaryOperator<Q> {
        zero, one, star, qmark, plus;

        @Override
        public Q apply(final Q quantifier) {
            switch (this) {
                case zero:
                    return new TQ<>(quantifier.low().zero());
                case one:
                    return new TQ<>(quantifier.low().one());
                case star:
                    return new TQ<>(quantifier.low().zero(), (WithRing) ((WithOrder) quantifier.high()).max());
                case qmark:
                    return new TQ<>(quantifier.low().zero(), quantifier.high().one());
                case plus:
                    return new TQ<>(quantifier.low().one(), (WithRing) ((WithOrder) quantifier.high()).max());
                default:
                    throw new RuntimeException("Undefined shorthand: " + this);
            }
        }
    }

    // this is necessary as the quantifier is really wrapped in a supplier to avoid stackoverflow during construction
    public A object();

    @Override
    public default <O extends Obj> O peek() {
        return (O) this.object().peek();
    }

    @Override
    public default <O extends Obj> O last() {
        return (O) this.object().last();
    }

    public default A low() {
        return this.object().peek();
    }

    public default A high() {
        return this.object().last();
    }

    @Override
    public default Q<A> mult(final Q<A> object) {
        return new TQ<>(this.object().mult(object.object()));
    }

    @Override
    public default Q<A> plus(final Q<A> object) {
        return new TQ<>(this.object().plus(object.object()));
    }

    @Override
    public default Q<A> neg() {
        return new TQ<>(this.low().neg(), this.high().neg());
    }

    public default Q<A> and(final Q<A> obj) {
        return new TQ<>(this.low().mult(obj.low()), this.high().mult(obj.high()));
    }

    public default Q<A> or(final Q<A> obj) {
        return new TQ<>(this.low().plus(obj.low()), this.high().plus(obj.high()));
    }

    @Override
    public default Q<A> one() {
        return Tag.one.apply(this);
    }

    @Override
    public default Q<A> zero() {
        return Tag.zero.apply(this);
    }

    public default Q<A> qmark() {
        return Tag.qmark.apply(this);
    }

    public default Q<A> plus() {
        return Tag.plus.apply(this);
    }

    public default Q<A> star() {
        return Tag.star.apply(this);
    }

    public default boolean isStar() {
        return this.low().isZero() && ((WithOrder) this.high()).isMax();
    }

    public default boolean isPlus() {
        return this.low().isOne() && ((WithOrder) this.high()).isMax();
    }

    public default boolean isQMark() {
        return this.low().isZero() && this.high().isOne();
    }

    @Override
    public default boolean isZero() {
        return this.low().isZero() && this.high().isZero();
    }

    @Override
    public default boolean isOne() {
        return this.low().isOne() && this.high().isOne();
    }


}
