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

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class EmptyProcessor<S extends Obj, E extends Obj> implements Processor<S, E>, ProcessorFactory {

    private static final EmptyProcessor INSTANCE = new EmptyProcessor();

    private EmptyProcessor() {
        // static instance
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public Iterator<E> iterator(final Iterator<S> starts) {
        return Collections.emptyIterator();
    }

    @Override
    public void subscribe(final Iterator<S> starts, final Consumer<E> consumer) {

    }

    public static <S extends Obj, E extends Obj> EmptyProcessor<S, E> instance() {
        return INSTANCE;
    }
}