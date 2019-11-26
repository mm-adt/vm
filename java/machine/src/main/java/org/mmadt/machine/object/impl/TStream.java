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

package org.mmadt.machine.object.impl;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Stream;
import org.mmadt.machine.object.model.type.Bindings;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.Pattern;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.StringFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TStream<A extends Obj> implements Stream<A> {

    private static final TStream EMPTY_STREAM = new TStream<>(List.of());

    public final List<A> objects;

    private TStream(final List<A> objects) {
        this.objects = new ArrayList<>(objects);
    }

    public static <A extends Obj> TStream<A> of(final List objects) {
        if (objects.isEmpty())
            return EMPTY_STREAM;
        else {
            final PList<A> list = new PList<>();
            for (final Object object : objects) {
                if (object instanceof Pattern) {
                    if (object instanceof Obj && ((Obj) object).get() instanceof Stream)
                        ((Stream<A>) ((A) object).<A>get()).forEach(list::add);
                    else
                        list.add((A) object);
                } else {
                    list.add((A) ObjectHelper.from(object));
                }
            }
            return new TStream<>(list);
        }
    }


    @Override
    public void drop(final A object) {
        this.objects.remove(object);
    }
    
    @Override
    public Pattern bind(final Bindings bindings) {
        final List<A> list = new ArrayList<>();
        for (final A a : this) {
            list.add((A) a.bind(bindings));
        }
        return TStream.of(list);
    }

    @Override
    public Iterator<A> iterator() {
        return this.objects.iterator();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), this.objects);
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof TStream &&
                Objects.equals(this.objects, ((TStream) object).objects);
    }

    @Override
    public String toString() {
        return StringFactory.stream(this);
    }
}
