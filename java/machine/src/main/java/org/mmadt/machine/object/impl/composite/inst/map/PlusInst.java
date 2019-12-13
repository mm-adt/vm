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
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.atomic.Real;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.machine.object.model.ext.algebra.WithPlus;

import java.util.function.Supplier;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class PlusInst<S extends WithPlus<S>> extends TInst<S, S> implements MapInstruction<S, S> {

    private PlusInst(final Object arg) {
        super(PList.of(Tokens.PLUS, arg));
    }

    public S apply(final S obj) {
        return this.quantifyRange(obj.plus(this.<S>argument(0).mapArg(obj)));
    }

    public static <S extends WithPlus<S>> PlusInst<S> create(final Object arg) {
        return new PlusInst<>(arg);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Bool compute(final Bool lhs, final Bool rhs) {
        return (lhs.isInstance() && rhs.isInstance()) ?
                lhs.set(exclusiveOr(lhs.java(), rhs.java())) :
                PlusInst.<Bool>create(rhs).attach(lhs);
    }

    public static Int compute(final Int lhs, final Int rhs) {
        return (lhs.isInstance() && rhs.isInstance()) ?
                lhs.set(tryCatch(() -> Math.addExact(lhs.java(), rhs.java()), Integer.MAX_VALUE)) :
                PlusInst.<Int>create(rhs).attach(lhs);
    }

    public static Real compute(final Real lhs, final Real rhs) {
        return (lhs.isInstance() && rhs.isInstance()) ?
                lhs.set(lhs.java() + rhs.java()) :
                PlusInst.<Real>create(rhs).attach(lhs);
    }

    public static Str compute(final Str lhs, final Str rhs) {
        return (lhs.isInstance() && rhs.isInstance()) ?
                lhs.set(lhs.java().concat(rhs.java())) :
                PlusInst.<Str>create(rhs).attach(lhs);
    }

    //////////////////////////////////////////////
    /////////////// HELPER METHODS ///////////////
    //////////////////////////////////////////////

    private static boolean exclusiveOr(final boolean a, final boolean b) {
        return (a && !b) || (!a && b);
    }

    private static Object tryCatch(final Supplier<Object> function, final Object failValue) {
        try {
            return function.get();
        } catch (final ArithmeticException e) {
            return failValue;
        }
    }

}
