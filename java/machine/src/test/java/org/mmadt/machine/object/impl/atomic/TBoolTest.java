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
import org.mmadt.machine.object.model.type.POr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.machine.object.model.composite.Q.Tag.one;
import static org.mmadt.machine.object.model.composite.Q.Tag.plus;
import static org.mmadt.machine.object.model.composite.Q.Tag.qmark;
import static org.mmadt.machine.object.model.composite.Q.Tag.star;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TBoolTest {

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
        assertEquals(TBool.of(true).q(plus), TBool.of(true).q(plus).or(TBool.of(true).q(qmark)));
        assertEquals("bool|false", TBool.some().or(TBool.of(false)).toString());
        assertEquals("true", TBool.of(true).or(TBool.of(false)).toString());
        assertEquals("true{*}|false", TBool.of(true).q(star).or(TBool.of(false)).toString());
        assertEquals("true{*}~x|false~y", TBool.of(true).q(star).label("x").or(TBool.of(false).label("y")).toString());
        assertEquals("(true{*}~x|false~y)~z", TBool.of(true).q(star).label("x").or(TBool.of(false).label("y")).label("z").toString());
        assertEquals("(true{*}~x|false~y){?}", TBool.of(true).q(star).label("x").or(TBool.of(false).label("y")).q(qmark).toString());
        assertTrue(TBool.of(true).q(star).or(TBool.of(false)).isType());
        assertEquals(TBool.of(true).q(star), ((POr) TBool.of(true).q(star).or(TBool.of(false)).get()).predicates().get(0));
    }
}
