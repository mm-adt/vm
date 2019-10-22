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
import org.mmadt.machine.object.model.type.POr;

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
        final Obj unionType = TInt.some().or(TBool.some()).or(TStr.some()).or(TReal.some());
        assertEquals(4, (((POr) unionType.get()).predicates().size()));
    }

    @Test
    void testAtomicAndOr() {
        /*Str type1 = TStr.some();
        Obj type2 = TStr.some().gt("marko");
        assertFalse(type1.constant());
        assertFalse(type2.constant());
        assertEquals(type2, type1.and(type2));
        assertEquals(type2, type2.and(type1));
        assertEquals(type2, type2.and(type2));
        assertEquals(type1, type1.or(type1));*/
        //
        Obj type3 = TInt.gt(32);
        Int type4 = TInt.some();
        Obj type5 = TInt.gt(32).q(star);
        assertFalse(type3.constant());
        assertFalse(type4.constant());
        assertFalse(type5.constant());
        //
        Obj type6 = TInt.gt(32).q(star);
        assertEquals(type5.q(star), type4.and(type6));
        assertEquals(type6.q(star), type4.and(type6));
        assertFalse(type6.constant());
        //
        Obj type7 = gt(1).bytecode();
        Obj type8 = lt(2).bytecode();
        Obj type9 = is(type7.and(type8)).bytecode();
        Obj type10 = is(type7.or(type8)).bytecode();

        //System.out.println(TObj.some().gt(1).and(TObj.some().lt(2)));
        assertEquals(is(and(gt(1), lt(2))).bytecode(), type9);
        // TODO: NEED OrMap:: assertEquals(is(or(gt(1),lt(2))), type10);
        assertEquals(TInt.gt(32).as("x"), TInt.some().and(TInt.gt(32).as("x")));
        assertThrows(RuntimeException.class, () -> TInt.some().as("x").and(TInt.some().as("y")));
        //assertThrows(RuntimeException.class, () -> TInt.some().as("x").and(TStr.some().as("x")));
    }


    @Test
    void shouldHaveSymbols() {
        assertEquals("bool", TBool.some().symbol());
        assertEquals("int", TInt.some().symbol());
        assertEquals("real", TReal.some().symbol());
        assertEquals("str", TStr.some().symbol());
        assertEquals("inst", TInst.some().symbol());
        assertEquals("list", TLst.some().symbol());
        assertEquals("rec", TRec.some().symbol());
        //
        assertEquals("lucky", TBool.of(true).symbol("lucky").symbol());
        assertEquals("person", TRec.of("name", TStr.some(), "age", TInt.some()).symbol("person").symbol());
    }

    @Test
    void shouldToString() {
        assertEquals("bool", TBool.some().toString());
        assertEquals("true", TBool.of(true).toString());
        assertEquals("int~x", TInt.some().as("x").toString());
        assertEquals("3", TInt.of(3).toString());
        assertEquals("real", TReal.some().toString());
        assertEquals("6.6", TReal.of(6.6f).toString());
        assertEquals("str", TStr.some().toString());
        assertEquals("'hello'", TStr.of("hello").toString());
        assertEquals("[get,'inE'][get,'outV']", TInst.of("get", "inE").mult(TInst.of("get", "outV")).toString());
        assertEquals("inst", TInst.some().toString());
        assertEquals("[is,[get,'name'][eq,'marko']]", TInst.of("is", TInst.of("get", "name").mult(TInst.of("eq", "marko"))).toString());
        assertEquals("list", TLst.some().toString());
        assertEquals("['get';4;true;3.2]", TLst.of("get", 4, true, 3.2).toString());
        // TODO: assertEquals("(gt(1)|lt(20))~x", TInt.gt(1).or(TInt.lt(20)).as("x").toString());
        // TODO: assertEquals("(gt(1)&lt(20))~x", TInt.of(is(and(gt(1),lt(20)))).as("x").toString());
        assertEquals("rec", TRec.some().toString());
        assertEquals("['name':str,'age':int]", TRec.of("name", TStr.some(), "age", TInt.some()).toString());
        assertEquals("['name':str,'age':int]", TRec.of("name", TStr.some(), "age", TInt.some()).symbol("person").toString()); // TODO: @person prefix?
        assertEquals("['name':str~x,'age':int~y]~z", TRec.of("name", TStr.some().as("x"), "age", TInt.some().as("y")).as("z").symbol("person").toString());
    }

    @Test
    void shouldTestAny() {
        assertTrue(TBool.some().test(TBool.of(true)));
        assertTrue(TBool.some().test(TBool.of(false)));
        assertTrue(TInt.some().test(TInt.of(6)));
        assertTrue(TReal.some().test(TReal.of(6.3f)));
        assertTrue(TStr.some().test(TStr.of("hello")));
        assertTrue(TLst.some().test(TLst.of("get", 4, true, 3.2)));
        assertTrue(TRec.some().test(TRec.of("name", TStr.some(), "age", TInt.some())));
    }

    @Test
    void shouldMatchAny() {
        final Bindings bindings = new Bindings();
        TBool.of(true).as("a").match(bindings, TBool.of(true));
        assertEquals(1, bindings.size());
        assertEquals(TBool.of(true), bindings.get("a"));
        ///
        bindings.clear();
        TInt.some().as("a").match(bindings, TInt.of(6));
        assertEquals(1, bindings.size());
        assertEquals(TInt.of(6), bindings.get("a"));
        //
        bindings.clear();
        TReal.of(6.6f).as("a").match(bindings, TReal.of(6.6f));
        assertEquals(1, bindings.size());
        assertEquals(TReal.of(6.6f), bindings.get("a"));
        ///
        bindings.clear();
        TStr.some().as("a").match(bindings, TStr.of("marko"));
        assertEquals(1, bindings.size());
        assertEquals(TStr.of("marko"), bindings.get("a"));
        //
        bindings.clear();
        assertTrue(TInst.some().test(TInst.of("get", "outV")));
        TInst.some().as("a").match(bindings, TInst.of("get", "outV"));
        assertEquals(1, bindings.size());
        assertEquals(TInst.of("get", "outV"), bindings.get("a"));
        //
        bindings.clear();
        TInst.of("get", "outE").as("a").match(bindings, TInst.of("get", "outE"));
        assertEquals(1, bindings.size());
        assertEquals(TInst.of("get", "outE"), bindings.get("a"));
        //
        bindings.clear();
        TLst.of("get", TInt.some().as("a"), true, 3.2).as("b").match(bindings, TLst.of("get", 4, true, 3.2));
        assertEquals(2, bindings.size());
        assertEquals(TInt.of(4), bindings.get("a"));
        assertEquals(TLst.of("get", 4, true, 3.2), bindings.get("b"));
        //
        bindings.clear();
        TRec.of("name", TStr.some().as("a"), "age", TInt.some().as("b")).as("c").match(bindings, TRec.of("name", "marko", "age", 29));
        assertEquals(3, bindings.size());
        assertEquals(TStr.of("marko"), bindings.get("a"));
        assertEquals(TInt.of(29), bindings.get("b"));
        assertEquals(TRec.of("name", "marko", "age", 29), bindings.get("c"));
    }

    @Test
    void shouldTypeInstance() {
        assertTrue(TStr.some().isType());
        assertTrue(TStr.some().q(qmark).isType());
        assertTrue(TStr.some().set("marko").isInstance());
        assertTrue(TStr.some().symbol("str").as("a").isType());
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
        final List<Obj> somes = List.of(TObj.some(), TBool.some(), TInt.some(), TReal.some(), TStr.some(), TLst.some(), TRec.some(), TInst.some());
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

    // TODO @Test members
   /* void testMembers() {
        final TModel model = TModel.of("ex");
        model.define("ring", TObj.some().
                inst(TInst.of("xx", model.sym("ring").as("one")), TInst.of("map", 66)).
                inst(TInst.of("plus", model.sym("ring").as("one")), TInst.of("map", 55)));
        model.define("int", model.sym("ring").and(TInt.some()).
                member(TInt.some().as("zero"), TInt.zeroInt()).
                member(TInt.some().as("one"), TInt.oneInt()).
                inst(TInst.of("plus", TInt.some().as("zero")), TInst.of("map", 32)).
                inst(TInst.of("mult", TInt.some().as("one")), TInst.of("map", 77)));
        final Bindings bindings = new Bindings();
        assertEquals(TInst.of("map", 32), model.get("int").inst(bindings, TInst.of("plus", 0)).get());
        checkBindings(bindings);
        assertEquals(TInst.of("map", 77), model.get("int").inst(bindings, TInst.of("mult", 1)).get());
        checkBindings(bindings);
        assertEquals(TInst.of("map", 66), model.get("int").inst(bindings, TInst.of("xx", 1)).get());
        checkBindings(bindings);
        assertEquals(TInst.of("map", 55), model.get("int").inst(bindings, TInst.of("plus", 1)).get());
        checkBindings(bindings);
        System.out.println(model.get("int").toString());
        System.out.println(model.get("ring").toString());
    }*/

    private void checkBindings(final Bindings bindings) {
        assertEquals(2, bindings.size());
        assertTrue(bindings.has("zero"));
        assertTrue(bindings.has("one"));
        bindings.clear();
    }
}
