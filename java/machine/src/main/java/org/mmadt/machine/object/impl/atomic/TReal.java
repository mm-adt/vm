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
import org.mmadt.machine.object.impl.composite.inst.map.DivInst;
import org.mmadt.machine.object.impl.composite.inst.map.EqInst;
import org.mmadt.machine.object.impl.composite.inst.map.GtInst;
import org.mmadt.machine.object.impl.composite.inst.map.InvInst;
import org.mmadt.machine.object.impl.composite.inst.map.LtInst;
import org.mmadt.machine.object.impl.composite.inst.map.MinusInst;
import org.mmadt.machine.object.impl.composite.inst.map.MultInst;
import org.mmadt.machine.object.impl.composite.inst.map.NegInst;
import org.mmadt.machine.object.impl.composite.inst.map.OneInst;
import org.mmadt.machine.object.impl.composite.inst.map.PlusInst;
import org.mmadt.machine.object.impl.composite.inst.map.ZeroInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Real;
import org.mmadt.machine.object.model.util.ObjectHelper;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TReal extends TObj implements Real {

    private static final Real SOME = new TReal(null);
    private static final Real NONE = new TReal(null).q(0);
    private static final Real ALL = new TReal(null).q(0, Integer.MAX_VALUE);
    private static final Real ZERO = new TReal(0.0f);
    private static final Real ONE = new TReal(1.0f);
    private static final Real MAX = new TReal(Float.MAX_VALUE);
    private static final Real MIN = new TReal(Float.MIN_VALUE);

    private TReal(final Object value) {
        super(value);
    }

    public static Real some() {
        return SOME;
    }

    public static Real all() {
        return ALL;
    }

    public static Real none() {
        return NONE;
    }

    public static Real of(final Object... objects) {
        return ObjectHelper.make(TReal::new, objects);
    }

    @Override
    public Real max() {
        return MAX;
    }

    @Override
    public Real min() {
        return MIN;
    }

    @Override
    public Real one() {
        return OneInst.create(this, ONE);
    }

    @Override
    public Real zero() {
        return ZeroInst.create(this, ZERO);
    }

    @Override
    public Real neg() {
        return NegInst.create(() -> new TReal(-this.java()), this);
    }

    @Override
    public Real minus(final Real real) {
        return MinusInst.create(() -> new TReal(this.java() - real.java()), this, real);
    }

    @Override
    public Real plus(final Real real) {
        return PlusInst.create(() -> new TReal(this.java() + real.java()), this, real);
    }

    @Override
    public Real mult(final Real real) {
        return MultInst.create(() -> new TReal(this.java() * real.java()), this, real);
    }

    @Override
    public Real div(final Real real) {
        return DivInst.create(() -> new TReal(this.java() / real.java()), this, real);
    }

    @Override
    public Bool gt(final Real real) {
        return GtInst.create(() -> TBool.of(this.java() > real.java()), this, real);
    }

    @Override
    public Bool eq(final Obj obj) {
        return EqInst.create(() -> TBool.of(obj instanceof Real && this.java().equals(((Real) obj).java())), this, obj);
    }

    @Override
    public Bool lt(final Real real) {
        return LtInst.create(() -> TBool.of(this.java() < real.java()), this, real);
    }

    @Override
    public Real inv() {
        return InvInst.create(() -> new TReal(1.0f / this.java()), this);
    }

    @Override
    public Real set(final Object value) {
        return super.set(value.equals(-0.0f) ? 0.0f : value);
    }
}