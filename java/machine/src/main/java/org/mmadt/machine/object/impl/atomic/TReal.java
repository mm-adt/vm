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
import org.mmadt.machine.object.impl.TType;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Real;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.util.FunctionUtils;

import static org.mmadt.language.compiler.Tokens.REAL;

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
        return ObjectHelper.create(TReal::new, objects);
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
        return TReal.of(1.0f);
    }

    @Override
    public Real zero() {
        return TReal.of(0.0f);
    }

    @Override
    public Real negate() {
        return FunctionUtils.<Real, Float>monad(this, x -> -x);
    }

    @Override
    public Real plus(final Real object) {
        return FunctionUtils.<Real, Float>monad(this, object, (x, y) -> x + y);
    }

    @Override
    public Real mult(final Real object) {
        return FunctionUtils.<Real, Float>monad(this, object, (x, y) -> x * y);
    }

    @Override
    public Bool gt(final Real object) {
        return TBool.of(this.java() > object.java());
    }

    @Override
    public Bool eq(final Obj object) {
        return TBool.of(object instanceof Real && this.java().equals(object.get()));
    }

    @Override
    public Bool lt(final Real object) {
        return TBool.of(this.java() < object.java());
    }

    @Override
    public Real inverse() {
        return FunctionUtils.<Real, Float>monad(this, x -> 1.0f / x);
    }

    @Override
    public Real set(final Object value) {
        return super.set(value.equals(-0.0f) ? 0.0f : value);
    }
}