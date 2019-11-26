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

package org.mmadt.machine.object.impl.type.algebra;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.model.type.algebra.WithOrder;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class WithOrderTest {
    private static final List<List<WithOrder>> TEST_ARGS = List.of(
            List.of(TStr.of("a"), TStr.of("ab"), TStr.of("zZzzZ")),
            List.of(TInt.of(2), TInt.of(4545), TInt.of(467788)),
            List.of(TReal.of(65.0f), TReal.of(787.235f), TReal.of(103455677.13f)));

    static <A extends WithOrder<A>> void validate(final List<A> orders) {
        final A min = orders.get(0).min();
        final A max = orders.get(0).max();
        final A one = orders.get(0);
        final A two = orders.get(1);
        final A three = orders.get(2);
        ////////////////////////////////////////
        assertTrue(min.isMin());
        assertTrue(max.isMax());
        assertFalse(min.isMax());
        assertFalse(max.isMin());
        assertFalse(min.eq(max).java());
        assertTrue(min.neq(max).java());
        ////////////////////////////////////////
        assertFalse(one.isMin());
        assertFalse(two.isMin());
        assertFalse(three.isMin());
        assertFalse(one.isMax());
        assertFalse(two.isMax());
        assertFalse(three.isMax());
        ////////////////////////////////////////
        // true = min < x1
        assertEquals(TBool.of(true), min.lt(one));
        // true = min <= x1
        assertEquals(TBool.of(true), min.lte(one));
        // false = min >= x1
        assertEquals(TBool.of(false), min.gte(one));
        // false = min > x1
        assertEquals(TBool.of(false), min.gt(one));
        ////////////////////////////////////////
        // true = x2 < max
        assertEquals(TBool.of(true), two.lt(max));
        // true = x2 <= max
        assertEquals(TBool.of(true), two.lte(max));
        // false = x2 >= max
        assertEquals(TBool.of(false), two.gte(max));
        // false = x2 > max
        assertEquals(TBool.of(false), two.gt(max));
        ////////////////////////////////////////
        assertEquals(TBool.of(true), one.lt(two));
        assertEquals(TBool.of(true), two.lt(three));
        assertEquals(TBool.of(true), one.lt(three));
        ////////////////////////////////////////
        assertEquals(TBool.of(true), three.gt(two));
        assertEquals(TBool.of(true), two.gt(one));
        assertEquals(TBool.of(true), three.gt(one));
        ////////////////////////////////////////
        assertEquals(TBool.of(false), three.lte(two));
        assertEquals(TBool.of(false), two.lte(one));
        assertEquals(TBool.of(false), three.lte(one));
        ////////////////////////////////////////
        assertEquals(TBool.of(false), one.gte(two));
        assertEquals(TBool.of(false), two.gte(three));
        assertEquals(TBool.of(false), one.gte(three));
    }

    @TestFactory
    Stream<DynamicTest> testOrder() {
        return TEST_ARGS.stream().map(algebra -> DynamicTest.dynamicTest(algebra.toString(), () -> validate(algebra)));
    }
}
