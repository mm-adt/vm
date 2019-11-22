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

package org.mmadt.machine.object.impl.atomic;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.composite.inst.map.EqInst;
import org.mmadt.machine.object.impl.composite.inst.map.GtInst;
import org.mmadt.machine.object.impl.composite.inst.map.GteInst;
import org.mmadt.machine.object.impl.composite.inst.map.LtInst;
import org.mmadt.machine.object.impl.composite.inst.map.LteInst;
import org.mmadt.machine.object.impl.composite.inst.map.MinusInst;
import org.mmadt.machine.object.impl.composite.inst.map.MultInst;
import org.mmadt.machine.object.impl.composite.inst.map.NegInst;
import org.mmadt.machine.object.impl.composite.inst.map.OneInst;
import org.mmadt.machine.object.impl.composite.inst.map.PlusInst;
import org.mmadt.machine.object.impl.composite.inst.map.ZeroInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.util.ObjectHelper;

import java.util.function.Supplier;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TInt extends TObj implements Int {

    private TInt(final Object value) {
        super(value);
    }

    public static Int all() {
        return new TInt(null).q(0, Integer.MAX_VALUE);
    }

    public static Int none() {
        return new TInt(null).q(0);
    }

    public static Int some() {
        return new TInt(null);
    }

    public static Int some(final int low, final int high) {
        return new TInt(null).q(low, high);
    }

    public static Int some(final int count) {
        return TInt.some(count, count);
    }

    public static Int of(final Object... objects) {
        return ObjectHelper.make(TInt::new, objects);
    }

    public static Int zeroInt() {
        return new TInt(0);
    }

    public static Int oneInt() {
        return new TInt(1);
    }

    public static Int twoInt() {
        return new TInt(2);
    }

    /////////////////////////////////

    @Override
    public Int max() {
        return this.set(Integer.MAX_VALUE);
    } // TODO: these should be made [min]/[max]

    @Override
    public Int min() {
        return this.set(Integer.MIN_VALUE);
    }

    @Override
    public Int zero() {
        return this.q().constant() ? this.set(0) : this.mapTo(ZeroInst.create());
    }

    @Override
    public Int one() {
        return this.q().constant() ? this.set(1) : this.mapTo(OneInst.create());
    }

    @Override
    public Int neg() {
        return this.isInstance() ? this.set(-this.java()) : this.mapTo(NegInst.create());
    }

    @Override
    public Int minus(final Int integer) {
        return (this.isInstance() && integer.isInstance()) ?
                this.set(tryCatch(() -> Math.addExact(this.java(), -integer.java()), Integer.MIN_VALUE)) :
                this.mapTo(MinusInst.create(integer));
    }

    @Override
    public Int plus(final Int integer) {
        return (this.isInstance() && integer.isInstance()) ?
                this.set(tryCatch(() -> Math.addExact(this.java(), integer.java()), Integer.MAX_VALUE)) :
                this.mapTo(PlusInst.create(integer));
    }

    @Override
    public Int mult(final Int integer) {
        return (this.isInstance() && integer.isInstance()) ?
                this.set(tryCatch(() -> Math.multiplyExact(this.java(), integer.java()), Integer.MAX_VALUE)) :
                this.mapTo(MultInst.create(integer));
    }

    @Override
    public Bool gt(final Int integer) {
        return (this.isInstance() && integer.isInstance()) ?
                TBool.from(this).set(this.java() > integer.java()) :
                TBool.from(this).mapFrom(GtInst.create(integer));  // TODO: change to mapTo() syntax when test cases changed
    }

    @Override
    public Bool gte(final Int integer) {
        return (this.isInstance() && integer.isInstance()) ?
                TBool.from(this).set(this.java() >= integer.java()) :
                TBool.from(this).mapFrom(GteInst.create(integer));
    }

    @Override
    public Bool eq(final Obj obj) {
        return this.isInstance() ?
                TBool.from(this).set(obj instanceof Int && this.java().equals(((Int) obj).java())) :
                TBool.from(this).mapFrom(EqInst.create(obj));
    }

    @Override
    public Bool lt(final Int integer) {
        return (this.isInstance() && integer.isInstance()) ?
                TBool.from(this).set(this.java() < integer.java()) :
                TBool.from(this).mapFrom(LtInst.create(integer));
    }

    @Override
    public Bool lte(final Int integer) {
        return (this.isInstance() && integer.isInstance()) ?
                TBool.from(this).set(this.java() <= integer.java()) :
                TBool.from(this).mapFrom(LteInst.create(integer));
    }

    ///// HELPER METHODS

    private static Integer tryCatch(final Supplier<Integer> function, final Integer failValue) {
        try {
            return function.get();
        } catch (final ArithmeticException e) {
            return failValue;
        }
    }
}
