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
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.type.algebra.WithGroupPlus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class WithGroupPlusTest {

    private static final List<WithGroupPlus> TEST_ARGS = List.of(
            TInst.of(Tokens.ID),
            TReal.of(1.0f),
            TInt.of(1),
            TBool.of(true),
            /////
            // TInst.of(Tokens.ID),
            TReal.of(1.0f, 2.0f, 3.0f),
            // TInt.of(1),
            TBool.of(true, false, false));

    static void validate(final WithGroupPlus group) {
        WithMonoidPlusTest.validate(group);
        ///
        if (group.isInstance())
            testInstances(group);
        else if (group.isReference())
            testReferences(group);
        else
            throw new RuntimeException("Bad: " + group);

    }

    static void testInstances(final WithGroupPlus group) {
        final WithGroupPlus two = group.plus(group);
        final WithGroupPlus three = group.plus(group).plus(group);
        final WithGroupPlus four = group.plus(group).plus(group).plus(group);
        assertFalse(group.isZero());
        assertTrue(group.zero().isZero());
        assertNotEquals(group, group.zero());
        // x = x + 0
        assertEquals(group, group.plus(group.zero()));
        assertEquals(two, two.plus(group.zero()));
        // x = 0 + x
        assertEquals(group, group.zero().plus(group));
        assertEquals(two, group.zero().plus(two));
        // 0 = 0 + 0
        assertEquals(group.zero(), group.zero().plus(group.zero()));
        // (a+b)+c = a+(b+c)
        assertEquals(two.plus(three).plus(four), two.plus(three.plus(four)));
        // 0 = a - a
        assertEquals(group.zero(), group.minus(group));
        assertEquals(group.zero(), two.minus(two));
        // a = -(-a)
        assertEquals(group, group.negate().negate());
        assertEquals(two, two.negate().negate());
        assertEquals(three, three.negate().negate());
        // -a = 0 - a
        assertEquals(group.negate(), group.zero().minus(group));
        assertEquals(two.negate(), group.zero().minus(two));
        assertEquals(three.negate(), group.zero().minus(three));
    }

    static void testReferences(final WithGroupPlus group) {
        WithGroupPlus running = group;
        WithGroupPlus second = group;
        for (int i = 0; i < 10; i++) {
            assertEquals(running.access(running.access().mult(TInst.of(Tokens.ZERO)).mult(TInst.of(Tokens.NEG))), second.zero().negate());
            assertEquals(running.access(running.access().mult(TInst.of(Tokens.PLUS, group.negate()))), second.minus(group));
            assertEquals(running.access(running.access().mult(TInst.of(Tokens.PLUS, group)).mult(TInst.of(Tokens.NEG))), second.plus(group).negate());
            assertEquals(running = running.access(running.access().mult(TInst.of(Tokens.PLUS, group.negate()))), second = second.plus(group.negate()));
        }
    }

    @TestFactory
    Stream<DynamicTest> testGroupPlus() {
        return TEST_ARGS.stream().map(algebra -> DynamicTest.dynamicTest(algebra.toString(), () -> validate(algebra)));
    }

}
