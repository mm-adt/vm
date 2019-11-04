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

package org.mmadt.machine.object.impl.composite.inst.filter;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.composite.inst.filter.FilterInstruction;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.processor.compiler.Argument;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class IsInst<S extends Obj> extends TInst implements FilterInstruction<S> {

    private IsInst(final S argument) {
        super(PList.of(Tokens.IS, argument));
    }

    public boolean testt(final S s) {
        return Argument.<S, Bool>create(this.args().get(0)).mapArg(s).java();
    }

    public static <S extends Obj> S create(final S source, final Bool argument) {
        return ObjectHelper.allInstances(source, argument) ?
                argument.java() ? source : (S) source.q(Q.Tag.zero) :
                source.q(source.q().peek().zero(), source.q().last()).access(source.access().mult(new IsInst<>(argument)));
    }
}
