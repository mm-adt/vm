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

package org.mmadt.object.impl.atomic;

import org.mmadt.object.impl.TObj;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.atomic.Bool;
import org.mmadt.object.model.atomic.Int;
import org.mmadt.object.model.type.PRel;
import org.mmadt.object.model.util.ObjectHelper;
import org.mmadt.util.FunctionUtils;

import static org.mmadt.language.compiler.Tokens.INT;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TInt extends TObj implements Int {

    private static final Int SOME = new TInt(null);
    private static final Int ALL = new TInt(null).q(0, Integer.MAX_VALUE);
    private static final Int NONE = new TInt(null).q(0);

    private static final Int ZERO = new TInt(0);
    private static final Int ONE = new TInt(1);
    private static final Int TWO = new TInt(2);

    private TInt(final Object value) {
        super(value);
        this.symbol = INT;
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
        return ObjectHelper.create(TInt::new, objects);
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

    @Override
    public Int max() {
        return TInt.of(Integer.MAX_VALUE);
    }

    @Override
    public Int min() {
        return TInt.of(Integer.MIN_VALUE);
    }

    @Override
    public Int zero() {
        return ZERO;
    }

    @Override
    public Int one() {
        return ONE;
    }

    @Override
    public Int negate() {
        return this.isType() ? this.q(this.q().negate()) : FunctionUtils.<Int, Integer>monad(this, x -> -x); // TODO: negate types?
    }

    @Override
    public Int plus(final Int object) {
        return FunctionUtils.<Int, Integer>monad(this, object, Math::addExact);
    }

    @Override
    public Int mult(final Int object) {
        return FunctionUtils.<Int, Integer>monad(this, object, Math::multiplyExact);
    }

    @Override
    public Bool gt(final Int object) {
        // TODO: generalize monad to functions, not just operators
        //  return IteratorUtils.monad(this,(TInt)object,(x,y) -> x > y);
        return TBool.of(((Integer) this.value).doubleValue() > object.<Number>get().doubleValue());
    }

    @Override
    public Bool eq(Obj object) {
        return TBool.of(object instanceof Int && ((Integer) this.value).doubleValue() == object.<Number>get().doubleValue());
    }

    @Override
    public Bool lt(final Int object) {
        return TBool.of(((Integer) this.value).doubleValue() < object.<Number>get().doubleValue());
    }

    ///// HELPER METHODS

    public static Int gt(int object) {
        return new TInt(new PRel(PRel.Rel.GT, TInt.of(object)));
    }


    public static Int gte(int object) {
        return new TInt(new PRel(PRel.Rel.GTE, TInt.of(object)));
    }


    public static Int lte(int object) {
        return new TInt(new PRel(PRel.Rel.LTE, TInt.of(object)));
    }


    public static Int lt(int object) {
        return new TInt(new PRel(PRel.Rel.LT, TInt.of(object)));
    }
}
