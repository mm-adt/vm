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

package org.mmadt.language;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.object.impl.TObj;
import org.mmadt.object.impl.composite.TInst;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.composite.Inst;

import java.util.Objects;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class Traversal {

    private Inst bytecode;

    private Traversal(final Inst inst) {
        this.bytecode = inst;
    }

    Traversal() {
        this(TInst.of(Tokens.ID));
    }

    public Traversal branch(final Object... branches) {
        return this.compose(TInst.of(Tokens.BRANCH, args(branches)));
    }

    public static Traversal __() {
        return new Traversal();
    }

    public Traversal id() {
        return this.compose(TInst.of(Tokens.ID));
    }

    public Traversal count() {
        return this.compose(TInst.of(Tokens.COUNT));
    }

    public static Traversal db() {
        return new Traversal(TInst.of(Tokens.DB));
    }

    public Traversal drop(final Object key) {
        return this.compose(TInst.of(Tokens.DROP, arg(key)));
    }

    public Traversal and(final Object... objects) {
        return this.compose(TInst.of(Tokens.AND, args(objects)));
    }

    public Traversal dedup(final Object... objects) {
        return this.compose(TInst.of(Tokens.DEDUP, args(objects)));
    }

    public Traversal eq(final Object obj) {
        return this.compose(TInst.of(Tokens.EQ, arg(obj)));
    }

    public Traversal get(final Object key) {
        return this.compose(TInst.of(Tokens.GET, arg(key)));
    }

    public Traversal groupCount(final Object key) {
        return this.compose(TInst.of(Tokens.GROUPCOUNT, arg(key)));
    }

    public Traversal gt(final Object obj) {
        return this.compose(TInst.of(Tokens.GT, arg(obj)));
    }

    public Traversal is(final Object bool) {
        return this.compose(TInst.of(Tokens.IS, arg(bool)));
    }

    public Traversal map(final Object obj) {
        return this.compose(TInst.of(Tokens.MAP, arg(obj)));
    }

    public Traversal minus(final Object obj) {
        return this.compose(TInst.of(Tokens.MINUS, arg(obj)));
    }

    public Traversal mult(final Object obj) {
        return this.compose(TInst.of(Tokens.MULT, arg(obj)));
    }

    public Traversal one() {
        return this.compose(TInst.of(Tokens.ONE));
    }

    public Traversal plus(final Object obj) {
        return this.compose(TInst.of(Tokens.PLUS, arg(obj)));
    }

    public Traversal put(final Object key, final Object value) {
        return this.compose(TInst.of(Tokens.PUT, arg(key), arg(value)));
    }

    public Traversal a(final Object obj) {
        return new Traversal(TInst.of(Tokens.A, arg(obj)));
    }

    public Traversal start(final Object... objects) {
        return new Traversal(TInst.of(Tokens.START, args(objects)));
    }


    public Traversal sum() {
        return this.compose(TInst.of(Tokens.SUM));
    }


    public Traversal type() {
        return this.compose(TInst.of(Tokens.TYPE));
    }

    public Traversal zero() {
        return this.compose(TInst.of(Tokens.ZERO));
    }

    public Traversal as(final String key) {
        this.bytecode = this.bytecode.as(key);
        return this;
    }


    //////////////////

    public Inst bytecode() {
        return this.bytecode;
    }

    @Override
    public String toString() {
        return this.bytecode.toString();
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Traversal && ((Traversal) other).bytecode.equals(this.bytecode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), this.bytecode);
    }

    //////////////////

    private Traversal compose(final Inst inst) {
        this.bytecode = this.bytecode.mult(inst);
        return this;
    }

    private static Obj[] args(final Object[] objects) {
        final Obj[] objs = new Obj[objects.length];
        for (int i = 0; i < objects.length; i++) {
            objs[i] = Traversal.arg(objects[i]);
        }
        return objs;
    }

    private static Obj arg(final Object object) {
        return object instanceof Traversal ? ((Traversal) object).bytecode : TObj.from(object);
    }
}
