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
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.machine.object.model.util.ModelHelper;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.util.IteratorUtils;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class StartInst<S extends Obj> extends TInst<Obj, S> implements InitialInstruction<S> {

    private StartInst(final Object... arguments) {
        super(PList.of(arguments));
        this.<PList<Obj>>get().add(0, TStr.of(Tokens.START));
    }

    @Override
    public S apply(final Obj obj) { // obj is ignored because [start] is an initial
        return StartInst.instances(IteratorUtils.map(this.<S>args().iterator(), arg -> (S) ModelHelper.model(arg)));
    }

    @Override
    public S quantifyRange(final Obj range) {
        return (S) this.range;
    }

    public static <S extends Obj> StartInst<S> create(final Object... args) {
        final StartInst<S> inst = new StartInst<>(args);
        Obj kind = inst.args().isEmpty() ? TObj.none() : inst.args().get(0).type();
        for (int i = 1; i < inst.args().size(); i++) {
            kind = ObjectHelper.root(kind, inst.args().get(i)).q(kind.q().plus(inst.args().get(i).q()));
        }
        return (StartInst<S>) inst.domainAndRange(TObj.none(), kind);
    }

    public static <S extends Obj> S instances(final Iterator<S> iterator) {
        if (!iterator.hasNext())
            return (S) TObj.none();
        final Object[] instances = IteratorUtils.list(iterator).toArray(new Object[]{});
        if (1 == instances.length)
            return (S) instances[0];
        final StartInst<S> start = StartInst.create(instances);
        return start.quantifyRange(TObj.none()).access(start);
    }
}
