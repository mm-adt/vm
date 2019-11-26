/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.processor.util;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.util.FastNoSuchElementException;
import org.mmadt.util.IteratorUtils;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Spliterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class ObjSet<S extends Obj> extends AbstractSet<S> implements Set<S>, Queue<S>, Serializable {

    private final Map<S, S> map = Collections.synchronizedMap(new LinkedHashMap<>());

    @SafeVarargs
    public static <S extends Obj> ObjSet<S> create(final S... objs) {
        final ObjSet<S> set = new ObjSet<>();
        Collections.addAll(set, objs);
        return set;
    }

    private ObjSet() {
    }

    @Override
    public Iterator<S> iterator() {
        return this.map.values().iterator();
    }

    public S get(final S obj) {
        return this.map.get(obj);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    public long count() {
        long count = 0L;
        for (final S obj : this.map.values()) {
            count = count + obj.q().peek().<Integer>get(); // TODO: smarter count-based quantification
        }
        return count;
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean contains(final Object obj) {
        return this.map.containsKey(obj);
    }

    @Override
    public boolean add(final S obj) {
        synchronized (this.map) {
            final S existing = this.map.get(obj);
            if (null == existing) {
                this.map.put(obj, obj);
                return true;
            } else {
                existing.q(existing.q().plus(obj.q()));
                return false;
            }
        }
    }

    @Override
    public boolean offer(final S traverser) {
        return this.add(traverser);
    }

    @Override
    public S remove() {  // pop, exception if empty
        synchronized (this.map) {
            final Iterator<S> iterator = this.map.values().iterator();
            if (!iterator.hasNext())
                throw FastNoSuchElementException.instance();
            final S next = iterator.next();
            iterator.remove();
            return next;
        }
    }

    @Override
    public S poll() {  // pop, null if empty
        return this.map.isEmpty() ? null : this.remove();
    }

    @Override
    public S element() { // peek, exception if empty
        return this.iterator().next();
    }

    @Override
    public S peek() { // peek, null if empty
        return this.map.isEmpty() ? null : this.iterator().next();
    }

    @Override
    public boolean remove(final Object obj) {
        return this.map.remove(obj) != null;
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Spliterator<S> spliterator() {
        return this.map.values().spliterator();
    }

    @Override
    public String toString() {
        return this.map.values().toString();
    }

    public void sort(final Comparator<S> comparator) {
        final List<S> list = new ArrayList<>(this.map.size());
        IteratorUtils.removeOnNext(this.map.values().iterator()).forEachRemaining(list::add);
        Collections.sort(list, comparator);
        this.map.clear();
        list.forEach(traverser -> this.map.put(traverser, traverser));
    }

    public void shuffle() {
        final List<S> list = new ArrayList<>(this.map.size());
        IteratorUtils.removeOnNext(this.map.values().iterator()).forEachRemaining(list::add);
        Collections.shuffle(list);
        list.forEach(obj -> this.map.put(obj, obj));
    }
}
