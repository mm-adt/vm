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

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.type.algebra.WithOrder;
import org.mmadt.machine.object.model.type.algebra.WithRing;
import org.mmadt.machine.object.model.util.StringFactory;

import java.util.function.Supplier;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TQ<A extends WithRing<A>> extends TObj implements Q<A> {

    public static final Q ONE = new TQ<>(1, 1); // TODO: necessary for default value

    public TQ(final int low, final int high) {
        super(low == high ? (Supplier) () -> TInt.of(low) : (Supplier) () -> TInt.of(low, high));
        assert low <= high;
    }

    public TQ(final A obj) {
        super((Supplier) () -> obj);
    }

    public TQ(final A low, final A high) {
        super(low.equals(high) ? (Supplier) () -> TInt.of(low) : (Supplier) () -> TInt.of(low, high));
    }


    @Override
    public A object() {
        return ((Supplier<A>) this.value).get();
    }

    @Override
    public boolean constant() {
        return this.low().equals(this.high());
    }

    @Override
    public <B> B get() {
        return this.object().get();
    }

    @Override
    public <O extends Obj> O set(final Object object) {
        return super.set((Supplier) () -> object);
    }

    @Override
    public boolean test(final Obj obj) {
        return (((WithOrder<A>) obj.q().low()).gte(this.low()).<Boolean>get() && ((WithOrder<A>) obj.q().high()).lte(this.high()).<Boolean>get());
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

}
