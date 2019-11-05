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

package org.mmadt.machine.object.impl.composite.inst.map;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.processor.compiler.Argument;

import java.util.function.Supplier;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class MapInst<S extends Obj, E extends Obj> extends TInst implements MapInstruction<S, E> {

    private MapInst(final E argument) {
        super(PList.of(Tokens.MAP, argument));
    }

    public E apply(final S s) {
        return s.map(Argument.<S, E>create(this.args().get(0)).mapArg(s));
    }

    public static <S extends Obj, E extends Obj> S create(final Supplier<S> result, final S source, final E argument) {
        return ObjectHelper.allInstances(source, argument) ?
                result.get() :
                source.access(source.access().mult(new MapInst<>(argument)));
    }
}