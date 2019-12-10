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

package org.mmadt.machine.object.impl.composite;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.TestUtilities;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.Bindings;
import org.mmadt.util.ProcessArgs;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.machine.object.impl.__.neg;
import static org.mmadt.machine.object.impl.__.zero;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TLstTest implements TestUtilities {

    private final static ProcessArgs[] PROCESSING = new ProcessArgs[]{
            // instances
            ProcessArgs.args(List.of(TLst.of("a", 1)), TLst.of("a", 1)),
            ProcessArgs.args(List.of("a"), TLst.of("a", 1).get(0)),
            ProcessArgs.args(List.of(1), TLst.of("a", 1).get(1)),
            // references
            ProcessArgs.args(List.of(TLst.of("a", 1), TLst.of("b", 2)), TLst.of(TLst.of("a", 1), TLst.of("b", 2))),
            // ProcessArgs.args(List.of(TLst.of("a", "acac"), TLst.of("b", "bcbc")), TLst.of(TLst.of("a", 1), TLst.of("b", 2)).plus(zero()).put(1, get(0).plus("c").plus(zero()).plus(id()))),
            ProcessArgs.args(List.of(TLst.of("c", 1), TLst.of("c", 2)), TLst.of(TLst.of("a", 1), TLst.of("b", 2)).put(0, "c")),
            ProcessArgs.args(List.of(TLst.of("c", 1), TLst.of("c", 2)), TLst.of(TLst.of("a", 1), TLst.of("b", 2)).put(TInt.of(1).plus(2).plus(neg()), "c")),
            ProcessArgs.args(List.of(TLst.of("c", 1), TLst.of("c", 2)), TLst.of(TLst.of("a", 1), TLst.of("b", 2)).put(TInt.of(1).plus(2).plus(neg()), TStr.of("").plus("c").id().plus(zero()))),
            // types
            ProcessArgs.args(List.of(TLst.of(TInt.of(), TStr.of(), 1.0f, false)), TLst.of(TInt.of(), TStr.of()).plus(TLst.of(1.0f, false))),
            ProcessArgs.args(List.of(TLst.of(TInt.of(), TStr.of()).plus(TLst.of(TInt.of().mult(6), false))), TLst.of(TInt.of(), TStr.of()).plus(TLst.of(TInt.of().mult(6), false))),
            // state
            // ProcessArgs.of(List.of(TLst.of(TInt.of(), TStr.of(), 1.0f, false)), TLst.of(TInt.of().to("a"), TStr.of().to("b")).plus(TLst.of(1.0f, false)).from("a")),

    };

    @TestFactory
    Stream<DynamicTest> testTypes() {
        return Stream.of(PROCESSING).map(tp -> DynamicTest.dynamicTest(tp.input.toString(), () -> assertEquals(tp.expected, submit(tp.input))));
    }

    /*@Test
    void testInstanceReferenceType() {
        validateKinds(TLst.of("a", true, false), TLst.of(TLst.of("a"), TLst.of("b", 2), TLst.of("c", 5, true)), TLst.of().q(45));
        validateKinds(TLst.of(TLst.of("a", 2, 21.0)).q(2), TLst.of(TLst.of("a"), TLst.of("b", 2), TLst.of("c", 5, true)), TLst.of());
    }*/

    @Test
    void testType() {
        validateTypes(TLst.some());
    }

    @Test
    void testIsA() {
        validateIsA(TLst.some());
    }

    @Test
    void testPatterns() {
        assertTrue(TLst.of(TInt.of()).test(TLst.of(1)));
        assertTrue(TLst.of(TInt.of()).q(2).test(TLst.of(TInt.of(1)).q(2)));
        //  assertFalse(TLst.of(TInt.of()).q(2).test(TLst.of(TInt.of(1)).q(3)));
        assertTrue(TLst.of(TInt.of()).q(1, 4).test(TLst.of(TInt.of(1)).q(3)));
        assertTrue(TLst.of(TInt.of(), 2, "marko").q(1, 4).test(TLst.of(1, 2, "marko").q(3)));
        //  assertFalse(TLst.of(TInt.of(), 2, "marko").q(1, 4).test(TLst.of(1, 2, "marko").q(6)));
        assertFalse(TLst.of(TInt.of(), 2, TReal.of()).q(1, 4).test(TLst.of(1, 2, "marko").q(6)));
        assertTrue(TLst.of(TInt.of(), TLst.of(1, TInt.of(), 3), TReal.of()).q(1, 4).test(TLst.of(1, TLst.of(1, 2, 3), 0.2).q(4)));
    }

    @Test
    void shouldAndCorrectly() {
        Lst<Str> list1 = TLst.of("my", "name", "is", "marko");
        Lst<Str> list2 = TLst.of("my", "name", "is", TStr.of());
        assertTrue(list1.constant());
        assertFalse(list2.constant());
        assertTrue(list2.and(list1).constant());
        assertTrue(list1.and(list2).constant());
        assertEquals(list2.and(list1), list1.and(list2));
    }

    @Test
    void shouldOrCorrectly() {
        Lst<Str> list1 = TLst.of("my", "name", "is", "marko");
        Lst<Str> list2 = TLst.of("my", "name", "is", TStr.of());
        assertTrue(list1.constant());
        assertFalse(list2.constant());
        // System.out.println(list1.or(list2));
        assertFalse(list1.or(list2).constant());
//        assertEquals(list1.or(list2), list2.or(list1));
        assertTrue(list1.or(list2).isType());
        assertTrue(list1.or(list2).get() instanceof Inst);

    }

    @Test
    void shouldBindNestedLists() {
        Lst<Obj> list = TLst.of(TStr.of().label("a"), TInt.of(29).label("b"), TBool.of(true), TLst.of(TReal.of(), TReal.of().label("c"), TLst.of(TReal.of().label("d"))).label("e")).label("f");
        Lst<Obj> good = TLst.of("marko", 29, true, TLst.of(66.6f, 11.1f, TLst.of(1.0f)));
        Lst<Obj> bad1 = TLst.of("marko", 30, true, TLst.of(66.6f, 11.1f, TLst.of(1.0f)));
        Lst<Obj> bad2 = TLst.of(7, 29, true, TLst.of(66.6f, 11.1f, TLst.of(1.0f)));
        Lst<Obj> bad3 = TLst.of("marko", 29, true, TLst.of(66.6f, 11.1f, 1.0f));
        // System.out.println(good);
        // System.out.println(list);
        assertTrue(list.test(good));
        assertFalse(list.test(bad1));
        assertFalse(list.test(bad2));
        assertFalse(list.test(bad3));
        //
        final Bindings bindings = new Bindings();
        assertTrue(list.match(bindings, good));
        //System.out.println(bindings + "!!!");
        assertEquals(6, bindings.size());
        assertEquals(TStr.of("marko"), bindings.get("a"));
        assertEquals(TInt.of(29), bindings.get("b"));
        assertEquals(TReal.of(11.1f), bindings.get("c"));
        assertEquals(TReal.of(1.0f), bindings.get("d"));
        assertEquals(TLst.of(66.6f, 11.1f, TLst.of(1.0f)), bindings.get("e"));
        assertEquals(good, bindings.get("f"));
        //
        bindings.clear();
        assertFalse(list.match(bindings, bad1));
        assertEquals(0, bindings.size());
        assertFalse(list.match(bindings, bad2));
        assertEquals(0, bindings.size());
        assertFalse(list.match(bindings, bad3));
        assertEquals(0, bindings.size());
    }

    @Test
    void shouldSupportNoneEndings() {
        final Lst<Obj> a = TLst.of("marko", 29);
        assertFalse(TLst.of(TStr.of(), TObj.none()).test(a));
        assertTrue(TLst.of(TStr.of(), 29, TObj.none()).test(a));
        assertTrue(TLst.of(TStr.of(), 29, TObj.none(), TObj.none()).test(a));
        assertFalse(TLst.of(TStr.of(), TObj.none(), 29, TObj.none()).test(a));
    }
}
