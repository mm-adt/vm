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

package org.mmadt.object.impl.composite;

import org.junit.jupiter.api.Test;
import org.mmadt.object.impl.TObj;
import org.mmadt.object.impl.TStream;
import org.mmadt.object.impl.atomic.TBool;
import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.impl.atomic.TReal;
import org.mmadt.object.impl.atomic.TStr;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.atomic.Str;
import org.mmadt.object.model.composite.Lst;
import org.mmadt.object.model.type.Bindings;
import org.mmadt.object.model.type.POr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.object.model.type.Quantifier.plus;
import static org.mmadt.object.model.type.Quantifier.qmark;
import static org.mmadt.object.model.type.Quantifier.star;
import static org.mmadt.object.model.type.Quantifier.zero;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TLstTest {

    // @Test TODO: This is all messed up because of nested quantifiers
    void testList() {
        final Lst formal = TLst.of(TObj.all().set(TObj.all()));
        final Lst type0 = TLst.of(TObj.some().q(plus).set(TObj.some()));
        final Lst type1 = TLst.of(TObj.all().set(TStream.of(TObj.some().q(qmark), TInt.some().q(plus))));
        final Lst type2 = TLst.of(TObj.some().set(TStream.of(TObj.some().q(qmark), TInt.some().q(plus))));
        final Lst type3 = TLst.of(TObj.some().q(plus).set(TStream.of(TObj.some().q(qmark), TInt.some().q(plus), TInt.of(3).q(star))));
        assertEquals("[(obj{*}){*}]", formal.toString());
        assertEquals("[(obj){+}]", type0.toString());
        assertEquals("[(obj{?},int{+}){*}]", type1.toString());
        assertEquals("[obj{?},int{+}]", type2.toString());
        assertEquals("[(obj{?},int{+},3{*}){+}]", type3.toString());
        assertTrue(formal.test(TLst.of(1)));
        assertTrue(type0.test(TLst.of(1, 2, 3)));
        assertTrue(type0.test(TLst.of(1)));
        assertFalse(type0.test(TLst.of(TInt.of(1).q(zero))));
        assertFalse(type0.test(TLst.of(TInt.of(1).q(star))));
        assertFalse(type1.test(TLst.of(1)));
        // TODO: assertTrue(type1.test(TLst.of(TObj.none(),2)));
        assertTrue(formal.test(TLst.of(1, 2, "marko")));
        assertTrue(formal.test(TLst.of(1, 2, TObj.none())));
        assertTrue(formal.test(TLst.of(1, 2, TObj.none(), TLst.of("hello", "there"))));
        //assertTrue(type2.test(TLst.of(1)));
        assertFalse(type2.test(TLst.of(1)));
        // TODO: assertTrue(formal.test(TLst.none()));

        assertFalse(TLst.of(TInt.gt(32)).constant());
        assertFalse(TInt.gt(32).constant());
        assertTrue(TLst.of(134, 46, 88).constant());
        assertFalse(TLst.of(TInt.gt(32)).test(TLst.of(2)));
        assertTrue(TLst.of(TInt.gt(32)).test(TLst.of(72)));
    }

    @Test
    void testPatterns() {
        assertTrue(TLst.of(TInt.some()).test(TLst.of(1)));
        assertTrue(TLst.of(TInt.some()).q(2).test(TLst.of(TInt.of(1)).q(2)));
        assertFalse(TLst.of(TInt.some()).q(2).test(TLst.of(TInt.of(1)).q(3)));
        assertTrue(TLst.of(TInt.some()).q(1, 4).test(TLst.of(TInt.of(1)).q(3)));
        assertTrue(TLst.of(TInt.some(), 2, "marko").q(1, 4).test(TLst.of(1, 2, "marko").q(3)));
        assertFalse(TLst.of(TInt.some(), 2, "marko").q(1, 4).test(TLst.of(1, 2, "marko").q(6)));
        assertFalse(TLst.of(TInt.some(), 2, TReal.some()).q(1, 4).test(TLst.of(1, 2, "marko").q(6)));
        assertTrue(TLst.of(TInt.some(), TLst.of(1, TInt.some(), 3), TReal.some()).q(1, 4).test(TLst.of(1, TLst.of(1, 2, 3), 0.2).q(4)));
    }

    @Test
    void shouldAndCorrectly() {
        Lst<Str> list1 = TLst.of("my", "name", "is", "marko");
        Lst<Str> list2 = TLst.of("my", "name", "is", TStr.some());
        assertTrue(list1.constant());
        assertFalse(list2.constant());
        assertTrue(list2.and(list1).constant());
        assertTrue(list1.and(list2).constant());
        assertEquals(list2.and(list1), list1.and(list2));
    }

    @Test
    void shouldOrCorrectly() {
        Lst<Str> list1 = TLst.of("my", "name", "is", "marko");
        Lst<Str> list2 = TLst.of("my", "name", "is", TStr.some());
        assertTrue(list1.constant());
        assertFalse(list2.constant());
        // System.out.println(list1.or(list2));
        assertFalse(list1.or(list2).constant());
        assertEquals(list1.or(list2), list2.or(list1));
        assertTrue(list1.or(list2).isType());
        assertTrue(list1.or(list2).get() instanceof POr);

    }

    @Test
    void shouldBindNestedLists() {
        Lst<Obj> list = TLst.of(TStr.some().as("a"), TInt.of(29).as("b"), TBool.of(true), TLst.of(TReal.some(), TReal.some().as("c"), TLst.of(TReal.some().as("d"))).as("e")).as("f");
        Lst<Obj> good = TLst.of("marko", 29, true, TLst.of(66.6f, 11.1f, TLst.of(1.0f)));
        Lst<Obj> bad1 = TLst.of("marko", 30, true, TLst.of(66.6f, 11.1f, TLst.of(1.0f)));
        Lst<Obj> bad2 = TLst.of(7, 29, true, TLst.of(66.6f, 11.1f, TLst.of(1.0f)));
        Lst<Obj> bad3 = TLst.of("marko", 29, true, TLst.of(66.6f, 11.1f, 1.0f));
        // System.out.println(good);
        // System.out.println(list);
        assertTrue(list.test(good));
        assertFalse(list.test(bad1));
        assertFalse(list.test(bad2));
        assertFalse(list.test(bad3));
        //
        final Bindings bindings = new Bindings();
        assertTrue(list.match(bindings, good));
        //System.out.println(bindings + "!!!");
        assertEquals(6, bindings.size());
        assertEquals(TStr.of("marko"), bindings.get("a"));
        assertEquals(TInt.of(29), bindings.get("b"));
        assertEquals(TReal.of(11.1f), bindings.get("c"));
        assertEquals(TReal.of(1.0f), bindings.get("d"));
        assertEquals(TLst.of(66.6f, 11.1f, TLst.of(1.0f)), bindings.get("e"));
        assertEquals(good, bindings.get("f"));
        //
        bindings.clear();
        assertFalse(list.match(bindings, bad1));
        assertEquals(0, bindings.size());
        assertFalse(list.match(bindings, bad2));
        assertEquals(0, bindings.size());
        assertFalse(list.match(bindings, bad3));
        assertEquals(0, bindings.size());
    }

    @Test
    void shouldSupportNoneEndings() {
        final Lst<Obj> a = TLst.of("marko", 29);
        assertFalse(TLst.of(TStr.some(), TObj.none()).test(a));
        assertTrue(TLst.of(TStr.some(), 29, TObj.none()).test(a));
        assertTrue(TLst.of(TStr.some(), 29, TObj.none(), TObj.none()).test(a));
        assertFalse(TLst.of(TStr.some(), TObj.none(), 29, TObj.none()).test(a));
    }
}
