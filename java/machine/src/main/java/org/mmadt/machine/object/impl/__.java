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

package org.mmadt.machine.object.impl;

import org.mmadt.machine.object.impl.composite.inst.branch.BranchInst;
import org.mmadt.machine.object.impl.composite.inst.branch.ChooseInst;
import org.mmadt.machine.object.impl.composite.inst.filter.IdInst;
import org.mmadt.machine.object.impl.composite.inst.filter.IsInst;
import org.mmadt.machine.object.impl.composite.inst.map.AInst;
import org.mmadt.machine.object.impl.composite.inst.map.AndInst;
import org.mmadt.machine.object.impl.composite.inst.map.AsInst;
import org.mmadt.machine.object.impl.composite.inst.map.EqInst;
import org.mmadt.machine.object.impl.composite.inst.map.GetInst;
import org.mmadt.machine.object.impl.composite.inst.map.GtInst;
import org.mmadt.machine.object.impl.composite.inst.map.GteInst;
import org.mmadt.machine.object.impl.composite.inst.map.LtInst;
import org.mmadt.machine.object.impl.composite.inst.map.LteInst;
import org.mmadt.machine.object.impl.composite.inst.map.MapInst;
import org.mmadt.machine.object.impl.composite.inst.map.MinusInst;
import org.mmadt.machine.object.impl.composite.inst.map.ModelInst;
import org.mmadt.machine.object.impl.composite.inst.map.MultInst;
import org.mmadt.machine.object.impl.composite.inst.map.NegInst;
import org.mmadt.machine.object.impl.composite.inst.map.NeqInst;
import org.mmadt.machine.object.impl.composite.inst.map.OneInst;
import org.mmadt.machine.object.impl.composite.inst.map.OrInst;
import org.mmadt.machine.object.impl.composite.inst.map.PlusInst;
import org.mmadt.machine.object.impl.composite.inst.map.ZeroInst;
import org.mmadt.machine.object.impl.composite.inst.reduce.CountInst;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.DropInst;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.PutInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class __ {

    public static Inst a(final Object object) {
        return AInst.create(object);
    }

    public static Inst as(final Object object) {
        return AsInst.create(object);
    }

    public static Inst branch(final Object... branches) {
        return BranchInst.create(branches);
    }

    public static Inst choose(final Object... choices) {
        return ChooseInst.create(choices);
    }

    public static Inst count() {
        return CountInst.create();
    }

    public static Inst drop(final Object index) {
        return DropInst.create(index);
    }

    public static Inst get(final Object object) {
        return GetInst.create(object);
    }

    public static Inst get(final Object object, final Obj value) {
        return GetInst.create(object, value);
    }


    public static Inst gt(final Object object) {
        return GtInst.create(object);
    }

    public static Inst gte(final Object object) {
        return GteInst.create(object);
    }

    public static Inst eq(final Object object) {
        return EqInst.create(object);
    }

    public static Inst lte(final Object object) {
        return LteInst.create(object);
    }

    public static Inst lt(final Object object) {
        return LtInst.create(object);
    }

    public static Inst plus(final Object object) {
        return PlusInst.create(object);
    }

    public static Inst put(final Object index, final Object value) {
        return PutInst.create(index, value);
    }

    public static Inst map(final Object object) {
        return MapInst.create(object);
    }

    public static Inst model(final Object object) {
        return ModelInst.create(object);
    }

    public static Inst minus(final Object object) {
        return MinusInst.create(object);
    }

    public static Inst mult(final Object object) {
        return MultInst.create(object);
    }

    public static Inst neg() {
        return NegInst.create();
    }

    public static Inst neq(final Object object) {
        return NeqInst.create(object);
    }

    public static Inst one() {
        return OneInst.create();
    }

    public static Inst zero() {
        return ZeroInst.create();
    }

    public static Inst id() {
        return IdInst.create();
    }

    public static Inst is(final Object object) {
        return IsInst.create(object);
    }

    public static Inst and(final Object... args) {
        return AndInst.create(args);
    }

    public static Inst or(final Object... args) {
        return OrInst.create(args);
    }
}
