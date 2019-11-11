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

package org.mmadt.machine.object.model;

import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.type.algebra.WithProduct;
import org.mmadt.processor.util.FastProcessor;

import java.util.List;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface MModel<S extends Obj> extends WithProduct<Str, S> {

    public default Map<Str, S> java() {
        return this.get();
    }

    public Str name();

    @Override
    public default MModel<S> put(final Str symbol, final S obj) {
        this.java().put(symbol, obj);
        return this;
    }

    @Override
    public default MModel<S> drop(final Str symbol) {
        this.java().remove(symbol);
        return this;
    }

    @Override
    public default S get(final Str symbol) {
        return this.java().get(symbol);
    }

    @Override
    public default Iterable<MModel<S>> iterable() {
        return this.isInstance() ? List.of(this) : () -> new FastProcessor<MModel<S>, MModel<S>>(this.access()).iterator(this);
    }

}
