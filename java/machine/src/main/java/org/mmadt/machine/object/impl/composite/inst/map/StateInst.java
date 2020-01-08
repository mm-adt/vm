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

package org.mmadt.machine.object.impl.composite.inst.map;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.composite.util.PList;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class StateInst<S extends Obj, K extends Obj, V extends Obj> extends TInst<S, Rec<K, V>> implements MapInstruction<S, Rec<K, V>> {

    private StateInst() {
        super(PList.of(Tokens.STATE));
    }

    public Rec<K, V> apply(final S obj) {
        return this.quantifyRange(TRec.of(obj.model().bindings()));
    }

    public static <S extends Obj, K extends Obj, V extends Obj> StateInst<S, K, V> create() {
        return new StateInst<>();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static <S extends Obj, K extends Obj, V extends Obj> Rec<K, V> compute(final S obj) {
        return obj.isInstance() ?
                TRec.of(obj.model().bindings()) :
                StateInst.<S, K, V>create().attach(obj);
    }
}