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

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.type.algebra.WithEval;
import org.mmadt.processor.compiler.Argument;
import org.mmadt.processor.function.AbstractFunction;
import org.mmadt.processor.function.MapFunction;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class EvalMap<S extends WithEval, E extends Obj> extends AbstractFunction implements MapFunction<S, E> {

    @SafeVarargs
    private EvalMap(final Q quantifier, final String label, final Argument<S, Obj>... arguments) {
        super(quantifier, label, arguments);
    }

    @Override
    public E apply(final S obj) {
        final Obj[] args = new Obj[arguments().length - 1];
        for (int i = 1; i < arguments().length; i++) {
            args[i] = argument(i).mapArg(obj);
        }
        return obj.eval(((Str) argument(0).mapArg(obj)).java(), args);
    }

    public static <S extends WithEval, E extends Obj> EvalMap<S, E> compile(final Inst inst) {
        return new EvalMap<>(inst.q(), inst.label(), Argument.<S, Obj>args(inst.args()));
    }
}