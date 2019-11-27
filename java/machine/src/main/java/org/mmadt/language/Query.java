/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.inst.barrier.DedupInst;
import org.mmadt.machine.object.impl.composite.inst.branch.BranchInst;
import org.mmadt.machine.object.impl.composite.inst.branch.ChooseInst;
import org.mmadt.machine.object.impl.composite.inst.filter.IdInst;
import org.mmadt.machine.object.impl.composite.inst.filter.IsInst;
import org.mmadt.machine.object.impl.composite.inst.initial.StartInst;
import org.mmadt.machine.object.impl.composite.inst.map.AInst;
import org.mmadt.machine.object.impl.composite.inst.map.AndInst;
import org.mmadt.machine.object.impl.composite.inst.map.DivInst;
import org.mmadt.machine.object.impl.composite.inst.map.EqInst;
import org.mmadt.machine.object.impl.composite.inst.map.GetInst;
import org.mmadt.machine.object.impl.composite.inst.map.GtInst;
import org.mmadt.machine.object.impl.composite.inst.map.GteInst;
import org.mmadt.machine.object.impl.composite.inst.map.InvInst;
import org.mmadt.machine.object.impl.composite.inst.map.LtInst;
import org.mmadt.machine.object.impl.composite.inst.map.LteInst;
import org.mmadt.machine.object.impl.composite.inst.map.MapInst;
import org.mmadt.machine.object.impl.composite.inst.map.MinusInst;
import org.mmadt.machine.object.impl.composite.inst.map.MultInst;
import org.mmadt.machine.object.impl.composite.inst.map.NegInst;
import org.mmadt.machine.object.impl.composite.inst.map.NeqInst;
import org.mmadt.machine.object.impl.composite.inst.map.OneInst;
import org.mmadt.machine.object.impl.composite.inst.map.OrInst;
import org.mmadt.machine.object.impl.composite.inst.map.PlusInst;
import org.mmadt.machine.object.impl.composite.inst.map.QInst;
import org.mmadt.machine.object.impl.composite.inst.map.StateInst;
import org.mmadt.machine.object.impl.composite.inst.map.ZeroInst;
import org.mmadt.machine.object.impl.composite.inst.reduce.CountInst;
import org.mmadt.machine.object.impl.composite.inst.reduce.GroupCountInst;
import org.mmadt.machine.object.impl.composite.inst.reduce.SumInst;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.DropInst;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.PutInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Stream;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.util.ObjectHelper;

import java.util.Objects;

import static org.mmadt.machine.object.model.composite.Q.Tag.one;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class Query {

    private Inst bytecode;

    Query(final Inst inst) {
        this.bytecode = inst;
    }

    public Query and(final Object... objects) {
        return this.compose(AndInst.create(objects));
    }

    public Query branch(final Object... branches) {
        return this.compose(BranchInst.create(args(branches)));
    }

    public Query choose(final Object... branches) {
        return this.compose(ChooseInst.create(branches));
    }

    public Query state(final Object key) {
        return this.compose(StateInst.create(key));
    }

    public Query id() {
        return this.compose(IdInst.create());
    }

    public Query count() {
        return this.compose(CountInst.create());
    }

    public Query dedup(final Object... objects) {
        return this.compose(DedupInst.create(objects));
    }

    public Query div(final Object obj) {
        return this.compose(DivInst.create(obj));
    }

    public Query drop(final Object key) {
        return this.compose(DropInst.create(key));
    }

    public Query eq(final Object obj) {
        return this.compose(EqInst.create(obj));
    }

    public Query get(final Object key) {
        return this.compose(GetInst.create(key));
    }

    public Query groupCount(final Object key) {
        return this.compose(GroupCountInst.create(key));
    }

    public Query gt(final Object obj) {
        return this.compose(GtInst.create(obj));
    }

    public Query gte(final Object obj) {
        return this.compose(GteInst.create(obj));
    }

    public Query inv() {
        return this.compose(InvInst.create());
    }

    public Query is(final Object bool) {
        return this.compose(IsInst.create(bool));
    }

    public Query lt(final Object obj) {
        return this.compose(LtInst.create(obj));
    }

    public Query lte(final Object obj) {
        return this.compose(LteInst.create(obj));
    }

    public Query map(final Object obj) {
        return this.compose(MapInst.create(obj));
    }

    public Query minus(final Object obj) {
        return this.compose(MinusInst.create(obj));
    }

    public Query mult(final Object obj) {
        return this.compose(MultInst.create(obj));
    }

    public Query neg() {
        return this.compose(NegInst.create());
    }

    public Query neq(final Object obj) {
        return this.compose(NeqInst.create(obj));
    }

    public Query one() {
        return this.compose(OneInst.create());
    }

    public Query or(final Object... branches) {
        return this.compose(OrInst.create(branches));
    }

    public Query plus(final Object obj) {
        return this.compose(PlusInst.create(obj));
    }

    public Query put(final Object key, final Object value) {
        return this.compose(PutInst.create(key, value));
    }

    public Query a(final Object obj) {
        return this.compose(AInst.create(obj));
    }

    public Query q() {
        return this.compose(QInst.create());
    }

    public Query q(final Object quantifier) {
        final Inst last = this.bytecode.last(); // TODO: total shit show with streams...need a better model.
        if (this.bytecode.get() instanceof PList)
            this.bytecode = TInst.of(Tokens.ID);
        else
            this.bytecode.<Stream<Inst>>get().drop(last);
        return this.compose(last.q(ObjectHelper.from(quantifier)));
    }

    public Query reduce(final Object seed, final Object reduce) {
        return this.compose(TInst.of(Tokens.REDUCE, ObjectHelper.from(seed), ObjectHelper.from(reduce)));
    }

    public Query start(final Object... objects) {
        return new Query(StartInst.create(objects));
    }

    public Query sum() {
        return this.compose(SumInst.create());
    }

    public Query type() {
        return this;// this.append(a -> ObjectHelper.type(a));
    }

    public Query zero() {
        return this.compose(ZeroInst.create());
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
        return (A) this.bytecode.domain().access(this.bytecode);
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

    private Obj arg(final Obj domain, final Object object) {
        final Obj obj = ObjectHelper.from(object);
        return object instanceof Query ? domain.clone().q(one).access(((Query) object).bytecode()).access() : obj;
    }

    private Obj[] args(final Object[] objects) {
        final Obj[] objs = new Obj[objects.length];
        for (int i = 0; i < objects.length; i++) {
            objs[i] = ObjectHelper.from(objects[i]);
        }
        return objs;
    }
}
