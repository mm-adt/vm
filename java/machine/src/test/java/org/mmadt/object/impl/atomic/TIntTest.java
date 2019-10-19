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

package org.mmadt.object.impl.atomic;

import org.junit.jupiter.api.Test;
import org.mmadt.object.impl.TObj;
import org.mmadt.object.model.atomic.Int;
import org.mmadt.object.model.type.Bindings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.object.model.composite.Q.Tag.zero;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TIntTest {

    @Test
    void shouldSupportStreams() {
        final Int a = TInt.of(1);
        assertEquals(TInt.of(1, 1), a.q().object());
        a.push(TInt.of(2));
        assertEquals(TInt.of(2, 2), a.q().object());
        a.push(TInt.of(3));
        assertEquals(TInt.of(3, 3), a.q().object());
        ///
        assertEquals(TInt.of(3), a.peak());
        assertEquals(TInt.of(3), a.pop());
        assertEquals(TInt.of(2), a.pop());
        assertEquals(TInt.of(1), a.peak());
        assertEquals(TInt.of(1), a.peak());
        assertEquals(TInt.of(1), a.pop());
        assertEquals(TInt.none(), a.pop());
        assertEquals(TInt.none(), a.pop());
    }

    @Test
    void shouldHaveBasicSemantics() {
        assertTrue(TInt.some().test(TInt.of(32)));
        assertFalse(TInt.some().test(TReal.of(43.0f)));
        assertTrue(TObj.all().test(TInt.of(-1)));
        assertNotEquals(TInt.some(), TBool.some());
        assertNotEquals(TInt.some(), TStr.some());
    }

    @Test
    void shouldSupportMathOperators() {
        assertEquals(TInt.of(3), TInt.of(1).plus(TInt.of(2)));
        assertEquals(TInt.of(2), TInt.of(1).mult(TInt.of(2)));
        assertEquals(TInt.of(6), TInt.of(1).plus(TInt.of(2)).plus(TInt.of(3)));
        assertEquals(TInt.of(9), TInt.of(1).plus(TInt.of(2)).mult(TInt.of(3)));
        // System.out.println(TInt.of(9).or(TInt.of(10)).and(TInt.of(10).q(-1)));
        // TODO: BAD System.out.println(TInt.of(9).add(TInt.some()));
        assertEquals(TInt.of(9), TInt.of(9).and(TInt.some()));
        assertEquals(TInt.of(9), TInt.some().and(TInt.of(9)));
    }

    // TODO @Test
    void shouldSupportStreamTesting() {
        // TODO: nested quantifiers is not exactly correct
        assertTrue(TInt.of(1).q(3).test(TInt.of(1, 1, 1)));
        System.out.println(TInt.of(TInt.of(TInt.of(1)).q(2)).q(3).toString());
        System.out.println(TInt.of(TInt.of(TInt.of(1).q(5)).q(4)).q(3).toString());
        assertTrue(TInt.of(TInt.of(1)).q(3).test(TInt.of(1, 2, 3)));
        //assertTrue(TInt.of(TInt.of(1,2)).q(3).test(TInt.of(1, 2, 3)));
        assertTrue(TInt.of(TInt.of(1, 2, 3)).q(3).test(TInt.of(1, 2, 3).q(3)));
        assertFalse(TInt.of(TInt.of(1)).q(3).test(TInt.of(1, 1)));
        //
        assertTrue(TInt.of(TInt.of(TInt.some(), 1, 1)).q(3).test(TInt.of(1, 1, 1).q(3)));
        assertTrue(TInt.some().q(3).test(TInt.of(1).q(3)));
        assertTrue(TInt.of(TInt.some()).q(3).test(TInt.of(1, 2, 3)));
        assertFalse(TInt.of(TInt.some().q(2)).test(TInt.of(1, 2, 3)));
        assertFalse(TInt.of(TInt.some().q(3)).test(TInt.of(1, 2, 3)));
        assertFalse(TInt.of(TInt.some()).q(2).test(TInt.of(1, 2, 3)));
        assertTrue(TInt.of(TInt.some()).q(3).test(TInt.of(1, 2, 3)));
        assertTrue(TInt.of(TInt.some(), 2, TInt.some(), TInt.none(), TInt.none(), TInt.none()).test(TInt.of(1, 2, 3)));
        assertFalse(TInt.of(TInt.some(), 2, TInt.some(), TInt.none(), TInt.none(), TInt.none()).test(TInt.of(1, 2, 3, TInt.none(), 5)));
        assertTrue(TInt.of(1, 2).test(TInt.of(1, 2)));
        assertFalse(TInt.of(TInt.some()).q(2).test(TInt.of(1, 2, 3).q(3)));
        assertTrue(TInt.of(TInt.some()).q(1, 6).test(TInt.of(1, 2, 3).q(2)));
        assertTrue(TInt.of(TInt.some().q(1, 3), TInt.some(), TInt.none(), TInt.none()).q(1, 3).test(TInt.of(TInt.of(1).q(2), 2, TInt.of(3).q(zero)).q(2)));
        assertTrue(TInt.some().q(1, 5).test(TInt.of(1, 2, 3)));
        assertTrue(TInt.of(TInt.some()).q(1, 5).test(TInt.of(1, 2, 3)));
        assertTrue(TInt.of(TInt.all(), TInt.all(), TInt.all()).test(TInt.of(TInt.none(), 2, TInt.none())));
        assertFalse(TInt.of(TInt.all(), TInt.all(), 3).test(TInt.of(TInt.none(), 2, TInt.none())));
        assertTrue(TInt.of(TInt.all(), TInt.all(), TInt.all()).test(TInt.of(TInt.none(), 2, TInt.none(), TInt.all())));
    }

    @Test
    void shouldSupportStreamMatching() {
        final Bindings bindings = new Bindings();
        assertTrue(TInt.of(TInt.of(TInt.some().as("a"), 1, 1)).q(3).match(bindings, TInt.of(1, 1, 1).q(3)));
        assertEquals(1, bindings.size());
        assertEquals(TInt.of(1), bindings.get("a"));
        //
        bindings.clear();
        assertTrue(TInt.of(TInt.some().as("a"), 2, TInt.some().as("b"), TInt.none(), TInt.none(), TInt.none()).match(bindings, TInt.of(1, 2, 3)));
        assertEquals(2, bindings.size());
        assertEquals(TInt.of(1), bindings.get("a"));
        assertEquals(TInt.of(3), bindings.get("b"));
        //
        bindings.clear();
        assertTrue(TInt.of(TInt.all().as("a"), TInt.all().as("b"), TInt.all().as("c")).match(bindings, TInt.of(TInt.none(), 2, TInt.all(), TInt.all())));
        assertEquals(2, bindings.size());
        // TODO: Ask @dkuppitz if None can be bound -- assertEquals(TInt.none(), bindings.get("a"));
        assertEquals(TInt.of(2), bindings.get("b"));
        assertEquals(TInt.all(), bindings.get("c"));
    }

}
