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
import org.mmadt.machine.object.impl.util.TestHelper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mmadt.machine.object.impl.___.plus;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TRealTest implements TestUtilities {

    @Test
    void testInstanceReferenceType() {
        validateKinds(TReal.of(23.4f), TReal.of(1.46f, 13.02f).plus(TReal.of(2.0f)).div(TReal.of(1.4f)), TReal.some());
        TestHelper.validateKinds(TReal.of(41.3f).q(2), TReal.of(23.0f, 56.0f, 11.0f), TReal.of().q(1, 45));
    }

    @Test
    void testType() {
        validateTypes(TReal.some());
    }

    @Test
    void testIsA() {
        validateIsA(TReal.some());
    }

    @Test
    void testAccess() {
        assertEquals(objs(1.0f, 2.0f, 3.0f, 4.0f), submit(TReal.of(1.0f, 2.0f, 3.0f, 4.0f)));
        assertEquals(objs(1.1f), submit(TReal.of(1.1f)));
        assertEquals(List.of(TReal.of(1.1f).q(2)), submit(TReal.of(1.1f).mult(1.0f).q(2)));
        assertNotEquals(objs(1.1f), submit(TReal.of(2.3f)));
        assertNotEquals(objs(1), submit(TReal.of(1.0f)));
        assertEquals(objs(4.2f), submit(TReal.of(1.0f).plus(plus(plus(1.2f)))));
        assertEquals(objs(4.2f), submit(TReal.of(1.0f).plus(plus(plus(1.2f))).mult(1.0f)));
        assertEquals(objs(false), submit(TReal.of(1.0f).plus(1.2f).gt(plus(0.1f))));
    }
}
