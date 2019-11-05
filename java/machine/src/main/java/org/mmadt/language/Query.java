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
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.inst.initial.StartInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Stream;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.algebra.WithDiv;
import org.mmadt.machine.object.model.type.algebra.WithMinus;
import org.mmadt.machine.object.model.type.algebra.WithMult;
import org.mmadt.machine.object.model.type.algebra.WithOne;
import org.mmadt.machine.object.model.type.algebra.WithOrder;
import org.mmadt.machine.object.model.type.algebra.WithPlus;
import org.mmadt.machine.object.model.type.algebra.WithProduct;
import org.mmadt.machine.object.model.type.algebra.WithZero;
import org.mmadt.machine.object.model.util.ObjectHelper;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class Query {

    private Inst bytecode;
    private Function<Obj, Obj> function;
    private Obj obj;

    private Query(final Inst inst) {
        this.bytecode = inst;
    }

    private Query(final Obj obj) {
        this.obj = obj;
    }

    Query() {
        this(TInst.of(Tokens.ID));
    }

    public Query and(final Object... objects) {
        return this.append(a -> a.access(a.access().mult(TInst.of(Tokens.AND, args(objects)))));
    }

    public Query branch(final Object... branches) {
        return this.compose(TInst.of(Tokens.BRANCH, args(branches)));
    }

    public Query id() {
        return this.append(Obj::id);
    }

    public Query count() {
        return this.append(Obj::count);
    }

    public Query dedup(final Object... objects) {
        return this.compose(TInst.of(Tokens.DEDUP, args(objects)));
    }

    public Query div(final Object obj) {
        return this.append(a -> ((WithDiv<?>) a).div(prep(obj)));
    }

    public Query drop(final Object key) {
        return this.append(a -> ((WithProduct<?, ?>) a).drop(prep(key)));
    }

    public Query eq(final Object obj) {
        return this.append(a -> a.eq(prep(obj)));
    }

    public Query get(final Object key) {
        return this.append(a -> ((WithProduct<?, ?>) a).get(prep(key)));
    }

    public Query groupCount(final Object key) {
        return this.compose(TInst.of(Tokens.GROUPCOUNT, arg(key)));
    }

    public Query gt(final Object obj) {
        return this.append(a -> ((WithOrder<?>) a).gt(prep(obj)));
        //return this.compose(TInst.of(Tokens.GT, arg(obj)));
    }

    public Query gte(final Object obj) {
        return this.compose(TInst.of(Tokens.GTE, arg(obj)));
    }

    public Query inv() {
        return this.compose(TInst.of(Tokens.INV));
    }

    public Query is(final Object bool) {
        return this.append(a -> a.is(prep(bool)));
        // return this.compose(TInst.of(Tokens.IS, arg(bool)));
    }

    public Query lt(final Object obj) {
        return this.append(a -> ((WithOrder<?>) a).lt(prep(obj)));
    }

    public Query lte(final Object obj) {
        return this.compose(TInst.of(Tokens.LTE, arg(obj)));
    }

    public Query map(final Object obj) {
        return this.append(a -> a.map(prep(obj)));
    }

    public Query minus(final Object obj) {
        return this.append(a -> ((WithMinus<?>) a).minus(prep(obj)));
    }

    public Query mult(final Object obj) {
        return this.append(a -> ((WithMult<?>) a).mult(prep(obj)));
        //return this.compose(TInst.of(Tokens.MULT, arg(obj)));
    }

    public Query neg() {
        return this.append(a -> ((WithMinus) a).neg());
    }

    public Query neq(final Object obj) {
        return this.compose(TInst.of(Tokens.NEQ, arg(obj)));
    }

    public Query one() {
        return this.append(a -> ((WithOne<?>) a).one());
    }

    public Query or(final Object branch) {
        return this.append(a -> a.or(prep(branch)));
    }

    public Query plus(final Object obj) {
        return this.append(a -> ((WithPlus<?>) a).plus(prep(obj)));
        // return this.compose(TInst.of(Tokens.PLUS, arg(obj)));
    }

    public Query put(final Object key, final Object value) {
        return this.append(a -> ((WithProduct<?, ?>) a).put(prep(key), prep(value)));
    }

    public Query a(final Object obj) {
        return this.append(a -> a.a(prep(obj)));
    }

    public Query q() {
        return this.append(a -> a.q());
    }

    public Query q(final Object quantifier) {
        final Inst last = this.bytecode.last(); // TODO: total shit show with streams...need a better model.
        if (this.bytecode.get() instanceof PList)
            this.bytecode = TInst.of(Tokens.ID);
        else
            this.bytecode.<Stream<Inst>>get().drop(last);
        return this.compose(last.q(arg(quantifier)));
    }

    public Query reduce(final Object seed, final Object reduce) {
        return this.compose(TInst.of(Tokens.REDUCE, arg(seed), arg(reduce)));
    }

    public Query start(final Object... objects) {
        return new Query(0 == objects.length ? TObj.none() : StartInst.create(arg(objects[0]).<Obj>set(null), objects));
        // return new Query(TInst.of(Tokens.START, args(objects)));
    }

    public Query sum() {
        return this.append(Obj::sum);
    }

    public Query type() {
        return this.append(a -> ObjectHelper.type(a));
    }

    public Query zero() {
        return this.append(a -> ((WithZero<?>) a).zero());
    }

    public Query as(final String key) {
        this.bytecode = this.bytecode.label(key);
        return this;
    }


    //////////////////

    public Inst bytecode() {
        return this.bytecode;
    }

    public <A extends Obj> A obj() {
        return (A) this.obj;
    }

    @Override
    public String toString() {
        return null != this.obj ? this.obj.toString() : null != this.bytecode ? this.bytecode.toString() : "nothing";
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

    private Obj[] args(final Object[] objects) {
        final Obj[] objs = new Obj[objects.length];
        for (int i = 0; i < objects.length; i++) {
            objs[i] = prep(objects[i]);
        }
        return objs;
    }

    private static Obj arg(final Object object) {
        return object instanceof Query ? ((Query) object).bytecode : ObjectHelper.from(object);
    }

    private <A extends Obj> A prep(final Object object) {
        A a = null;
        if (object instanceof Query) {
            if (null == ((Query) object).obj) {
                if (null == this.obj)
                    throw new IllegalStateException("No available root: " + this);
                else if (null != ((Query) object).function)
                    a = (A) ((Query) object).function.apply(this.obj.q(Q.Tag.one).access((Inst) null));
            } else
                a = (A) ((Query) object).obj;
        } else
            a = (A) ObjectHelper.from(object);
        if (null == this.obj && null != a)
            this.obj = a.set(null);
        return a;
    }

    private Query append(final Function<Obj, Obj> function) {
        this.function = null == this.function ? function : this.function.andThen(function);
        if (null != this.obj) {
            this.obj = this.function.apply(this.obj);
            this.function = null;
        }
        return this;
    }


}
