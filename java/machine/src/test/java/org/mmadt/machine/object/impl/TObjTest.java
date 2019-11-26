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

package org.mmadt.machine.object.impl;

import org.junit.jupiter.api.Test;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.type.Bindings;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.language.__.and;
import static org.mmadt.language.__.gt;
import static org.mmadt.language.__.is;
import static org.mmadt.language.__.lt;
import static org.mmadt.machine.object.model.composite.Q.Tag.qmark;
import static org.mmadt.machine.object.model.composite.Q.Tag.star;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TObjTest {

    @Test
    void shouldOrCorrectly() {
        //      final Obj unionType = TInt.of().or(TBool.of()).or(TStr.of()).or(TReal.of());
//        assertEquals(4, (((POr) unionType.get()).predicates().size()));


        System.out.println(TInt.of().or(TBool.of()).or(TReal.of()).or(TStr.of()).test(TLst.of("marko")));
    }

    @Test
    void testAtomicAndOr() {
        /*Str type1 = TStr.of();
        Obj type2 = TStr.of().gt("marko");
        assertFalse(type1.constant());
        assertFalse(type2.constant());
        assertEquals(type2, type1.and(type2));
        assertEquals(type2, type2.and(type1));
        assertEquals(type2, type2.and(type2));
        assertEquals(type1, type1.or(type1));*/
        //
        Obj type3 = TInt.of(is(gt(32)));
        Int type4 = TInt.of();
        Obj type5 = TInt.of(is(gt(32))).q(star);
        assertFalse(type3.constant());
        assertFalse(type4.constant());
        assertFalse(type5.constant());
        //
        Obj type6 = TInt.of(is(gt(32))).q(star);
        assertEquals(type5.q(star), type4.and(type6));
        assertEquals(type6.q(star), type4.and(type6));
        assertFalse(type6.constant());
        //
        Obj type7 = gt(1).bytecode();
        Obj type8 = lt(2).bytecode();
        Obj type9 = is(type7.and(type8)).bytecode();
        Obj type10 = is(type7.or(type8)).bytecode();

        //System.out.println(TObj.of().gt(1).and(TObj.of().lt(2)));
        assertEquals(is(and(gt(1), lt(2))).bytecode(), type9);
        // TODO: NEED OrMap:: assertEquals(is(or(gt(1),lt(2))), type10);
        assertEquals(TInt.of(is(gt(32))).label("x"), TInt.of().and(TInt.of(is(gt(32))).label("x")));
        assertThrows(RuntimeException.class, () -> TInt.of().label("x").and(TInt.of().label("y")));
        //assertThrows(RuntimeException.class, () -> TInt.of().as("x").and(TStr.of().as("x")));
    }


    @Test
    void shouldHaveSymbols() {
        assertEquals("bool", TBool.of().symbol());
        assertEquals("int", TInt.of().symbol());
        assertEquals("real", TReal.of().symbol());
        assertEquals("str", TStr.of().symbol());
        assertEquals("inst", TInst.some().symbol());
        assertEquals("list", TLst.some().symbol());
        assertEquals("rec", TRec.some().symbol());
        //
        assertEquals("lucky", TBool.of(true).symbol("lucky").symbol());
        assertEquals("person", TRec.of("name", TStr.of(), "age", TInt.of()).symbol("person").symbol());
    }

    @Test
    void shouldToString() {
        assertEquals("bool", TBool.of().toString());
        assertEquals("true", TBool.of(true).toString());
        assertEquals("int~x", TInt.of().label("x").toString());
        assertEquals("3", TInt.of(3).toString());
        assertEquals("real", TReal.of().toString());
        assertEquals("6.6", TReal.of(6.6f).toString());
        assertEquals("str", TStr.of().toString());
        assertEquals("'hello'", TStr.of("hello").toString());
        assertEquals("[get,'inE'][get,'outV']", TInst.of("get", "inE").mult(TInst.of("get", "outV")).toString());
        assertEquals("inst", TInst.some().toString());
        assertEquals("[is,[get,'name'][eq,'marko']]", TInst.of("is", TInst.of("get", "name").mult(TInst.of("eq", "marko"))).toString());
        assertEquals("list", TLst.some().toString());
        assertEquals("['get';4;true;3.2]", TLst.of("get", 4, true, 3.2).toString());
        // TODO: assertEquals("(gt(1)|lt(20))~x", TInt.of(is(or(gt(1),lt(20)))).as("x").toString());
        // TODO: assertEquals("(gt(1)&lt(20))~x", TInt.of(is(and(gt(1),lt(20)))).as("x").toString());
        assertEquals("rec", TRec.some().toString());
        assertEquals("['name':str,'age':int]", TRec.of("name", TStr.of(), "age", TInt.of()).toString());
        assertEquals("['name':str,'age':int]", TRec.of("name", TStr.of(), "age", TInt.of()).symbol("person").toString()); // TODO: @person prefix?
        assertEquals("['name':str~x,'age':int~y]~z", TRec.of("name", TStr.of().label("x"), "age", TInt.of().label("y")).label("z").symbol("person").toString());
    }

    @Test
    void shouldTestAny() {
        assertTrue(TBool.of().test(TBool.of(true)));
        assertTrue(TBool.of().test(TBool.of(false)));
        assertTrue(TInt.of().test(TInt.of(6)));
        assertTrue(TReal.of().test(TReal.of(6.3f)));
        assertTrue(TStr.of().test(TStr.of("hello")));
        assertTrue(TLst.some().test(TLst.of("get", 4, true, 3.2)));
        assertTrue(TRec.some().test(TRec.of("name", TStr.of(), "age", TInt.of())));
    }

    @Test
    void shouldMatchAny() {
        final Bindings bindings = new Bindings();
        TBool.of(true).label("a").match(bindings, TBool.of(true));
        assertEquals(1, bindings.size());
        assertEquals(TBool.of(true), bindings.get("a"));
        ///
        bindings.clear();
        TInt.of().label("a").match(bindings, TInt.of(6));
        assertEquals(1, bindings.size());
        assertEquals(TInt.of(6), bindings.get("a"));
        //
        bindings.clear();
        TReal.of(6.6f).label("a").match(bindings, TReal.of(6.6f));
        assertEquals(1, bindings.size());
        assertEquals(TReal.of(6.6f), bindings.get("a"));
        ///
        bindings.clear();
        TStr.of().label("a").match(bindings, TStr.of("marko"));
        assertEquals(1, bindings.size());
        assertEquals(TStr.of("marko"), bindings.get("a"));
        //
        bindings.clear();
        assertTrue(TInst.some().test(TInst.of("get", "outV")));
        TInst.some().label("a").match(bindings, TInst.of("get", "outV"));
        assertEquals(1, bindings.size());
        assertEquals(TInst.of("get", "outV"), bindings.get("a"));
        //
        bindings.clear();
        TInst.of("get", "outE").label("a").match(bindings, TInst.of("get", "outE"));
        assertEquals(1, bindings.size());
        assertEquals(TInst.of("get", "outE"), bindings.get("a"));
        //
        bindings.clear();
        TLst.of("get", TInt.of().label("a"), true, 3.2).label("b").match(bindings, TLst.of("get", 4, true, 3.2));
        assertEquals(2, bindings.size());
        assertEquals(TInt.of(4), bindings.get("a"));
        assertEquals(TLst.of("get", 4, true, 3.2), bindings.get("b"));
        //
        bindings.clear();
        TRec.of("name", TStr.of().label("a"), "age", TInt.of().label("b")).label("c").match(bindings, TRec.of("name", "marko", "age", 29));
        assertEquals(3, bindings.size());
        assertEquals(TStr.of("marko"), bindings.get("a"));
        assertEquals(TInt.of(29), bindings.get("b"));
        assertEquals(TRec.of("name", "marko", "age", 29), bindings.get("c"));
    }

    @Test
    void shouldTypeInstance() {
        assertTrue(TStr.of().isType());
        assertTrue(TStr.of().q(qmark).isType());
        assertTrue(TStr.of().set("marko").isInstance());
        assertTrue(TStr.of().symbol("str").label("a").isType());
    }

    @Test
    void testNoneSomeAllEqualities() {
        assertEquals(TObj.none(), TInt.none());
        assertTrue(TObj.all().test(TObj.none()));
        assertFalse(TObj.none().test(TObj.all()));
        assertNotEquals(TObj.none(), 23);
        assertNotEquals(TObj.none(), TInt.of(23));
        assertEquals(TStr.none(), TObj.none());
        assertEquals(TLst.none(), TBool.none());
        assertEquals(TReal.none(), TRec.none());

        final List<Obj> nones = List.of(TObj.none(), TBool.none(), TInt.none(), TReal.none(), TStr.none(), TLst.none(), TRec.none(), TInst.none());
        final List<Obj> somes = List.of(TObj.some(), TBool.of(), TInt.of(), TReal.of(), TStr.of(), TLst.some(), TRec.some(), TInst.some());
        final List<Obj> alls = List.of(TObj.all(), TBool.all(), TInt.all(), TReal.all(), TStr.all(), TLst.all(), TRec.all(), TInst.all());

        nones.forEach(a -> nones.forEach(b -> {
            assertEquals(a, b);
            assertEquals(a.q(), b.q());
        }));
        somes.forEach(a -> somes.stream().filter(b -> a != b).forEach(b -> {
            assertNotEquals(a, b);
            assertEquals(a.q(), b.q());
        }));
        alls.forEach(a -> alls.stream().filter(b -> a != b).forEach(b -> {
            assertNotEquals(a, b);
            assertEquals(a.q(), b.q());
        }));
        //
        nones.forEach(a -> somes.forEach(b -> {
            assertNotEquals(a, b);
            assertNotEquals(a.q(), b.q());
        }));
        nones.forEach(a -> alls.forEach(b -> {
            assertNotEquals(a, b);
            assertNotEquals(a.q(), b.q());
        }));
        somes.forEach(a -> alls.forEach(b -> {
            assertNotEquals(a, b);
            assertNotEquals(a.q(), b.q());
        }));
    }
}
