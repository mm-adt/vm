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
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.type.PRel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.machine.object.model.composite.Q.Tag.star;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TStrTest {

    @Test
    void shouldHaveBasicSemantics() {
        assertTrue(TStr.some().test(TStr.of("hello")));
        assertFalse(TInt.some().test(TStr.of("hello")));
        assertTrue(TObj.all().test(TStr.of("hello")));
        // System.out.println(TObj.all().eq("hello"));
        assertTrue(TStr.of("hello").q(star).test(TStr.of("hello")));
        assertTrue(TStr.some().set(new PRel(PRel.Rel.EQ, TStr.of("id"))).test(TStr.of("id")));
        assertTrue(TStr.some().set(new PRel(PRel.Rel.EQ, TStr.of("id").or(TStr.of("label")))).test(TStr.of("id")));
        assertTrue(TStr.some().set(TStr.of("id").or(TStr.of("label"))).test(TStr.of("id")));
        assertFalse(TStr.some().set(new PRel(PRel.Rel.NEQ, TStr.of("id").or(TStr.of("label")))).test(TStr.of("id")));
        assertTrue(TStr.some().set(new PRel(PRel.Rel.NEQ, TStr.of("id").or(TStr.of("label")))).test(TStr.of("hello")));
    }

    @Test
    void shouldSupportSemigroupAddition() {
        final Str a = TStr.of("marko");
        final Str b = TStr.of("rodriguez");
        assertEquals(TStr.of("marko rodriguez"), a.plus(TStr.of(" ").plus(b).plus(a.zero())));
    }
}
