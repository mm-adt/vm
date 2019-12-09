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

import java.util.function.Function;
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
        return !this.isReference() ? this.set(0) : ZeroInst.<Int>create().attach(this);
    }

    @Override
    public Int one() {
        return !this.isReference() ? this.set(1) : OneInst.<Int>create().attach(this);
    }

    @Override
    public Int neg() {
        return this.isInstance() ? this.set(-this.java()) : NegInst.<Int>create().attach(this);
    }

    @Override
    public Int minus(final Int integer) {
        return (this.isInstance() && integer.isInstance()) ?
                this.set(tryCatch(() -> Math.addExact(this.java(), -integer.java()), Integer.MIN_VALUE)) :
                MinusInst.<Int>create(integer).attach(this);
    }

    @Override
    public Int plus(final Int integer) {
        // TODO: MAKE CLEAN AND EASILY ADAPTABLE TO OTHER INSTRUCTIONS
        if (null != this.state().apply(PlusInst.create(integer)))
            return (Int) ((Function) this.state().apply(PlusInst.create(integer))).apply(this);
        return (this.isInstance() && integer.isInstance()) ?
                this.set(tryCatch(() -> Math.addExact(this.java(), integer.java()), Integer.MAX_VALUE)) :
                PlusInst.<Int>create(integer).attach(this);
    }

    @Override
    public Int mult(final Int integer) {
        return (this.isInstance() && integer.isInstance()) ?
                this.set(tryCatch(() -> Math.multiplyExact(this.java(), integer.java()), Integer.MAX_VALUE)) :
                MultInst.<Int>create(integer).attach(this);
    }

    @Override
    public Bool gt(final Int integer) {
        return (this.isInstance() && integer.isInstance()) ?
                TBool.via(this).set(this.java() > integer.java()) :
                GtInst.<Int>create(integer).attach(this, TBool.via(this));
    }

    @Override
    public Bool gte(final Int integer) {
        return (this.isInstance() && integer.isInstance()) ?
                TBool.via(this).set(this.java() >= integer.java()) :
                GteInst.<Int>create(integer).attach(this, TBool.via(this));
    }

    @Override
    public Bool eq(final Obj obj) {
        return this.isInstance() ?
                TBool.via(this).set(obj instanceof Int && this.java().equals(((Int) obj).java())) :
                EqInst.<Int>create(obj).attach(this, TBool.via(this));
    }

    @Override
    public Bool lt(final Int integer) {
        return (this.isInstance() && integer.isInstance()) ?
                TBool.via(this).set(this.java() < integer.java()) :
                LtInst.<Int>create(integer).attach(this, TBool.via(this));
    }

    @Override
    public Bool lte(final Int integer) {
        return (this.isInstance() && integer.isInstance()) ?
                TBool.via(this).set(this.java() <= integer.java()) :
                LteInst.<Int>create(integer).attach(this, TBool.via(this));
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
