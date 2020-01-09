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
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.composite.inst.filter.IsInst;
import org.mmadt.machine.object.impl.composite.inst.map.GtInst;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.util.ProcessArgs;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.machine.object.impl.__.and;
import static org.mmadt.machine.object.impl.__.eq;
import static org.mmadt.machine.object.impl.__.gt;
import static org.mmadt.machine.object.impl.__.id;
import static org.mmadt.machine.object.impl.__.is;
import static org.mmadt.machine.object.impl.__.neq;
import static org.mmadt.machine.object.impl.__.or;
import static org.mmadt.machine.object.impl.__.zero;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.star;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.zero;
import static org.mmadt.util.ProcessArgs.args;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TStrTest implements TestUtilities {

    private final static ProcessArgs[] PROCESSING = new ProcessArgs[]{
            args(List.of("marko"), TStr.of("marko")),
            args(List.of("marko rodriguez"), TStr.of("marko").plus(zero()).plus(" ").plus("rodriguez").plus(zero())),
            args(List.of("abcde"), TStr.of("a").plus("b").plus(TStr.of("c").plus("d")).plus("e")),
            args(List.of("abcdef"), TStr.of("a").plus("b").plus(TStr.of("c").plus("d")).plus("e").is(gt("")).plus("f")),
            args(List.of("abcde", "aabcde"), TStr.of("a", "aa").plus("b").plus(TStr.of("c").plus("d")).plus("e")),
            args(List.of("abcde", "abcde", "aabcde", "aabcde"), TStr.of("a", "aa").plus("b").<Str>branch(id(), id()).plus(TStr.of("c").plus("d")).plus("e")), // TODO: test q() to make sure its {4}
            // ProcessArgs.args(List.of("a"), TStr.of("a", "a","a").branch(id(),id()).dedup()),
    };

    @TestFactory
    Stream<DynamicTest> testProcessing() {
        return Stream.of(PROCESSING).map(tp -> DynamicTest.dynamicTest(tp.input.toString(), () -> assertEquals(tp.expected, submit(tp.input))));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final static Str[] INSTANCES = new Str[]{
            TStr.of("a"),
            TStr.of("a").q(2),
            TStr.of().min()
    };

    @TestFactory
    Stream<DynamicTest> testInstances() {
        return Stream.of(INSTANCES).map(obj -> DynamicTest.dynamicTest(obj.toString(), () -> assertTrue(obj.isInstance())));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final static Str[] TYPES = new Str[]{
            TStr.of(),
            TStr.of().q(1, 45),
            TStr.of().q(45)
    };

    @TestFactory
    Stream<DynamicTest> testTypes() {
        return Stream.of(TYPES).map(obj -> DynamicTest.dynamicTest(obj.toString(), () -> assertTrue(obj.isType())));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final static Str[] REFERENCES = new Str[]{
            TStr.of("a", "b").plus(TStr.of("b")),
            TStr.of("a", "b", "c"),
            TStr.of("a", "b", "c").q(3)
    };

    @TestFactory
    Stream<DynamicTest> testReferences() {
        return Stream.of(REFERENCES).map(obj -> DynamicTest.dynamicTest(obj.toString(), () -> assertTrue(obj.isReference())));
    }

    @Test
    void testType() {
        validateTypes(TStr.of());
    }

    @Test
    void testIsA() {
        validateIsA(TStr.of());
    }

    @Test
    void testString() {
        assertEquals("str", TStr.of().toString());
        assertEquals("'marko'", TStr.of("marko").toString());
        assertEquals("str[is,[gt,'a']]", TStr.of(IsInst.create(GtInst.create("a"))).toString());
        assertEquals("(str[is,[gt,'a']])~x", TStr.of(IsInst.create(GtInst.create("a"))).bind("x").toString()); // TODO: perhaps str([is]){x,y}~a
        assertEquals("(str[is,[gt,'a']]){0,2}", TStr.of(IsInst.create(GtInst.create("a"))).q(0, 2).toString());
        assertEquals("(str[is,[gt,'a']]){0,2}~x", TStr.of(IsInst.create(GtInst.create("a"))).q(0, 2).bind("x").toString());
        assertEquals("'marko'", TStr.of("marko").and(TStr.of("marko")).toString());
        assertEquals("'marko'{*}", TStr.of("marko").q(star).and(TStr.of("marko")).toString());
        assertEquals("'marko'{0}", TStr.of("marko").q(star).and(TStr.of("marko").q(zero)).toString());
    }

    @Test
    void shouldHaveTypeBasics() {
        assertTrue(TStr.of().test(TStr.of("hello")));
        assertFalse(TInt.of().test(TStr.of("hello")));
        assertTrue(TObj.all().test(TStr.of("hello")));
        assertTrue(TStr.of("hello").q(star).test(TStr.of("hello")));
        assertFalse(TStr.of("hello").q(zero).test(TStr.of("hello")));
        assertTrue(TStr.of(is(eq("id"))).test(TStr.of("id")));
        assertTrue(TStr.of(is(or(eq("id"), eq("label")))).test(TStr.of("id")));
        assertTrue(TStr.of("id").or(TStr.of("label")).test(TStr.of("id")));
        assertFalse(TStr.of(is(and(neq("id"), neq("label")))).test(TStr.of("id")));
        assertTrue(TStr.of(is(and(neq("id"), neq("label")))).test(TStr.of("hello")));
    }
}
