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
import org.mmadt.machine.object.model.type.algebra.WithRing;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class WithRingTest {

    private static final List<WithRing> TEST_ARGS = List.of(
            TInst.of(Tokens.ID),
            TReal.of(1.0f),
            TInt.of(1),
            TBool.of(true));

    static <A extends WithRing<A>> void validate(final A ring) {
        WithGroupPlusTest.validate(ring);
        //
        final A two = ring.plus(ring.one());
        final A three = ring.plus(ring.one()).plus(ring.one());
        final A four = ring.plus(ring.one()).plus(ring.one()).plus(ring.one());
        assertTrue(ring.isOne());
        assertFalse(ring.isZero());
        assertTrue(ring.one().isOne());
        assertTrue(ring.zero().isZero());
        assertEquals(ring, ring.one());
        assertNotEquals(ring, ring.zero());
        // x = 1 * x
        assertEquals(ring, ring.one().mult(ring));
        assertEquals(two, ring.one().mult(two));
        // x = x * 1
        assertEquals(ring, ring.mult(ring.one()));
        assertEquals(two, two.mult(ring.one()));
        // 0 = 0 * 0
        assertEquals(ring.zero(), ring.zero().mult(ring.zero()));
        // 0 = 0 * x
        assertEquals(ring.zero(), ring.zero().mult(ring));
        assertEquals(ring.zero(), ring.zero().mult(two));
        // 0 = x * 0
        assertEquals(ring.zero(), ring.mult(ring.zero()));
        assertEquals(ring.zero(), two.mult(ring.zero()));
        // x + 0 = x
        assertEquals(two, two.plus(ring.zero()));
        // x = 0 + x
        assertEquals(two, ring.zero().plus(two));
        // 0 = 0 + 0
        assertEquals(ring.zero(), ring.zero().plus(ring.zero()));
        // (a+b)+c = a+(b+c)
        assertEquals(two.plus(ring.one()).plus(three), two.plus(ring.one().plus(three)));
        // a = -(-a)
        assertEquals(ring.one(), ring.one().neg().neg());
        assertEquals(two, two.neg().neg());
        assertEquals(three, three.neg().neg());
        // a - a = 0
        assertEquals(ring.zero(), ring.one().minus(ring.one()));
        // 1 = 3a - 2a
        assertEquals(ring.one(), three.minus(two));
        // a * (b + c) = (a * b) + (a * c)
        assertEquals(two.mult(three.plus(four)), (two.mult(three)).plus(two.mult(four)));
        // (a + b) * c = (a * c) + (b * c)
        assertEquals(two.plus(three).mult(four), (two.mult(four)).plus(three.mult(four)));
        // -a = 0 - a
        assertEquals(ring.one().neg(), ring.zero().minus(ring.one()));
        assertEquals(two.neg(), ring.zero().minus(two));
        assertEquals(three.neg(), ring.zero().minus(three));

    }

    @TestFactory
    Stream<DynamicTest> testRing() {
        return TEST_ARGS.stream().map(algebra -> DynamicTest.dynamicTest(algebra.toString(), () -> validate(algebra)));
    }
}
