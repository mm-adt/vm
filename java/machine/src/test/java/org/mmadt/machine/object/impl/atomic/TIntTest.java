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

package org.mmadt.machine.object.impl.atomic;

import org.junit.jupiter.api.Test;
import org.mmadt.TestUtilities;
import org.mmadt.machine.object.model.atomic.Int;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.machine.object.impl.___.gt;
import static org.mmadt.machine.object.impl.___.gte;
import static org.mmadt.machine.object.impl.___.lt;
import static org.mmadt.machine.object.impl.___.lte;
import static org.mmadt.machine.object.impl.___.plus;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TIntTest implements TestUtilities {

    @Test
    void testInstanceReferenceType() {
        Int instance = TInt.of(23);
        Int reference = TInt.of(1, 2).plus(TInt.of(2)).minus(TInt.of(7));
        Int type = TInt.some();
        validateKinds(instance, reference, type);
        //////
        instance = TInt.of(4).q(2);
        reference = TInt.of(23, 56, 11);
        type = TInt.of().q(45);
        validateKinds(instance, reference, type);
    }

    @Test
    void testType() {
        validateTypes(TInt.some());
    }

    @Test
    void testIsA() {
        assertTrue(TInt.some().test(TInt.of(32)));
        assertFalse(TInt.some().test(TReal.of(43.0f)));
    }

    @Test
    void testAccess() {
        assertEquals(objs(1), submit(TInt.of(1)));
        assertNotEquals(objs(1.0), submit(TInt.of(1)));
        assertEquals(objs(4), submit(TInt.of(1).plus(plus(plus(1)))));
        assertEquals(objs(50), submit(TInt.of(1).plus(4).mult(10)));
        assertEquals(objs(50), submit(TInt.of(1).plus(4).mult(10).is(gt(plus(-50)))));
        assertEquals(objs(true), submit(TInt.of(1).plus(4).mult(10).gt(plus(-50))));
        assertEquals(objs(true), submit(TInt.of(1).plus(4).mult(10).gt(plus(plus(-60)))));
        assertEquals(objs(49, 50), submit(TInt.of(49, 50).is(gt(plus(-1)))));
        assertEquals(objs(49, 50), submit(TInt.of(49, 50).is(gte(plus(-1)))));
        assertEquals(objs(), submit(TInt.of(49, 50).is(lt(plus(-1)))));
        assertEquals(objs(49, 50), submit(TInt.of(49, 50).is(lt(plus(1)))));
        assertEquals(objs(49, 50), submit(TInt.of(49, 50).is(lte(plus(1)))));
        assertEquals(objs(), submit(TInt.of(49, 50).is(gt(plus(1)))));
    }
}
