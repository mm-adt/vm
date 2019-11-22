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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.machine.object.impl.___.and;
import static org.mmadt.machine.object.impl.___.id;
import static org.mmadt.machine.object.model.composite.Q.Tag.one;
import static org.mmadt.machine.object.model.composite.Q.Tag.plus;
import static org.mmadt.machine.object.model.composite.Q.Tag.qmark;
import static org.mmadt.machine.object.model.composite.Q.Tag.star;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TBoolTest implements TestUtilities {

    @Test
    void testInstanceReferenceType() {
        validateKinds(TBool.of(true), TBool.of(true, false, true, false), TBool.all());
        validateKinds(TBool.of(false).q(2), TBool.of(true, false, true, false), TBool.of().q(1, 45));
        validateKinds(TBool.of(true).neg(), TBool.of(true, false).q(10), TBool.some());
    }

    @Test
    void testType() {
        validateTypes(TBool.some());
    }

    @Test
    void testIsA() {
        validateIsA(TBool.some());
    }


    @Test
    void testAccess() {
        assertEquals(objs(true), submit(TBool.of(true)));
        assertNotEquals(objs(false), submit(TBool.of(true)));
        assertNotEquals(objs(true, false), submit(TBool.of(true, false).branch(id(), id())));
        assertEquals(objs(true, true, false, false), submit(TBool.of(true, true, false, false)));
        assertEquals(objs(true, true, false, false), submit(TBool.of(true, false).branch(id(), id())));
        assertEquals(objs(), submit(TBool.of(true, false).branch(id(), id()).is(and(false)).id().id()));
    }

    @Test
    void shouldAndCorrectly() {
        assertEquals("true{*}", TBool.of(true).q(star).toString());
        assertEquals(TBool.of(true), TBool.of(true).and(TBool.of(true)));
        assertEquals(TBool.of(true).q(one), TBool.of(true).and(TBool.of(true)));
        assertEquals(TBool.of(true).q(star), TBool.of(true).q(plus).and(TBool.of(true).q(qmark)));
        assertEquals("true{*}", TBool.of(true).q(star).and(TBool.some()).toString());
        assertEquals("false{*}~x", TBool.of(false).q(qmark).label("x").and(TBool.some().q(plus)).toString());
        assertEquals("false{*}~x", TBool.of(false).q(qmark).label("x").and(TBool.some().label("x").q(plus)).toString());
        assertEquals(TBool.of(false), TBool.of(true).and(TBool.of(false)));
        assertThrows(RuntimeException.class, () -> TBool.of(false).q(qmark).label("x").and(TBool.some().q(plus).label("y")));
        assertEquals("false", TBool.some().and(TBool.of(false)).toString());
    }

    @Test
    void shouldOrCorrectly() {
        assertEquals(TBool.of(true), TBool.of(true).or(TBool.of(true)));
        assertEquals("true{+}|true{?}", TBool.of(true).q(plus).or(TBool.of(true).q(qmark)).toString());
        assertEquals("bool|false", TBool.some().or(TBool.of(false)).toString());
        assertEquals("true", TBool.of(true).or(TBool.of(false)).toString());
        assertEquals("true{*}|false", TBool.of(true).q(star).or(TBool.of(false)).toString());
        assertEquals("true{*}~x|false~y", TBool.of(true).q(star).label("x").or(TBool.of(false).label("y")).toString());
        assertEquals("(true{*}~x|false~y)~z", TBool.of(true).q(star).label("x").or(TBool.of(false).label("y")).label("z").toString());
        assertEquals("(true{*}~x|false~y){?}", TBool.of(true).q(star).label("x").or(TBool.of(false).label("y")).q(qmark).toString());
        assertTrue(TBool.of(true).q(star).or(TBool.of(false)).isType());
    }
}
