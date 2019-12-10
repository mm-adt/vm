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

package org.mmadt.machine.object.impl.composite.inst.barrier;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.inst.BarrierInstruction;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.processor.util.ObjSet;
import org.mmadt.util.IteratorUtils;

import java.util.Iterator;

import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.one;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class DedupInst<S extends Obj> extends TInst<S, S> implements BarrierInstruction<S, ObjSet<S>> {

    private DedupInst(final Object... arguments) {
        super(PList.of(arguments));
        this.<PList<Obj>>get().add(0, TStr.of(Tokens.DEDUP));
    }

    @Override
    public ObjSet<S> getInitialValue() {
        return ObjSet.create();
    }

    @Override
    public ObjSet<S> merge(final ObjSet<S> barrierA, final ObjSet<S> barrierB) {
        final ObjSet<S> set = ObjSet.create();
        set.addAll(barrierA);
        set.addAll(barrierB);
        return set;
    }

    @Override
    public Iterator<ObjSet<S>> createIterator(final ObjSet<S> barrier) {
        return IteratorUtils.map(barrier.iterator(), a -> ObjSet.create(a.q(one)));
    }

    @Override
    public ObjSet<S> apply(final S s, final ObjSet<S> seed) {
        seed.add(s);
        return seed;
    }

    public static <S extends Obj> DedupInst<S> create(final Object... projections) {
        return new DedupInst<>(projections);
    }
}
