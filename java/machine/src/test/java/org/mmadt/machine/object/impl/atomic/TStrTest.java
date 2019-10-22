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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.language.__.and;
import static org.mmadt.language.__.eq;
import static org.mmadt.language.__.is;
import static org.mmadt.language.__.neq;
import static org.mmadt.language.__.or;
import static org.mmadt.machine.object.model.composite.Q.Tag.star;
import static org.mmadt.machine.object.model.composite.Q.Tag.zero;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TStrTest {

    @Test
    void shouldHaveTypeBasics() {
        assertTrue(TStr.some().test(TStr.of("hello")));
        assertFalse(TInt.some().test(TStr.of("hello")));
        assertTrue(TObj.all().test(TStr.of("hello")));
        assertTrue(TStr.of("hello").q(star).test(TStr.of("hello")));
        assertFalse(TStr.of("hello").q(zero).test(TStr.of("hello")));
        assertTrue(TStr.of(is(eq("id"))).test(TStr.of("id")));
        assertTrue(TStr.of(is(or(eq("id"), eq("label")))).test(TStr.of("id")));
        assertTrue(TStr.of("id").or(TStr.of("label")).test(TStr.of("id")));
        assertFalse(TStr.of(is(and(neq("id"), neq("label")))).test(TStr.of("id")));
        assertTrue(TStr.of(is(and(neq("id"), neq("label")))).test(TStr.of("hello")));
    }

    @Test
    void shouldSupportSemigroupAddition() {
        final Str a = TStr.of("marko");
        final Str b = TStr.of("rodriguez");
        assertEquals(TStr.of("marko rodriguez"), a.plus(TStr.of(" ").plus(b).plus(a.zero())));
    }
}
