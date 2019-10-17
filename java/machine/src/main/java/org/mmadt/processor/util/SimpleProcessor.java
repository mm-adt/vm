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

import org.mmadt.object.model.Obj;
import org.mmadt.processor.Processor;
import org.mmadt.processor.ProcessorFactory;
import org.mmadt.processor.compiler.IR;
import org.mmadt.util.FastNoSuchElementException;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class SimpleProcessor<S extends Obj, E extends Obj> implements Processor<S, E>, ProcessorFactory {

    protected E traverser = null;

    @Override
    public void stop() {
        this.traverser = null;
    }

    @Override
    public boolean isRunning() {
        return null != this.traverser;
    }

    @Override
    public Iterator<E> iterator(final Iterator<S> starts) {
        this.processTraverser(starts);
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return null != traverser;
            }

            @Override
            public E next() {
                if (null == traverser)
                    throw FastNoSuchElementException.instance();
                else {
                    final E temp = traverser;
                    traverser = null;
                    return temp;
                }
            }
        };
    }

    @Override
    public void subscribe(final Iterator<S> starts, final Consumer<E> consumer) {
        this.processTraverser(starts);
        if (null != this.traverser)
            consumer.accept(this.traverser);
        this.traverser = null;
    }

    @Override
    public <A extends Obj, B extends Obj> Processor<A, B> mint(final IR<A, B> compilation) {
        return (Processor<A, B>) this;
    }

    protected abstract void processTraverser(final Iterator<S> starts);
}