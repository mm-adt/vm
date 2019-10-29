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
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.OperatorHelper;
import org.mmadt.machine.object.model.util.StringFactory;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TStr extends TObj implements Str {

    private static final String MAX_VALUE = "zzzzzzzzzzzz";

    private static final Str SOME = new TStr(null);
    private static final Str NONE = new TStr(null).q(0);
    private static final Str ALL = new TStr(null).q(0, Integer.MAX_VALUE);

    private TStr(final Object value) {
        super(value);
    }

    public static Str all() {
        return ALL;
    }

    public static Str none() {
        return NONE;
    }

    public static Str some() {
        return SOME;
    }

    public static Str of(final Object... objects) {
        return ObjectHelper.make(TStr::new, objects);
    }

    @Override
    public String toString() {
        return StringFactory.string(this);
    }

    @Override
    public Bool gt(final Str object) {
        return OperatorHelper.bifunction(Tokens.GT, (x, y) -> TBool.of(x.java().compareTo(y.java()) > 0), this, object, TBool.of());
    }

    @Override
    public Bool eq(final Obj object) {
        return OperatorHelper.bifunction(Tokens.EQ, (x, y) -> TBool.of(object instanceof Str && x.get().equals(y.get())), this, object, TBool.of());
    }

    @Override
    public Bool lt(final Str object) {
        return OperatorHelper.bifunction(Tokens.LT, (x, y) -> TBool.of(x.java().compareTo(y.java()) < 0), this, object, TBool.of());
    }

    @Override
    public Bool regex(final Str pattern) {
        return TBool.of(this.java().matches(pattern.java()));
    } // TODO: [regex,...]

    @Override
    public Str plus(final Str object) {
        return OperatorHelper.binary(Tokens.PLUS, (x, y) -> new TStr(x.java().concat(y.java())), this, object);
    }

    @Override
    public Str zero() {
        return OperatorHelper.unary(Tokens.ZERO, x -> new TStr(Tokens.EMPTY), this);
    }

    @Override
    public Str max() {
        return new TStr(MAX_VALUE);
    }

    @Override
    public Str min() {
        return new TStr(Tokens.EMPTY);
    }
}
