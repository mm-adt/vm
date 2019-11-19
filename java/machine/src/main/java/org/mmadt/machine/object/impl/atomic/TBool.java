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
import org.mmadt.machine.object.impl.composite.inst.map.EqInst;
import org.mmadt.machine.object.impl.composite.inst.map.MinusInst;
import org.mmadt.machine.object.impl.composite.inst.map.MultInst;
import org.mmadt.machine.object.impl.composite.inst.map.OneInst;
import org.mmadt.machine.object.impl.composite.inst.map.PlusInst;
import org.mmadt.machine.object.impl.composite.inst.map.ZeroInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.util.ObjectHelper;


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TBool extends TObj implements Bool {

    private TBool(final Object value) {
        super(value);
    }

    public static Bool all() {
        return new TBool(null).q(0, Integer.MAX_VALUE);
    }

    public static Bool none() {
        return new TBool(null).q(0);
    }

    public static Bool some() {
        return new TBool(null);
    }

    public static Bool some(final int count) {
        return TBool.some(count, count);
    }

    public static Bool some(final int low, final int high) {
        return new TBool(null).q(low, high);
    }

    public static Bool of(final Object... objects) {
        return ObjectHelper.make(TBool::new, objects);
    }

    public static Bool from(final Obj obj) {
        return TBool.some().q(obj.q()).accessFrom(obj.accessFrom()).accessTo(obj.accessTo());
    }

    @Override
    public Obj or(final Obj obj) {
        return this.constant() && this.q().constant() ? new TBool(this.java() || obj.<Boolean>get()) : super.or(obj);
    }

    @Override
    public Bool one() {
        return this.q().constant() ? this.set(Boolean.TRUE) : this.mapFrom(OneInst.create());
    }

    @Override
    public Bool zero() {
        return this.q().constant() ? this.set(Boolean.FALSE) : this.mapFrom(ZeroInst.create());
    }

    @Override
    public Bool neg() {
        return this;
    }

    @Override
    public Bool mult(final Bool bool) {
        return (this.isInstance() && bool.isInstance()) ?
                this.set(this.java() && bool.java()) :
                this.mapFrom(MultInst.create(bool));
    }

    @Override
    public Bool minus(final Bool bool) {
        return (this.isInstance() && bool.isInstance()) ?
                this.set(exclusiveOr(this.java(), bool.java())) :
                this.mapFrom(MinusInst.create(bool));
    }

    @Override
    public Bool plus(final Bool bool) {
        return (this.isInstance() && bool.isInstance()) ?
                this.set(exclusiveOr(this.java(), bool.java())) :
                this.mapFrom(PlusInst.create(bool));
    }

    @Override
    public Bool eq(final Obj obj) {
        return this.isInstance() ?
                this.set(obj instanceof Bool && this.java().equals(((Bool) obj).java())) :
                this.mapFrom(EqInst.create(obj));
    }

    private static boolean exclusiveOr(final boolean a, final boolean b) {
        return (a && !b) || (!a && b);
    }
}
