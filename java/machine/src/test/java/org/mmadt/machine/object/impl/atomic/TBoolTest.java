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
import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.util.TestHelper;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.util.IteratorUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.language.__.id;
import static org.mmadt.language.__.start;
import static org.mmadt.machine.object.model.composite.Q.Tag.one;
import static org.mmadt.machine.object.model.composite.Q.Tag.plus;
import static org.mmadt.machine.object.model.composite.Q.Tag.qmark;
import static org.mmadt.machine.object.model.composite.Q.Tag.star;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TBoolTest {

    @Test
    void testInstanceReferenceType() {
        Bool instance = TBool.of(true);
        Bool reference = TBool.of().access(start(true, false, true, false));
        Bool type = TBool.all();
        TestHelper.validateKinds(instance, reference, type);
        //////
        instance = TBool.of(false).q(2);
        reference = TBool.of(true, false, true, false);
        type = TBool.of().q(45);
        TestHelper.validateKinds(instance, reference, type);
        //////
        instance = TBool.of(false).access(start(true).neg());
        reference = TBool.of(true, false).q(2, 10);
        type = TBool.some();
        TestHelper.validateKinds(instance, reference, type);
    }

    @Test
    void shouldStreamCorrectly() {
        assertEquals(TInst.ids(), TBool.of(true).access());
        assertEquals(TBool.some().access(start(true, false, true, false)).q(4), TBool.of(true, false, true, false));
        assertEquals(TLst.of(true, false, true, false).<List<Bool>>get(), IteratorUtils.list(TBool.of(true, false, true, false).iterable().iterator()));
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
        // assertEquals(TBool.of(true).q(star), ((POr) TBool.of(true).q(star).or(TBool.of(false)).get()).predicates().get(0));
    }

    @Test
    void shouldAccessCorrectly() {
        final Bool bool = TBool.of(true, true, false);
        assertEquals(TInst.of(Tokens.START, true, true, false), bool.access());
        assertEquals(TBool.of(true), bool.iterable().iterator().next());
        assertEquals(start(true, true, false).plus(true).bytecode(), bool.plus(TBool.of(true)).access());
        assertEquals(start(true, true, false).mult(true).bytecode(), bool.mult(TBool.of(true)).access());
    }
}
