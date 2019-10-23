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

package org.mmadt.processor.function.initial;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.TStream;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.processor.compiler.Argument;
import org.mmadt.processor.function.AbstractFunction;
import org.mmadt.processor.function.InitialFunction;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class StartInitial<S extends Obj> extends AbstractFunction implements InitialFunction<S> {

    private StartInitial(final Q quantifier, final String label, final Argument<Obj, S> argument) {
        super(quantifier, label, argument);
    }

    @Override
    public Iterator<S> get() {
        return this.<Obj, S>argument(0).flatMapArg(TObj.none());
    }

    public static <S extends Obj> StartInitial<S> compile(final Inst inst) {
        return new StartInitial<>(inst.q(), inst.label(), Argument.create(TObj.all().q(inst.args().size()).set(TStream.of(inst.args()))));
    }
}
