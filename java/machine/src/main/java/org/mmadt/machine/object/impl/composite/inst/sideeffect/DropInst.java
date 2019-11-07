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

package org.mmadt.machine.object.impl.composite.inst.sideeffect;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.inst.util.InstructionHelper;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.inst.SideEffectInstruction;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.algebra.WithProduct;

import java.util.function.Supplier;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class DropInst<K extends Obj, V extends Obj> extends TInst implements SideEffectInstruction<WithProduct<K, V>> {

    private DropInst(final Object key) {
        super(PList.of(Tokens.DROP, key));
    }

    @Override
    public void accept(final WithProduct<K, V> obj) {
        obj.drop(this.<Obj, K>argument(0).mapArg(obj));
    }

    public static <K extends Obj, V extends Obj> WithProduct<K, V> create(final Supplier<WithProduct<K, V>> compute, final WithProduct<K, V> obj, final K key) {
        return InstructionHelper.<WithProduct<K, V>>rewrite(obj, new DropInst<>(key)).orElse(
                obj.isInstance() || obj.isType() ? // this is necessary because records and lists store their patterns in maps and lists respectively
                        compute.get() :
                        obj.append(new DropInst<>(key)));
    }

    public static <K extends Obj, V extends Obj> DropInst<K, V> create(final Object arg) {
        return new DropInst<>(arg);
    }


}