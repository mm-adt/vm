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

package org.mmadt.processor;

import org.mmadt.object.model.Obj;
import org.mmadt.util.IteratorUtils;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Processor<S extends Obj, E extends Obj> {


    public boolean isRunning();

    /**
     * When a processor is stopped, subscriptions and iteration are halted.
     */
    public void stop();

    /**
     * Start the processor and return a pull-based iterator.
     * If pull-based iteration is used, then push-based subscription can not be used while the processor is running.
     *
     * @return an iterator of traverser results
     */
    public Iterator<E> iterator(final Iterator<S> starts);

    public default Iterator<E> iterator(final S start) {
        return this.iterator(IteratorUtils.of(start));
    }

    /**
     * Start the processor and process the resultant traversers using the push-based consumer.
     * If push-based subscription is used, then pull-based iteration can not be used while the processor is running.
     *
     * @param consumer a consumer of traversers results
     */
    public void subscribe(final Iterator<S> starts, final Consumer<E> consumer);

    public class Exceptions {

        public static IllegalStateException processorIsCurrentlyRunning(final Processor processor) {
            return new IllegalStateException("The processor can not be started because it is currently running: " + processor);
        }
    }

}