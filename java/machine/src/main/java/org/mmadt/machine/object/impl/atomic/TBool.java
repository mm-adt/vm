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
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.util.ObjectHelper;

import static org.mmadt.language.compiler.Tokens.BOOL;


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TBool extends TObj implements Bool {

    private static final Bool SOME = new TBool(null);
    private static final Bool NONE = new TBool(null).q(0);
    private static final Bool ALL = new TBool(null).q(0, Integer.MAX_VALUE);
    private static final Bool TRUE = new TBool(Boolean.TRUE);
    private static final Bool FALSE = new TBool(Boolean.FALSE);


    private TBool(final Object value) {
        super(value);
        this.symbol = BOOL;
    }

    public static Bool all() {
        return ALL;
    }

    public static Bool none() {
        return NONE;
    }

    public static Bool some() {
        return SOME;
    }

    public static Bool some(final int count) {
        return TBool.some(count, count);
    }

    public static Bool some(final int low, final int high) {
        return new TBool(null).q(low, high);
    }

    public static Bool of(final Object... objects) {
        return ObjectHelper.create(TBool::new, objects);
    }

    @Override
    public Bool one() {
        return TRUE;
    }

    @Override
    public Bool zero() {
        return FALSE;
    }

    @Override
    public Bool negate() {
        return TBool.of(!(Boolean) this.value);
    }

    @Override
    public Bool mult(final Bool bool) {
        return TBool.of((Boolean) this.value && bool.<Boolean>get());
    }

    @Override
    public Bool plus(final Bool bool) {
        return TBool.of(exclusiveOr(exclusiveOr((Boolean) this.value, bool.get()), this.mult(bool).<Boolean>get()));
    }

    @Override
    public Bool minus(final Bool bool) {
        return this.plus(bool).negate();
    }

    private static final boolean exclusiveOr(final boolean a, final boolean b) {
        return (a && !b) || (!a && b);
    }

}
