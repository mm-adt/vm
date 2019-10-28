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

import org.mmadt.machine.object.impl.TModel;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.processor.compiler.FunctionTable;
import org.mmadt.processor.function.FilterFunction;
import org.mmadt.processor.function.InitialFunction;
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
        Obj obj = starts.next();
        for (final Inst inst : this.bytecode.iterable()) {
            final QFunction function = FunctionTable.function(TModel.of("ex"), inst);
            if (function instanceof FilterFunction) {
                obj = FunctionUtils.test((FilterFunction<Obj>) function, obj).orElse(null);
                if (null == obj) break;
            } else if (function instanceof MapFunction) {
                obj = FunctionUtils.map((MapFunction) function, obj);
            } else if (function instanceof InitialFunction) {
                obj = ((InitialFunction<S>) function).get().next(); // TODO: along with flatmap, create intermediate Iterator
            } else {
                throw new UnsupportedOperationException("This is not implemented yet: " + function);
            }
        }
        if (null != obj) {
            if (null != this.bytecode.label())
                obj = obj.label(this.bytecode.label()); // TODO: bytecode as a whole shouldn't be able to be labeled (only individual instructions)
            this.traverser = (E) obj;
        }

    }

}