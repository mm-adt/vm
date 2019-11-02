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

package org.mmadt.processor.function.filter;

import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.processor.compiler.Argument;
import org.mmadt.processor.function.AbstractFunction;
import org.mmadt.processor.function.FilterFunction;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class IsFilter<S extends Obj> extends AbstractFunction implements FilterFunction<S> {

    private IsFilter(final Q quantifier, final String label, final Argument<S, Bool> argument) {
        super(quantifier, label, argument);
    }

    @Override
    public boolean test(final S obj) {
        return this.<S, Bool>argument(0).mapArg(obj).java();
    }

    public static <S extends Obj> IsFilter<S> compile(final Inst inst) {
        return new IsFilter<>(inst.q(), inst.label(), Argument.create(inst.get(TInt.oneInt())));
    }
}