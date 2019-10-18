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

package org.mmadt.process.compliance;

import org.mmadt.language.Traversal;
import org.mmadt.object.impl.TObj;
import org.mmadt.object.model.Obj;
import org.mmadt.util.IteratorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class AbstractTest implements TestModel {

    <E extends Obj> List<E> submit(final Traversal query) {
        return IteratorUtils.list(provider().machine().submit(query.bytecode()));
    }

    <E extends Obj> List<E> objs(final Object... objects) {
        final List<E> objs = new ArrayList<>();
        for (final Object object : objects) {
            objs.add((E) TObj.from(object));
        }
        return objs;
    }
}
