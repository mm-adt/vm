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

package org.mmadt.machine.object.impl.ext.algebra;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.model.ext.algebra.WithField;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class WithFieldTest {

    private static final List<WithField> TEST_ARGS = List.of(TReal.of(1.0f));

    private static <A extends WithField<A>> void validate(final A field) {
        WithRingTest.validate(field);
        //
        final A two = field.plus(field.one());
        final A three = field.plus(field.one()).plus(field.one());
        final A four = field.plus(field.one()).plus(field.one()).plus(field.one());
        assertTrue(field.isOne());
        assertFalse(field.isZero());
        assertTrue(field.one().isOne());
        assertTrue(field.zero().isZero());
        assertEquals(field, field.one());
        assertNotEquals(field, field.zero());
        // 1 = a / a
        assertEquals(field.one(), field.div(field));
        assertEquals(field.one(), two.div(two));
        assertEquals(field.one(), three.div(three));
        // a = a / 1
        assertEquals(field, field.div(field.one()));
        assertEquals(two, two.div(field.one()));
        assertEquals(three, three.div(field.one()));
        // 1 = a * a^-1
        assertEquals(field.one(), field.mult(field.inv()));
        // a^-1 = 1 / a
        assertEquals(field.inv(), field.one().div(field));
        assertEquals(two.inv(), two.one().div(two));
        assertEquals(three.inv(), three.one().div(three));
        // a = a^2 / a
        assertEquals(field, field.mult(field).div(field));
        assertEquals(two, two.mult(two).div(two));
        assertEquals(three, three.mult(three).div(three));
        // 2a = 4a / 2a
        assertEquals(two, four.div(two));
        // 0 = 0 / a
        assertEquals(field.zero(), field.zero().div(field));
        // error = a / 0
        // System.out.println(field.div(field.zero())); // TODO: returns Infinity (decide mm-ADT semantics and then finish test)
        // assertThrows(Exception.class, () -> field.div(field.zero()));
    }

    @TestFactory
    Stream<DynamicTest> testField() {
        return TEST_ARGS.stream().map(algebra -> DynamicTest.dynamicTest(algebra.toString(), () -> validate(algebra)));
    }
}
