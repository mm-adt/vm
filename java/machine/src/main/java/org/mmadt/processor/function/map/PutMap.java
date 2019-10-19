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

package org.mmadt.processor.function.map;

import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.composite.Inst;
import org.mmadt.object.model.composite.Quantifier;
import org.mmadt.object.model.composite.Struct;
import org.mmadt.object.impl.composite.TQuantifier;
import org.mmadt.processor.compiler.Argument;
import org.mmadt.processor.function.AbstractFunction;
import org.mmadt.processor.function.MapFunction;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class PutMap<K extends Obj, V extends Obj> extends AbstractFunction implements MapFunction<Struct<K, V>, Struct<K, V>> {


    private PutMap(final Quantifier quantifier, final String label, final Argument<Struct<K, V>, K> key, final Argument<Struct<K, V>, V> value) {
        super(quantifier, label, key, value);
    }

    @Override
    public Struct<K, V> apply(final Struct<K, V> obj) {
        return obj.put(this.<Struct<K, V>, K>argument(0).mapArg(obj), this.<Struct<K, V>, V>argument(1).mapArg(obj));
    }

    public static <K extends Obj, V extends Obj> PutMap<K, V> compile(final Inst inst) {
        return new PutMap<>(inst.q(), inst.variable(), Argument.create(inst.get(TInt.oneInt())), Argument.create(inst.get(TInt.twoInt())));
    }
}