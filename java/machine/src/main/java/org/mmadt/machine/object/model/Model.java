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

package org.mmadt.machine.object.model;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.storage.Storage;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Model extends Function<Obj, Obj>, Cloneable {

    static final ServiceLoader<Storage> STORAGES = ServiceLoader.load(Storage.class);

    Obj apply(final Obj inst);

    public default <O extends Obj> O readOrGet(final Obj key, final O missing) {
        final O o = (O) this.apply(key);
        return TObj.none().equals(o) ? missing : o;
    }

    public Map<Obj, Obj> bindings();

    public Model write(final Obj value);

    public Model clone();

}
