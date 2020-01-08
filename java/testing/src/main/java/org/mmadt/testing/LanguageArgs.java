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

package org.mmadt.testing;

import org.junit.jupiter.api.DynamicTest;
import org.mmadt.machine.object.impl.TModel;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.processor.util.FastProcessor;
import org.mmadt.util.IteratorUtils;

import javax.script.ScriptEngine;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class LanguageArgs<A extends Obj> {
    public final List<A> expected;
    public final String input;
    private final Map<Obj, Obj> expectedState;

    private LanguageArgs(final List<A> expected, final List<A> expectedState, final String input) {
        this.expected = expected;
        this.expectedState = null == expectedState ? null : expectedState.stream().collect(Collectors.toMap(Obj::clone, Obj::clone));
        this.input = input;
    }

    public static <A extends Obj> LanguageArgs<A> args(final List<A> expected, final String input) {
        return new LanguageArgs<>(expected, null, input);
    }

    public static <A extends Obj> LanguageArgs<A> except(final Exception expected, final String input) {
        return new LanguageArgs<>(null, null, input);
    }

    public static <A extends Obj> LanguageArgs<A> args(final A expected, final String input) {
        return new LanguageArgs<>(List.of(expected), null, input);
    }

    public static <A extends Obj> LanguageArgs<A> args(final A expected, final List<A> expectedState, final String input) {
        return new LanguageArgs<>(List.of(expected), expectedState, input);
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

    public static Lst lsts(final Object... lists) {
        return TLst.of(lists);
    }

    public static Rec recs(final Object... maps) {
        return TRec.of(maps);
    }

    public static Obj process(final Obj obj) {
        return FastProcessor.process(obj).next();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public DynamicTest execute(final ScriptEngine engine) {
        return DynamicTest.dynamicTest(this.input, () -> {
            if (null == this.expected) {
                try {
                    IteratorUtils.list((Iterator<A>) engine.eval(this.input));
                    fail();
                } catch (Exception e) {
                    assertTrue(true);
                }
            } else {
                final List<A> results = IteratorUtils.list((Iterator<A>) engine.eval(this.input));
                assertEquals(this.expected, results);
                if (null != this.expectedState) {
                    final A obj = results.get(0);
                    assertEquals(TModel.of(this.expectedState), obj.model());
                }
            }
        });
    }

}