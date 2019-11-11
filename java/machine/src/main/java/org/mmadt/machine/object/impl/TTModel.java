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

package org.mmadt.machine.object.impl;

import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.model.MModel;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.util.ModelCache;
import org.mmadt.machine.object.model.util.StringFactory;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TTModel<S extends Obj> extends TObj implements MModel<S> {

    private final Str name;

    private static final MModel SOME = new TTModel<>(null, null);

    public static MModel some() {
        return SOME;
    }

    private TTModel(final Str name, final Object value) {
        super(value);
        this.name = name;
    }

    public static <S extends Obj> MModel<S> of(final String name, final Rec<Str, S> definitions) {
        final PMap<Str, S> map = new PMap<>();
        map.putAll(definitions.java());
        final MModel<S> model = new TTModel<>(TStr.of(name), map);
        ModelCache.CACHE.put(name, model);
        return model;
    }

    @Override
    public Str name() {
        return this.name;
    }

    @Override
    public String toString() {
        return StringFactory.mmodel(this);
    }
}
