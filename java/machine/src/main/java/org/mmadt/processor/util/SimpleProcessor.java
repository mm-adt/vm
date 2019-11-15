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

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.processor.Processor;
import org.mmadt.processor.ProcessorFactory;
import org.mmadt.util.FastNoSuchElementException;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class SimpleProcessor<S extends Obj> implements Processor<S>, ProcessorFactory {

    protected S obj = null;

    @Override
    public void stop() {
        this.obj = null;
    }

    @Override
    public boolean alive() {
        return null != this.obj;
    }

    @Override
    public Iterator<S> iterator(final S obj) {
        this.processObj(obj);
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return null != SimpleProcessor.this.obj;
            }

            @Override
            public S next() {
                if (null == SimpleProcessor.this.obj)
                    throw FastNoSuchElementException.instance();
                else {
                    final S temp = SimpleProcessor.this.obj;
                    SimpleProcessor.this.obj = null;
                    return temp;
                }
            }
        };
    }

    @Override
    public void subscribe(final S obj, final Consumer<S> consumer) {
        this.processObj(obj);
        if (null != this.obj)
            consumer.accept(this.obj);
        this.obj = null;
    }

    @Override
    public <A extends Obj> Processor<A> mint(final Inst inst) {
        return (Processor<A>) this;
    }

    protected abstract void processObj(final S starts);
}