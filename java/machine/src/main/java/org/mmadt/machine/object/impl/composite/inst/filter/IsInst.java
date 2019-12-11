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

package org.mmadt.machine.object.impl.composite.inst.filter;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.inst.map.AInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.inst.FilterInstruction;
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.machine.object.model.util.InstHelper;

import java.util.Optional;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class IsInst<S extends Obj> extends TInst<S, S> implements FilterInstruction<S> {

    private IsInst(final Object argument) {
        super(PList.of(Tokens.IS, argument));
    }

    public S apply(final S obj) {
        return FilterInstruction.super.quantifyRange(obj.is(this.<Bool>argument(0).mapArg(obj)));
    }

    public static <S extends Obj> IsInst<S> create(final Object arg) {
        return new IsInst<>(arg);
    }

    public S quantifyRange(final S domain) {
        return FilterInstruction.super.quantifyRange(isARange().orElse(domain));
    }

    private Optional<S> isARange() {
        final Obj arg = this.args().get(0);
        final Inst inst = InstHelper.first(arg.access());
        return inst instanceof AInst && !(inst.args().get(0).get() instanceof Inst) ? Optional.of((S) inst.args().get(0)) : Optional.empty();
    }

    public static <S extends Obj> S compute(final S from, final Bool bool) {
        return from.isInstance() && bool.isInstance() ?
                bool.java() ? from : from.kill() :
                IsInst.<S>create(bool).attach(from);
    }
}
