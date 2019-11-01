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

import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.composite.TQ;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.type.algebra.WithOrder;
import org.mmadt.machine.object.model.type.algebra.WithOrderedRing;

import java.util.function.UnaryOperator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Q<A extends WithOrderedRing<A>> extends Obj, WithOrderedRing<Q<A>> { // TODO: WithOrderedRing?

    public enum Tag implements UnaryOperator<Q> {
        zero, one, star, qmark, plus;

        @Override
        public Q apply(final Q quantifier) {
            switch (this) {
                case zero:
                    return new TQ<>(quantifier.peek().zero(), quantifier.peek().zero());
                case one:
                    return new TQ<>(quantifier.peek().one(), quantifier.peek().one());
                case star:
                    return new TQ<>(quantifier.peek().zero(), quantifier.last().max());
                case qmark:
                    return new TQ<>(quantifier.peek().zero(), quantifier.last().one());
                case plus:
                    return new TQ<>(quantifier.peek().one(), quantifier.last().max());
                default:
                    throw new RuntimeException("Undefined shorthand: " + this);
            }
        }
    }

    // this is necessary as the quantifier is really wrapped in a supplier to avoid stackoverflow during construction
    public A object();

    @Override
    public default A peek() {
        return this.object().peek();
    }

    @Override
    public default A last() {
        return this.object().last();
    }

    @Override
    public default Q<A> mult(final Q<A> object) {
        return new TQ<>(this.peek().mult(object.peek()), this.last().mult(object.last()));
    }

    @Override
    public default Q<A> plus(final Q<A> object) {
        return new TQ<>(this.peek().plus(object.peek()), this.last().plus(object.last()));
    }

    @Override
    public default Q<A> neg() {
        return new TQ<>(this.peek().neg(), this.last().neg());
    }

    @Override
    public default Bool gt(final Q<A> object) {
        return TBool.of(this.last().gt(object.last()));
    }

    @Override
    public default Bool lt(Q<A> object) {
        return TBool.of(this.last().lt(object.last()));
    }

    @Override
    public default Q<A> max() {
        return new TQ<>(this.peek().max(), this.last().max());
    }

    @Override
    public default Q<A> min() {
        return new TQ<>(this.peek().min(), this.last().min());
    }

    public default Q<A> and(final Q<A> obj) {
        return new TQ<>(this.peek().mult(obj.peek()), this.last().mult(obj.last()));
    }

    public default Q<A> or(final Q<A> obj) {
        return new TQ<>(this.peek().plus(obj.peek()), this.last().plus(obj.last()));
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
        return this.peek().isZero() && ((WithOrder) this.last()).isMax();
    }

    public default boolean isPlus() {
        return this.peek().isOne() && ((WithOrder) this.last()).isMax();
    }

    public default boolean isQMark() {
        return this.peek().isZero() && this.last().isOne();
    }

    @Override
    public default boolean isZero() {
        return this.peek().isZero() && this.last().isZero();
    }

    @Override
    public default boolean isOne() {
        return this.peek().isOne() && this.last().isOne();
    }


}
