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
public final class Query {

    private Inst bytecode;

    private Query(final Inst inst) {
        this.bytecode = inst;
    }

    Query() {
        this(TInst.of(Tokens.ID));
    }

    public Query branch(final Object... branches) {
        return this.compose(TInst.of(Tokens.BRANCH, args(branches)));
    }

    public Query id() {
        return this.compose(TInst.of(Tokens.ID));
    }

    public Query count() {
        return this.compose(TInst.of(Tokens.COUNT));
    }

    public Query drop(final Object key) {
        return this.compose(TInst.of(Tokens.DROP, arg(key)));
    }

    public Query and(final Object... objects) {
        return this.compose(TInst.of(Tokens.AND, args(objects)));
    }

    public Query dedup(final Object... objects) {
        return this.compose(TInst.of(Tokens.DEDUP, args(objects)));
    }

    public Query eq(final Object obj) {
        return this.compose(TInst.of(Tokens.EQ, arg(obj)));
    }

    public Query get(final Object key) {
        return this.compose(TInst.of(Tokens.GET, arg(key)));
    }

    public Query groupCount(final Object key) {
        return this.compose(TInst.of(Tokens.GROUPCOUNT, arg(key)));
    }

    public Query gt(final Object obj) {
        return this.compose(TInst.of(Tokens.GT, arg(obj)));
    }

    public Query is(final Object bool) {
        return this.compose(TInst.of(Tokens.IS, arg(bool)));
    }

    public Query map(final Object obj) {
        return this.compose(TInst.of(Tokens.MAP, arg(obj)));
    }

    public Query minus(final Object obj) {
        return this.compose(TInst.of(Tokens.MINUS, arg(obj)));
    }

    public Query mult(final Object obj) {
        return this.compose(TInst.of(Tokens.MULT, arg(obj)));
    }

    public Query one() {
        return this.compose(TInst.of(Tokens.ONE));
    }

    public Query plus(final Object obj) {
        return this.compose(TInst.of(Tokens.PLUS, arg(obj)));
    }

    public Query put(final Object key, final Object value) {
        return this.compose(TInst.of(Tokens.PUT, arg(key), arg(value)));
    }

    public Query a(final Object obj) {
        return new Query(TInst.of(Tokens.A, arg(obj)));
    }

    public Query start(final Object... objects) {
        return new Query(TInst.of(Tokens.START, args(objects)));
    }


    public Query sum() {
        return this.compose(TInst.of(Tokens.SUM));
    }


    public Query type() {
        return this.compose(TInst.of(Tokens.TYPE));
    }

    public Query zero() {
        return this.compose(TInst.of(Tokens.ZERO));
    }

    public Query as(final String key) {
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
        return other instanceof Query && ((Query) other).bytecode.equals(this.bytecode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), this.bytecode);
    }

    //////////////////

    private Query compose(final Inst inst) {
        this.bytecode = this.bytecode.mult(inst);
        return this;
    }

    private static Obj[] args(final Object[] objects) {
        final Obj[] objs = new Obj[objects.length];
        for (int i = 0; i < objects.length; i++) {
            objs[i] = Query.arg(objects[i]);
        }
        return objs;
    }

    private static Obj arg(final Object object) {
        return object instanceof Query ? ((Query) object).bytecode : TObj.from(object);
    }
}
