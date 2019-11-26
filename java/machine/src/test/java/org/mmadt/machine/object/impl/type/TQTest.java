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

package org.mmadt.machine.object.impl.type;

import org.junit.jupiter.api.Test;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.composite.TQ;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.composite.Q;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TQTest {

    @Test
    void shouldSupportSingle() {
        final Q<Int> a = new TQ<>(TInt.of(3));
        final Q<Int> b = new TQ<>(TInt.of(3, 3));
        assertEquals(a, b);
        assertEquals(a.<Int>peek(), a.last());
    }

    @Test
    void shouldNegate() {
        assertEquals(TInt.of(-4, -43), new TQ<>(4, 43).neg().object());
        assertEquals(TInt.of(-4, -43), new TQ<>(TInt.of(4, 43)).neg().object());
//        assertEquals(TInt.of(-4, -43), new TQ<>(TInt.of(4, 43).neg()).object());
        // TODO: assertEquals(TReal.of().access(__.start(43.2, 411.34).neg()), new TQ<>(TReal.of(43.2, 411.34)).negate().object());
        // TODO: non-mumeric obj testing
        //  assertEquals(TStr.of("marko","rodriguez"), new Q<>(TStr.of("marko","rodriguez")).negate().object());

    }

    @Test
    void shouldAndCorrectly() {
        TQ q1 = new TQ(1, 1);
        TQ q2 = new TQ(50, 50);
        TQ q3 = new TQ(0, Integer.MAX_VALUE);
        TQ q4 = new TQ(0, 1);

        assertEquals(q1.one(), q1);
        assertTrue(q3.isStar());
        assertEquals(q4.qmark(), q4);
        //
        assertEquals(q2, q1.and(q2));
        //
        assertEquals(q3, q1.and(q3));
        assertEquals(q3, q2.and(q3));
        assertEquals(q3, q3.and(q3));
        //
        assertEquals(q4, q1.and(q4));
        assertEquals(new TQ(0, 50), q2.and(q4));
        assertEquals(q3, q3.and(q4));
        assertEquals(q4, q4.and(q4));
    }

    @Test
    void shouldOrCorrectly() {
        TQ q1 = new TQ(1, 1);
        TQ q2 = new TQ(50, 50);
        TQ q3 = new TQ(0, Integer.MAX_VALUE);
        TQ q4 = new TQ(0, 1);

        assertEquals(new TQ(51, 51), q1.or(q2));
        //
        assertTrue(q1.or(q3).isPlus());
        assertEquals(new TQ(50, Integer.MAX_VALUE), q2.or(q3));
        assertEquals(q3, q3.or(q3));
        //
        assertEquals(new TQ(1, 2), q1.or(q4));
        assertEquals(new TQ(50, 51), q2.or(q4));
        assertTrue(q3.or(q4).isStar());
        assertEquals(new TQ(0, 2), q4.or(q4));
    }

    @Test
    void shouldSupportRealQuantifiers() {
        final Int a = TInt.of(1).q(TReal.of(1.0, 1.0));
        assertEquals(a.q().object(), TReal.of(1.0, 1.0));
        final Int b = TInt.of(3).q(TReal.of(2.0, 3.0));
        assertEquals(b.q().object(), TReal.of(2.0, 3.0));
//        final Int c = a.mult(b);
//        assertEquals(3, c.<Integer>get());
        // TODO: What is the logic?
        //  assertEquals(c.q().object(), TReal.of(2.0, 3.0));
    }

}
