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

package org.mmadt.machine.object.impl.composite.inst.initial;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.inst.InitialInstruction;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.processor.compiler.Argument;
import org.mmadt.util.IteratorUtils;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class StartInst<S extends Obj> extends TInst implements InitialInstruction<S> {

    private StartInst(final Object... arguments) {
        super(PList.of(arguments));
        this.<PList<Obj>>get().add(0, TStr.of(Tokens.START));
    }

    public static <S extends Obj> StartInst<S> create(final Object... args) {
        final StartInst<S> inst = new StartInst<>(args);
        Obj kind = inst.args().isEmpty() ? TObj.none() : inst.args().get(0).set(null); // TODO: for lst and rec, I think this should be .setValue()
        for (int i = 1; i < inst.args().size(); i++) {
            kind = ObjectHelper.root(kind, inst.args().get(i)).q(kind.q().plus(inst.args().get(i).q()));
        }
        return (StartInst<S>) inst.domainAndRange(TObj.none(), kind);
    }

    public S computeRange(final Obj domain) {
        return (S) this.range;
    }

    @Override
    public S apply(final S obj) {
        return TObj.none().set(IteratorUtils.<S, S>map(IteratorUtils.asIterator(this.args()), arg -> Argument.<Obj, S>create(arg).mapArg(TObj.none())));
    }
}
