/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.machine.object.impl.type.algebra;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.type.algebra.WithMonoidPlus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class WithMonoidPlusTest {

    private static final List<WithMonoidPlus> TEST_ARGS = List.of(
            //////////////// INSTANCES
            TLst.of(1),
            TInst.of(Tokens.ID),
            TStr.of("a"),
            TInt.of(1),
            TReal.of(1.0f),
            TBool.of(true),
            //////////////// REFERENCES
            TLst.of(TLst.of(1, "a", "x"), TLst.of(1, "c", 32), TLst.of(false, "abc", true)),
            TRec.of(TRec.of("a", 1, "b", 2), TRec.of("a", 2, "c", 3), TRec.of("e", 4)),
            TInst.of(List.of(TInst.of(Tokens.START, TInt.of(1)), TInst.of(Tokens.NEG), TInst.of(Tokens.PLUS, TInt.of(32)))),
            TStr.of("a", "b", "c"),
            TInt.of(1, 2, 3, 4, 5, 6),
            TReal.of(1.0f, 2.0f, 3.0f),
            TBool.of(true, true, false));

    static <A extends WithMonoidPlus<A>> void validate(final A monoid) {
        if (monoid.isInstance())
            testInstances(monoid);
        else if (monoid.isReference())
            testReferences(monoid);
        else
            throw new RuntimeException("Bad: " + monoid);
    }

    static <A extends WithMonoidPlus<A>> void testInstances(final A monoid) {
        final A two = monoid.plus(monoid);
        final A three = two.plus(monoid);
        final A four = three.plus(monoid);
        assertFalse(monoid.isZero());
        assertTrue(monoid.zero().isZero());
        assertNotEquals(monoid, monoid.zero());
        // 0 + 0 = 0
        assertEquals(monoid.zero(), monoid.zero().plus(monoid.zero()));
        // x + 0 = x
        assertEquals(two, two.plus(monoid.zero()));
        // 0 + x = x
        assertEquals(two, monoid.zero().plus(two));
        // (a+b)+c = a+(b+c)
        assertEquals(two.plus(three).plus(four), two.plus(three.plus(four)));
    }

    static <A extends WithMonoidPlus<A>> void testReferences(final A monoid) {
        A running = monoid;
        A second = monoid;
        // assertFalse(monoid.isZero()); // TODO: Why?
        for (int i = 0; i < 10; i++) {
            // assertEquals(running.access(running.access().mult(TInst.of(Tokens.ZERO))), second.zero());
            assertEquals(running = running.access(running.access().mult(TInst.of(Tokens.PLUS, monoid))), second = second.plus(monoid));
        }
    }

    @TestFactory
    Stream<DynamicTest> testMonoidPlus() {
        return TEST_ARGS.stream().map(algebra -> DynamicTest.dynamicTest(algebra.toString(), () -> validate(algebra)));
    }
}
