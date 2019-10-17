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

package org.mmadt.object.impl.type.feature;

import org.junit.jupiter.api.Test;
import org.mmadt.object.impl.atomic.TBool;
import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.impl.atomic.TReal;
import org.mmadt.object.impl.composite.TInst;
import org.mmadt.object.model.type.feature.WithRing;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class WithRingTest {

    @Test
    void shouldSupportRingAxioms() {
        List.<WithRing>of(TInst.none(), TReal.of(0.0f), TInt.of(0), TBool.of(false)).forEach(ring -> {
            final WithRing two = ring.plus(ring.one()).plus(ring.one());
            final WithRing three = ring.plus(ring.one()).plus(ring.one()).plus(ring.one());
            final WithRing four = ring.plus(ring.one()).plus(ring.one()).plus(ring.one()).plus(ring.one());
            assertTrue(ring.isZero());
            assertTrue(ring.one().isOne());
            assertTrue(ring.zero().isZero());
            // 1 * x = x
            assertEquals(two, ring.one().mult(two));
            // x * 1 = x
            assertEquals(two, two.mult(ring.one()));
            // 0 * 0 = 0
            assertEquals(ring.zero(), ring.zero().mult(ring.zero()));
            // 0 * x = 0
            assertEquals(ring.zero(), ring.zero().mult(two));
            // x * 0 = 0
            assertEquals(ring.zero(), two.mult(ring.zero()));
            // x + 0 = x
            assertEquals(two, two.plus(ring.zero()));
            assertEquals(two, ring.zero().plus(two));
            // 0 + 0 = 0
            assertEquals(ring.zero(), ring.zero().plus(ring.zero()));
            // (a+b)+c = a+(b+c)
            assertEquals(two.plus(ring.one()).plus(three), two.plus(ring.one().plus(three)));
            // a - a = 0
            // TODO: bool breaks assertEquals(ring.zero(), ring.one().minus(ring.one()));
            assertEquals(ring.one(), three.minus(two));
            // a * (b + c) = (a * b) + (a * c)
            assertEquals(two.mult(three.plus(four)), (two.mult(three)).plus(two.mult(four)));
            // (a + b) * c = (a * c) + (b * c)
            assertEquals(two.plus(three).mult(four), (two.mult(four)).plus(three.mult(four)));
            // 1 - a = -a
            assertEquals(two.negate(), ring.zero().minus(two));
            assertEquals(three.negate(), ring.zero().minus(three));
        });
    }
}
