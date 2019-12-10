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

package org.mmadt.machine.object.model.composite;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.DropInst;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.PutInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.Bindings;
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.machine.object.model.ext.algebra.WithProduct;
import org.mmadt.machine.object.model.ext.algebra.WithRing;
import org.mmadt.machine.object.model.util.InstHelper;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.processor.util.FastProcessor;

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

    /*public Obj attach(final Obj domain, final Obj range);

    public Obj attach(Obj domainRange);*/

    public default <V extends Obj> List<V> args() {
        final List<V> args = new ArrayList<>();
        boolean first = true;
        for (final Obj arg : InstHelper.first(this).<Iterable<Obj>>get()) {
            if (first) first = false;
            else
                args.add((V) arg);
        }
        return args;
    }

    public default Str opcode() {
        return (Str) InstHelper.first(this).<PList>get().get(0);
    }

    @Override
    public default Inst put(final Int index, final Obj value) {
        if (this.isInstance() || this.isType()) {
            this.java().add(index.java(), value);
            return this;
        } else
            return this.mapTo(PutInst.create(index, value));
    }

    @Override
    public default Inst drop(final Int index) {
        if (this.isInstance() || this.isType()) {
            this.java().remove((int) index.java());
            return this; // TODO ?? Instructions.compile(this);
        } else
            return this.mapFrom(DropInst.create(index));
    }

    @Override
    public default Obj get(final Int index) {
        final PList list = this.get();
        if (list.size() > index.java())
            return (Obj) list.get(index.java());
        else
            return TObj.none();
    }

    @Override
    public default boolean test(final Obj obj) {
        // when testing instruction against instruction, use list testing inst(x,y)
        if (obj instanceof Inst)
            return WithProduct.super.test(((Inst) obj));
        return FastProcessor.process(obj.mapTo(this)).hasNext();
    }

    @Override
    public default boolean match(final Bindings bindings, final Obj obj) {
        // when matching instruction against instruction, use list matching inst(x,y)
        if (obj instanceof Inst)
            return WithProduct.super.match(bindings, (Inst) obj);

        if (bindings.has(this.label()))
            return bindings.get(this.label()).test(obj);
        bindings.start();
        final Iterator<Obj> itty = FastProcessor.process(obj.mapTo(this));
        if (itty.hasNext()) {
            final Obj object = itty.next();
            if (null != object.label())
                bindings.put(object.label(), object.label(null));
            return true;
        } else {
            bindings.rollback();
            return false;
        }
    }

    public default Iterable<Inst> iterable() {
        return InstHelper.singleInst(this) ? List.of(this) : this.<PList<Inst>>get();
    }


    ///////////////

    public default Inst put(final Object index, final Object value) {
        return this.put(ObjectHelper.create(TInt.of().copy(this), index), ObjectHelper.create(TObj.single().copy(this), value));
    }


    public default Inst drop(final Object index) {
        return this.drop(ObjectHelper.create(TInt.of().copy(this), index));
    }
}
