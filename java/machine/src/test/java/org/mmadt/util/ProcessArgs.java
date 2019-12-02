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

package org.mmadt.util;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.util.ObjectHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class ProcessArgs<A extends Obj> {
    public final List<A> expected;
    public final A input;

    private ProcessArgs(final List<A> expected, final A input) {
        this.expected = expected;
        this.input = input;
    }

    public static <A extends Obj> ProcessArgs<A> args(final List<Object> expected, final A input) {
        return new ProcessArgs<>(objs(expected), input);
    }

    private static <A extends Obj> List<A> objs(final List<Object> objects) {
        final List<A> objs = new ArrayList<>();
        for (final Object object : objects) {
            objs.add((A) ObjectHelper.from(object));
        }
        return objs;
    }


}