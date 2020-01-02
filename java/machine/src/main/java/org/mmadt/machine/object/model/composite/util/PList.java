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

package org.mmadt.machine.object.model.composite.util;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.model.Bindings;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Pattern;
import org.mmadt.machine.object.model.util.ObjectHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class PList<V extends Obj> extends ArrayList<V> implements Pattern {

    private static final PList<Obj> EMPTY_LIST = new PList<>(0);

    public PList() {
        super();
    }

    public PList(final int size) {
        super(size);
    }

    public PList(final List<V> list) {
        super(list);
    }

    public static <V extends Obj> PList<V> of(final Object... objects) {
        final List<V> list = new ArrayList<>();
        for (final Object object : objects) {
            list.add((V) ObjectHelper.from(object));
        }
        return new PList<>(list);
    }

    public static <V extends Obj> PList<V> of() {
        return (PList<V>) EMPTY_LIST;
    }

    @Override
    public boolean test(final Obj object) {
        if (!(object.get() instanceof List))
            return false;
        final List<V> other = object.get();
        for (int i = 0; i < this.size(); i++) {
            if (other.size() <= i)
                return this.get(i).test(TObj.none());
            if (!this.get(i).test(other.get(i)))
                return false;
        }
        return true;
    }

    @Override
    public boolean match(final Bindings bindings, final Obj object) {
        if (!(object.get() instanceof List))
            return false;
        bindings.start();
        final List<V> other = object.get();
        for (int i = 0; i < this.size(); i++) {
            final V thisValue = this.get(i);
            final V otherValue = other.get(i);
            if (otherValue.isNamed() && otherValue.symbol().equals(thisValue.symbol())) {
                if (null != thisValue.label())
                    bindings.put(thisValue.label(), otherValue);
            } else if (!thisValue.match(bindings, otherValue)) {
                bindings.rollback();
                return false;
            }
        }
        bindings.commit();
        return true;
    }


    @Override
    public final boolean constant() {
        for (final V entry : this) {
            if (!entry.constant())
                return false;
        }
        return true;
    }
}
