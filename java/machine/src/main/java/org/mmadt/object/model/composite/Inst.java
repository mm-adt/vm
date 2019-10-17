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

package org.mmadt.object.model.composite;

import org.mmadt.object.model.Obj;
import org.mmadt.object.model.atomic.Int;
import org.mmadt.object.model.atomic.Str;
import org.mmadt.object.model.type.Bindings;
import org.mmadt.object.model.type.feature.WithRing;
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
public interface Inst extends Struct<Int, Obj>, WithRing<Inst> {

    public Str opcode();

    public Obj domain();

    public Obj range();

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

    @Override
    public default boolean test(final Obj object) {
        // when testing instruction against instruction, use list testing inst(x,y)
        if (object instanceof Inst)
            return Struct.super.test(((Inst) object));
        return new MinimalProcessor<>(this).iterator(object).hasNext();
    }

    @Override
    public default boolean match(final Bindings bindings, final Obj object) {
        // when matching instruction against instruction, use list matching inst(x,y)
        if (object instanceof Inst)
            return Struct.super.match(bindings, (Inst) object);

        if (bindings.has(this.variable()))
            return bindings.get(this.variable()).test(object);
        bindings.start();
        final Iterator<Obj> itty = new MinimalProcessor<>(this).iterator(object);
        if (itty.hasNext()) {
            final Obj obj = itty.next();
            if (null != obj.variable())
                bindings.put(obj.variable(), obj.as(null));
            return true;
        } else {
            bindings.rollback();
            return false;
        }
    }

    @Override
    public default Inst bind(final Bindings bindings) {
        return (Inst) Struct.super.bind(bindings);
    }

    @Override
    public default Iterable<? extends Inst> iterable() {
        return (Iterable<? extends Inst>) Struct.super.iterable();
    }

}
