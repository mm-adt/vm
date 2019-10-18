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

package org.mmadt.object.impl.type;

import org.junit.jupiter.api.Test;
import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.impl.atomic.TReal;
import org.mmadt.object.model.atomic.Int;
import org.mmadt.object.model.type.Quantifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class QuantifierTest {

    @Test
    void shouldNegate() {
        assertEquals(TInt.of(-4, -43), new Quantifier<>(4, 43).negate().object());
        assertEquals(TInt.of(-4, -43), new Quantifier<>(TInt.of(4, 43)).negate().object());
        assertEquals(TInt.of(-4, -43), new Quantifier<>(TInt.of(4, 43).negate()).object());
        assertEquals(TReal.of(-43.2, -411.34), new Quantifier<>(TReal.of(43.2, 411.34)).negate().object());
        // TODO: non-mumeric obj testing
        //  assertEquals(TStr.of("marko","rodriguez"), new Quantifier<>(TStr.of("marko","rodriguez")).negate().object());

    }

    @Test
    void shouldAndCorrectly() {
        Quantifier q1 = new Quantifier(1, 1);
        Quantifier q2 = new Quantifier(50, 50);
        Quantifier q3 = new Quantifier(0, Integer.MAX_VALUE);
        Quantifier q4 = new Quantifier(0, 1);

        assertEquals(Quantifier.one, q1);
        assertEquals(Quantifier.star, q3);
        assertEquals(Quantifier.qmark, q4);
        //
        assertEquals(q2, q1.and(q2));
        //
        assertEquals(q3, q1.and(q3));
        assertEquals(q3, q2.and(q3));
        assertEquals(q3, q3.and(q3));
        //
        assertEquals(q4, q1.and(q4));
        assertEquals(new Quantifier(0, 50), q2.and(q4));
        assertEquals(q3, q3.and(q4));
        assertEquals(q4, q4.and(q4));
    }

    @Test
    void shouldOrCorrectly() {
        Quantifier q1 = new Quantifier(1, 1);
        Quantifier q2 = new Quantifier(50, 50);
        Quantifier q3 = new Quantifier(0, Integer.MAX_VALUE);
        Quantifier q4 = new Quantifier(0, 1);

        assertEquals(new Quantifier(51, 51), q1.or(q2));
        //
        assertEquals(Quantifier.plus, q1.or(q3));
        assertEquals(new Quantifier(50, Integer.MAX_VALUE), q2.or(q3));
        assertEquals(q3, q3.or(q3));
        //
        assertEquals(new Quantifier(1, 2), q1.or(q4));
        assertEquals(new Quantifier(50, 51), q2.or(q4));
        assertEquals(Quantifier.star, q3.or(q4));
        assertEquals(new Quantifier(0, 2), q4.or(q4));
    }

    @Test
    void shouldSupportRealQuantifiers() {
        final Int a = TInt.of(1).q(TReal.of(1.0,1.0));
        assertEquals(a.q().object(), TReal.of(1.0, 1.0));
    }

}
