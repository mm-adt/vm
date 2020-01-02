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

package org.mmadt.machine.object.impl.composite.inst.map;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.machine.object.model.ext.algebra.WithProduct;

import java.util.List;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class GetInst<K extends Obj, V extends Obj> extends TInst<WithProduct<K, V>, V> implements MapInstruction<WithProduct<K, V>, V> {

    private GetInst(final Object key) {
        super(PList.of(Tokens.GET, key));
    }

    @Override
    public V apply(final WithProduct<K, V> obj) {
        return obj.get(this.<K>argument(0).mapArg(obj));
    }

    public static <K extends Obj, V extends Obj> GetInst<K, V> create(final Object arg) {
        return new GetInst<>(arg);
    }

    public static <K extends Obj, V extends Obj> V compute(final WithProduct<K, V> product, final K key) {
        if (!product.isReference() && !key.isReference() &&
                null != product.get() && null != key.get()) {
            return GetInst.composite(product, key);
        } else {
            return (null == product.get()) ?
                    GetInst.<K, V>create(key).attach(product) :
                    GetInst.<K, V>create(key).attach(product, GetInst.composite(product, key));
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static <K extends Obj, V extends Obj> V composite(final WithProduct<K, V> product, final K key) {
        return product.isLst() ?
                lst((Lst<V>) product, (Int) key) :
                rec((Rec<K, V>) product, key);
    }

    private static <V extends Obj> V lst(final Lst<V> lst, final Int key) {
        return (lst.<List<V>>get().size() <= key.<Integer>get() ?
                (V) TObj.none() :
                lst.<List<V>>get().get(key.get())).copy(lst);
    }

    private static <K extends Obj, V extends Obj> V rec(final Rec<K, V> rec, final K key) {
        for (final Map.Entry<K, V> entry : rec.<Map<K, V>>get().entrySet()) {
            if (key.test(entry.getKey()))
                return entry.getValue().copy(rec);
        }
        return (V) TObj.none();
    }

}