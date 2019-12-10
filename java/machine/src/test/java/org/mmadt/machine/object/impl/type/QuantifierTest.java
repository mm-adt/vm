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
import org.mmadt.machine.object.impl.ext.composite.TPair;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.ext.composite.Pair;
import org.mmadt.machine.object.model.util.QuantifierHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.qmark;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class QuantifierTest {

    @Test
    void shouldAndCorrectly() {
        Pair q1 = TPair.of(1, 1);
        Pair q2 = TPair.of(50, 50);
        Pair q3 = TPair.of(0, Integer.MAX_VALUE);
        Pair q4 = TPair.of(0, 1);

        assertEquals(q1.one(), q1);
        assertTrue(QuantifierHelper.isStar(q3));
        assertEquals(q4.q(qmark), q4);
    }

    @Test
    void shouldOrCorrectly() {
        Pair q1 = TPair.of(1, 1);
        Pair q2 = TPair.of(50, 50);
        Pair q3 = TPair.of(0, Integer.MAX_VALUE);
        Pair q4 = TPair.of(0, 1);
    }

    @Test
    void shouldSupportRealQuantifiers() {
        final Int a = TInt.of(1).q(1.0);
        assertEquals(a.q(), TPair.of(1.0, 1.0));
        final Int b = TInt.of(3).q(2.0, 3.0);
        assertEquals(b.q(), TPair.of(2.0, 3.0));
    }

}
