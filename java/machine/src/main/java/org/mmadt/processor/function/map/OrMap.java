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

import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.processor.compiler.Argument;
import org.mmadt.processor.function.AbstractFunction;
import org.mmadt.processor.function.MapFunction;

import java.util.stream.Stream;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class OrMap<S extends Obj> extends AbstractFunction implements MapFunction<S, Bool> {

    @SafeVarargs
    private OrMap(final Q quantifier, final String label, final Argument<S, Bool>... arguments) {
        super(quantifier, label, arguments);
    }

    @Override
    public Bool apply(final S obj) {
        return Stream.of(this.<S, Bool>arguments()).map(a -> a.mapArg(obj)).reduce((a, b) -> a.or(b)).orElse(TBool.of(true));
    }


    public static <S extends Obj> OrMap<S> compile(final Inst inst) {
        return new OrMap<>(inst.q(), inst.label(), Argument.<S, Bool>args(inst.args()));
    }
}