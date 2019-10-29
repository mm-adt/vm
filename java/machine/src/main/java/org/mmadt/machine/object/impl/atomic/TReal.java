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

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Real;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.OperatorHelper;
import org.mmadt.processor.util.MinimalProcessor;

import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TReal extends TObj implements Real {

    private static final Real SOME = new TReal(null);
    private static final Real NONE = new TReal(null).q(0);
    private static final Real ALL = new TReal(null).q(0, Integer.MAX_VALUE);

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
        return TReal.of(Float.MAX_VALUE);
    }

    @Override
    public Real min() {
        return TReal.of(Float.MIN_VALUE);
    }

    @Override
    public Real one() {
        return OperatorHelper.unary(Tokens.ONE, x -> new TReal(1.0f), this);
    }

    @Override
    public Real zero() {
        return OperatorHelper.unary(Tokens.ZERO, x -> new TReal(0.0f), this);
    }

    @Override
    public Real neg() {
        return OperatorHelper.<Real>unary(Tokens.NEG, x -> new TReal(-x.java()), this);
    }

    @Override
    public Real plus(final Real object) {
        return OperatorHelper.binary(Tokens.PLUS, (x, y) -> new TReal(x.java() + y.java()), this, object);
    }

    @Override
    public Real mult(final Real object) {
        return OperatorHelper.binary(Tokens.MULT, (x, y) -> new TReal(x.java() * y.java()), this, object);
    }

    @Override
    public Bool gt(final Real object) {
        return OperatorHelper.bifunction(Tokens.GT, (x, y) -> TBool.of(x.java() > y.java()), this, object, TBool.of());
    }

    @Override
    public Bool eq(final Obj object) {
        return OperatorHelper.bifunction(Tokens.EQ, (x, y) -> TBool.of(object instanceof Real && x.java().equals(y.java())), this, (Real) object, TBool.of());
    }

    @Override
    public Bool lt(final Real object) {
        return OperatorHelper.bifunction(Tokens.LT, (x, y) -> TBool.of(x.java() < y.java()), this, object, TBool.of());
    }

    @Override
    public Real inv() {
        return OperatorHelper.<Real>unary(Tokens.INV, x -> new TReal(1.0f / x.java()), this);
    }

    @Override
    public Real set(final Object value) {
        return super.set(value.equals(-0.0f) ? 0.0f : value);
    }

    @Override
    public Iterable<Real> iterable() {
        return this.isInstance() ? List.of(this) : () -> new MinimalProcessor<Real, Real>(this.access()).iterator(this);
    }
}