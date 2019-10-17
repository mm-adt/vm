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

package org.mmadt.processor.function.reduce;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.impl.composite.TInst;
import org.mmadt.object.impl.composite.TRec;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.atomic.Int;
import org.mmadt.object.model.composite.Inst;
import org.mmadt.object.model.composite.Rec;
import org.mmadt.object.model.type.PMap;
import org.mmadt.object.model.type.Quantifier;
import org.mmadt.processor.compiler.Argument;
import org.mmadt.processor.function.AbstractFunction;
import org.mmadt.processor.function.ReduceFunction;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class GroupCountReduce<S extends Obj, E extends Obj> extends AbstractFunction implements ReduceFunction<S, Rec<E, Int>> {

    private GroupCountReduce(final Quantifier quantifier, final String label, final Argument<S, E> argument) {
        super(quantifier, label, argument);

    }

    @Override
    public Rec<E, Int> apply(final S obj, final Rec<E, Int> seed) {
        final E object = this.<S, E>argument(0).mapArg(obj);
        seed.put(object, obj.<Int>q().low().plus(seed.<PMap<E, Int>>get().getOrDefault(object, TInt.zeroInt())));
        return seed;
    }

    @Override
    public Rec<E, Int> merge(final Rec<E, Int> valueA, final Rec<E, Int> valueB) {
        final Rec<E, Int> tuple = TRec.of();
        valueA.<PMap<E, Int>>get().forEach(tuple::put);
        valueB.<PMap<E, Int>>get().forEach(tuple::put);
        return tuple;
    }

    @Override
    public Rec<E, Int> getInitialValue() {
        return TRec.of();
    }

    public static <S extends Obj, E extends Obj> GroupCountReduce<S, E> compile(final Inst inst) {
        return new GroupCountReduce<>(inst.q(), inst.variable(), Argument.create(inst.args().isEmpty() ? TInst.of(Tokens.ID) : inst.args().get(0)));
    }

}