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

package org.mmadt.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class IteratorUtils {

    private IteratorUtils() {
    }

    public static Iterator asIterator(final Object o) {
        final Iterator itty;
        if (o instanceof Iterable)
            itty = ((Iterable) o).iterator();
        else if (o instanceof Iterator)
            itty = (Iterator) o;
        else if (o instanceof Object[])
            itty = new ArrayIterator<>((Object[]) o);
        else if (o instanceof Stream)
            itty = ((Stream) o).iterator();
        else if (o instanceof Map)
            itty = ((Map) o).entrySet().iterator();
        else if (o instanceof Throwable)
            itty = of(((Throwable) o).getMessage());
        else
            itty = of(o);
        return itty;
    }

    public static final <S> Iterator<S> of(final S a) {
        return new SingleIterator<>(a);
    }


    public static final <S extends Collection<T>, T> S fill(final Iterator<T> iterator, final S collection) {
        while (iterator.hasNext()) {
            collection.add(iterator.next());
        }
        return collection;
    }

    public static <S> Iterator<S> onLast(final Iterator<S> iterator, final Runnable onLast) {
        return new Iterator<>() {
            boolean lastExecuted = false;

            @Override
            public boolean hasNext() {
                final boolean hasNext = iterator.hasNext();
                if (!hasNext && !this.lastExecuted) {
                    this.lastExecuted = true;
                    onLast.run();
                }
                return hasNext;
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public S next() {
                try {
                    return iterator.next();
                } finally {
                    if (!iterator.hasNext() && !this.lastExecuted) {
                        this.lastExecuted = true;
                        onLast.run();
                    }
                }
            }
        };
    }

    public static <S> List<S> list(final Iterator<S> iterator) {
        return fill(iterator, new ArrayList<>());
    }


    ///////////////

    public static final <S, E> Iterator<E> map(final Iterator<S> iterator, final Function<S, E> function) {
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public E next() {
                return function.apply(iterator.next());
            }
        };
    }

    public static final <S, E> Iterable<E> map(final Iterable<S> iterable, final Function<S, E> function) {
        return () -> IteratorUtils.map(iterable.iterator(), function);
    }

    ///////////////

    public static final <S> Iterator<S> filter(final Iterator<S> iterator, final Predicate<S> predicate) {


        return new Iterator<S>() {
            S nextResult = null;

            @Override
            public boolean hasNext() {
                if (null != this.nextResult) {
                    return true;
                } else {
                    advance();
                    return null != this.nextResult;
                }
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public S next() {
                try {
                    if (null != this.nextResult) {
                        return this.nextResult;
                    } else {
                        advance();
                        if (null != this.nextResult)
                            return this.nextResult;
                        else
                            throw FastNoSuchElementException.instance();
                    }
                } finally {
                    this.nextResult = null;
                }
            }

            private final void advance() {
                this.nextResult = null;
                while (iterator.hasNext()) {
                    final S s = iterator.next();
                    if (predicate.test(s)) {
                        this.nextResult = s;
                        return;
                    }
                }
            }
        };
    }

    public static final <S> Iterable<S> filter(final Iterable<S> iterable, final Predicate<S> predicate) {
        return () -> IteratorUtils.filter(iterable.iterator(), predicate);
    }

    ///////////////////

    public static final <S, E> Iterator<E> flatMap(final Iterator<S> iterator, final Function<S, Iterator<E>> function) {
        return new Iterator<E>() {

            private Iterator<E> currentIterator = Collections.emptyIterator();

            @Override
            public boolean hasNext() {
                if (this.currentIterator.hasNext())
                    return true;
                else {
                    while (iterator.hasNext()) {
                        this.currentIterator = function.apply(iterator.next());
                        if (this.currentIterator.hasNext())
                            return true;
                    }
                }
                return false;
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public E next() {
                if (this.hasNext())
                    return this.currentIterator.next();
                else
                    throw FastNoSuchElementException.instance();
            }
        };
    }

    public static <T> Stream<T> stream(final Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.IMMUTABLE | Spliterator.SIZED), false);
    }

    public static <T> Stream<T> stream(final Iterable<T> iterable) {
        return IteratorUtils.stream(iterable.iterator());
    }


    public static <T> Iterator<T> removeOnNext(final Iterator<T> iterator) {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public T next() {
                final T object = iterator.next();
                iterator.remove();
                return object;
            }
        };
    }

}
