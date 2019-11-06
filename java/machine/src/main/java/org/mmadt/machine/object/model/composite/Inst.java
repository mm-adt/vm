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

package org.mmadt.machine.object.model.composite;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Stream;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.type.Bindings;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.algebra.WithProduct;
import org.mmadt.machine.object.model.type.algebra.WithRing;
import org.mmadt.machine.object.model.util.OperatorHelper;
import org.mmadt.processor.util.MinimalProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Java representation of the {@code inst} object in mm-ADT.
 * An {@code inst} is a ring with unity.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Inst extends WithRing<Inst>, WithProduct<Int, Obj> {

    public default List<Obj> java() {
        return this.get();
    }

    public Obj domain();

    public Obj range();

    @Override
    public Inst or(final Obj obj);

    public default Inst domain(final Obj domain) {
        return this.domainAndRange(domain, this.range());
    }

    public default Inst range(final Obj range) {
        return this.domainAndRange(this.domain(), range);
    }

    public default Inst domainAndRange(final Obj domain, final Obj range) {
        return this.domain(domain).range(range);
    }

    public default <V extends Obj> List<V> args() {
        final List<V> args = new ArrayList<>();
        boolean first = true;
        for (final Obj arg : this.<Iterable<Obj>>get()) {
            if (first) first = false;
            else
                args.add((V) arg);
        }
        return args;
    }

    public default Str opcode() {
        return (Str) this.java().get(0);
    }

    @Override
    public default Inst put(final Int index, final Obj value) {
        return OperatorHelper.unary(Tokens.PUT, () -> {
            this.java().add(index.java(), value);
            return this;
        }, this);
    }

    @Override
    public default Inst drop(final Int key) {
        return OperatorHelper.unary(Tokens.DROP, () -> {
            this.java().remove(key.java());
            return this;
        }, this);
    }

    @Override
    public default Obj get(final Int index) {
        final Object object = this.peek().get();
        return (object instanceof PList && (((PList<Obj>) object).size() > index.<Integer>get())) ? ((PList<Obj>) object).get(index.get()) : TObj.none();
    }

    @Override
    public default boolean test(final Obj obj) {
        // when testing instruction against instruction, use list testing inst(x,y)
        if (obj instanceof Inst)
            return WithProduct.super.test(((Inst) obj));
        return new MinimalProcessor<>(TInst.some().access(this)).iterator(obj).hasNext();
    }

    @Override
    public default boolean match(final Bindings bindings, final Obj object) {
        // when matching instruction against instruction, use list matching inst(x,y)
        if (object instanceof Inst)
            return WithProduct.super.match(bindings, (Inst) object);

        if (bindings.has(this.label()))
            return bindings.get(this.label()).test(object);
        bindings.start();
        final Iterator<Obj> itty = new MinimalProcessor<>(TInst.some().access(this)).iterator(object);
        if (itty.hasNext()) {
            final Obj obj = itty.next();
            if (null != obj.label())
                bindings.put(obj.label(), obj.label(null));
            return true;
        } else {
            bindings.rollback();
            return false;
        }
    }

    @Override
    public default Inst bind(final Bindings bindings) {
        return (Inst) WithProduct.super.bind(bindings);
    }

    @Override
    public default Iterable<Inst> iterable() {
        return this.get() instanceof Stream ? this.<Stream<Inst>>get() : null == this.get() ? List.of() : List.of(this);
    }
}
