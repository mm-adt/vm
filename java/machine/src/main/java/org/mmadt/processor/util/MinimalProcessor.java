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

package org.mmadt.processor.util;

import org.mmadt.object.impl.TModel;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.composite.Inst;
import org.mmadt.processor.compiler.FunctionTable;
import org.mmadt.processor.function.FilterFunction;
import org.mmadt.processor.function.MapFunction;
import org.mmadt.processor.function.QFunction;
import org.mmadt.util.FunctionUtils;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class MinimalProcessor<S extends Obj, E extends Obj> extends SimpleProcessor<S, E> {

    protected final Inst bytecode;

    public MinimalProcessor(final Inst inst) {
        this.bytecode = inst;
    }

    @Override
    protected void processTraverser(final Iterator<S> starts) {
        final S start = starts.next();
        Obj temp = start;
        for (final Inst inst : this.bytecode.iterable()) {
            final QFunction function = FunctionTable.function(TModel.of("ex"), inst);
            if (function instanceof FilterFunction) {
                temp = FunctionUtils.test((FilterFunction<Obj>) function, temp).orElse(null);
                if (null == temp) break;
            } else if (function instanceof MapFunction) {
                temp = FunctionUtils.map((MapFunction) function, temp);
            } else {
                throw new UnsupportedOperationException("This is not implemented yet: " + function);
            }
        }
        if (null != temp) {
            if (null != this.bytecode.variable())
                temp = temp.as(this.bytecode.variable()); // TODO: bytecode as a whole shouldn't be able to be labeled (only individual instructions)
            this.traverser = (E) temp;
        }

    }

}