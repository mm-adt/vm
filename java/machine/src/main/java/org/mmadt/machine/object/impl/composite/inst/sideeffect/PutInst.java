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
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.inst.SideEffectInstruction;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.algebra.WithProduct;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class PutInst<K extends Obj, V extends Obj> extends TInst implements SideEffectInstruction<WithProduct<K, V>> {

    private PutInst(final Object key, final Object value) {
        super(PList.of(Tokens.PUT, key, value));
    }

    @Override
    public void accept(final WithProduct<K, V> obj) {
        obj.put(this.<Obj, K>argument(0).mapArg(obj), this.<Obj, V>argument(1).mapArg(obj));
    }

    public static <K extends Obj, V extends Obj> PutInst<K, V> create(final Object key, final Object value) {
        return new PutInst<>(key, value);
    }


}