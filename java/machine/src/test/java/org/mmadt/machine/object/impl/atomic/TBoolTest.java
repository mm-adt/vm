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

package org.mmadt.machine.object.impl.atomic;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.TestUtilities;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.util.ProcessArgs;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.machine.object.impl.__.and;
import static org.mmadt.machine.object.impl.__.gt;
import static org.mmadt.machine.object.impl.__.id;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.one;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.plus;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.qmark;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.star;
import static org.mmadt.util.ProcessArgs.args;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TBoolTest implements TestUtilities {

    private final static ProcessArgs[] PROCESSING = new ProcessArgs[]{
            args(List.of(true), TBool.of(true)),
            args(List.of(true, true, false, false), TBool.of(true, true, false, false)),
            args(List.of(true, true, false, false), TBool.of(true, false).branch(id(), id())),
            args(List.of(), TBool.of(true, false).<Bool>branch(id(), id()).is(and(false)).id().id()),
            args(List.of(TBool.of().mapFrom(gt(10))), TInt.of().gt(10)),
            // ProcessArgs.of(List.of(true), TBool.of(true, false).branch(id(), id()).is(or(false)).id().id()),
    };

    @TestFactory
    Stream<DynamicTest> testProcessing() {
        return Stream.of(PROCESSING).map(tp -> DynamicTest.dynamicTest(tp.input.toString(), () -> assertEquals(tp.expected, submit(tp.input))));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final static Bool[] INSTANCES = new Bool[]{
            TBool.of(true),
            TBool.of(false),
            TBool.of().zero(),
    };

    @TestFactory
    Stream<DynamicTest> testInstances() {
        return Stream.of(INSTANCES).map(obj -> DynamicTest.dynamicTest(obj.toString(), () -> assertTrue(obj.isInstance())));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final static Bool[] TYPES = new Bool[]{
            TBool.all(),
            TBool.of().q(1, 45),
            TBool.of(),
    };

    @TestFactory
    Stream<DynamicTest> testTypes() {
        return Stream.of(TYPES).map(obj -> DynamicTest.dynamicTest(obj.toString(), () -> assertTrue(obj.isType())));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final static Bool[] REFERENCES = new Bool[]{
            TBool.of(true, false, true, false),
            TBool.of(true, false).q(10),
            TBool.of(true, false, true, false).plus(true), // TODO: should just be true{4}
    };

    @TestFactory
    Stream<DynamicTest> testReferences() {
        return Stream.of(REFERENCES).map(obj -> DynamicTest.dynamicTest(obj.toString(), () -> assertTrue(obj.isReference())));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    void testType() {
        validateTypes(TBool.of());
    }

    @Test
    void testIsA() {
        validateIsA(TBool.of());
    }

    @Test
    void shouldAndCorrectly() {
        assertEquals("true{*}", TBool.of(true).q(star).toString());
        assertEquals(TBool.of(true), TBool.of(true).and(TBool.of(true)));
        assertEquals(TBool.of(true).q(one), TBool.of(true).and(TBool.of(true)));
        assertEquals(TBool.of(true).q(star), TBool.of(true).q(plus).and(TBool.of(true).q(qmark)));
        assertEquals("true{*}", TBool.of(true).q(star).and(TBool.of()).toString());
        assertEquals("false{*}~x", TBool.of(false).q(qmark).label("x").and(TBool.of().q(plus)).toString());
        assertEquals("false{*}~x", TBool.of(false).q(qmark).label("x").and(TBool.of().label("x").q(plus)).toString());
        assertEquals(TBool.of(false), TBool.of(true).and(TBool.of(false)));
        assertThrows(RuntimeException.class, () -> TBool.of(false).q(qmark).label("x").and(TBool.of().q(plus).label("y")));
        assertEquals("false", TBool.of().and(TBool.of(false)).toString());
    }

    @Test
    void shouldOrCorrectly() {
        assertEquals(TBool.of(true), TBool.of(true).or(TBool.of(true)));
        //      assertEquals("true{+}|true{?}", TBool.of(true).q(plus).or(TBool.of(true).q(qmark)).toString());
//      assertEquals("bool|false", TBool.of().or(TBool.of(false)).toString());
        assertEquals("true", TBool.of(true).or(TBool.of(false)).toString());
        //        assertEquals("true{*}|false", TBool.of(true).q(star).or(TBool.of(false)).toString());
        //       assertEquals("true{*}~x|false~y", TBool.of(true).q(star).label("x").or(TBool.of(false).label("y")).toString());
        //       assertEquals("(true{*}~x|false~y)~z", TBool.of(true).q(star).label("x").or(TBool.of(false).label("y")).label("z").toString());
        //       assertEquals("(true{*}~x|false~y){?}", TBool.of(true).q(star).label("x").or(TBool.of(false).label("y")).q(qmark).toString());
    }

}
