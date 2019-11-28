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

package org.mmadt.language.mmlang.util;

import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.processor.util.FastProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class ParserArgs<A extends Obj> {
    public final List<A> expected;
    public final String input;

    private ParserArgs(final List<A> expected, final String input) {
        this.expected = expected;
        this.input = input;
    }

    public static <A extends Obj> ParserArgs<A> args(final List<A> expected, final String input) {
        return new ParserArgs<>(expected, input);
    }

    public static <A extends Obj> ParserArgs<A> args(final A expected, final String input) {
        return new ParserArgs<>(List.of(expected), input);
    }

    public static <A extends Obj> List<A> objs(final Object... objects) {
        final List<A> objs = new ArrayList<>();
        for (final Object object : objects) {
            objs.add((A) ObjectHelper.from(object));
        }
        return objs;
    }

    public static Int ints(final Object... integers) {
        return TInt.of(integers);
    }

    public static Bool bools(final Object... booleans) {
        return TBool.of(booleans);
    }

    public static Str strs(final Object... strings) {
        return TStr.of(strings);
    }


    public static Obj process(final Obj obj) {
        return FastProcessor.process(obj).next();
    }


}