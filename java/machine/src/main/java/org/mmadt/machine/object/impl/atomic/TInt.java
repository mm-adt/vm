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
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.type.PRel;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.OperatorHelper;

import static org.mmadt.machine.object.model.type.PRel.Rel.GT;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TInt extends TObj implements Int {

    private static final Int SOME = new TInt(null);
    private static final Int ALL = new TInt(null).q(0, Integer.MAX_VALUE);
    private static final Int NONE = new TInt(null).q(0);

    private static final Int MAX = new TInt(Integer.MAX_VALUE);
    private static final Int MIN = new TInt(Integer.MIN_VALUE);
    private static final Int ZERO = new TInt(0);
    private static final Int ONE = new TInt(1);
    private static final Int TWO = new TInt(2);

    private TInt(final Object value) {
        super(value);
    }

    public static Int all() {
        return ALL;
    }

    public static Int none() {
        return NONE;
    }

    public static Int some() {
        return SOME;
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
        return ZERO;
    }

    public static Int oneInt() {
        return ONE;
    }

    public static Int twoInt() {
        return TWO;
    }

    /////////////////////////////////

    @Override
    public Int max() {
        return MAX;
    }

    @Override
    public Int min() {
        return MIN;
    }

    @Override
    public Int zero() {
        return OperatorHelper.unary(Tokens.ZERO, x -> new TInt(0), this);
    }

    @Override
    public Int one() {
        return OperatorHelper.unary(Tokens.ONE, x -> new TInt(1), this);
    }

    @Override
    public Int neg() {
        return OperatorHelper.<Int>unary(Tokens.NEG, x -> new TInt(-x.java()), this);
    }

    @Override
    public Int plus(final Int object) {
        return OperatorHelper.binary(Tokens.PLUS, (x, y) -> new TInt(OperatorHelper.tryCatch(() -> Math.addExact(x.java(), y.java()), Integer.MAX_VALUE)), this, object);
    }

    @Override
    public Int mult(final Int object) {
        return OperatorHelper.binary(Tokens.MULT, (x, y) -> new TInt(OperatorHelper.tryCatch(() -> Math.multiplyExact(x.java(), y.java()), Integer.MAX_VALUE)), this, object);
    }

    @Override
    public Bool gt(final Int object) {
        return OperatorHelper.bifunction(Tokens.GT, (x, y) -> TBool.of(x.java() > y.java()), this, object, TBool.of());
    }

    @Override
    public Bool eq(final Obj object) {
        return OperatorHelper.bifunction(Tokens.EQ, (x, y) -> TBool.of(object instanceof Int && x.get().equals(y.get())), this, (Int) object, TBool.of());
    }

    @Override
    public Bool lt(final Int object) {
        return OperatorHelper.bifunction(Tokens.LT, (x, y) -> TBool.of(x.java() < y.java()), this, object, TBool.of());
    }

    ///// HELPER METHODS

    public static Int gt(int object) {
        return new TInt(new PRel(GT, TInt.of(object)));
    }

}
