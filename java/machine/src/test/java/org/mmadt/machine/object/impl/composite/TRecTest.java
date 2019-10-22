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

package org.mmadt.machine.object.impl.composite;

import org.junit.jupiter.api.Test;
import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.type.Bindings;
import org.mmadt.machine.object.model.type.PAnd;
import org.mmadt.machine.object.model.type.PMap;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.language.__.eq;
import static org.mmadt.language.__.gt;
import static org.mmadt.language.__.is;
import static org.mmadt.language.__.type;
import static org.mmadt.machine.object.model.composite.Q.Tag.one;
import static org.mmadt.machine.object.model.composite.Q.Tag.plus;
import static org.mmadt.machine.object.model.composite.Q.Tag.qmark;
import static org.mmadt.machine.object.model.composite.Q.Tag.star;
import static org.mmadt.machine.object.model.composite.Q.Tag.zero;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TRecTest {

    @Test
    void testBytecodeTestingMatchingAndBinding() {
        final Bindings bindings = new Bindings();
        final Rec<Str, ?> type = TRec.of(
                "name", is(eq("marko")).as("x"),
                "age", is(type().a(TInt.some())).is(gt(23)).as("y"));
        assertEquals("y", type.get(TStr.of("age")).variable());
        final Rec<Str, Obj> person = TRec.of("name", "marko", "age", 29);
        // System.out.println(type + ":::" + person);
        assertTrue(type.test(person));
        assertFalse(person.test(type));
        assertTrue(type.match(bindings, person));
        assertEquals(2, bindings.size());
        assertEquals(TStr.of("marko"), bindings.get("x"));
        assertEquals(TInt.of(29), bindings.get("y"));
        // TODO final Rec<Str,?> bound = type.bind(bindings);
        // System.out.println(bound);
        //  assertEquals(person, bound);
        final Lst list = TLst.of(TStr.some().as("x"), TInt.some().as("y"));
        assertEquals(TLst.of("marko", 29), list.bind(bindings));
    }

    @Test
    void shouldAndCorrectly() {
        Rec rec1 = TRec.of("name", "marko");
        Rec rec2 = TRec.of("age", 29);
        Rec rec3 = TRec.of("name", "marko", "age", 29);
        assertTrue(rec1.constant());
        assertTrue(rec2.constant());
        assertTrue(rec3.constant());
        assertEquals(rec3, rec1.and(rec2));
        assertEquals(rec3, rec3.and(rec3));
        ///
        rec1 = TRec.of("name", "marko");
        assertTrue(rec1.constant());
        assertEquals(TRec.of("name", "marko", "age", 29), rec1.and(rec2));
        assertEquals(rec3, rec3.or(rec3));
        assertEquals(rec1, rec1.or(rec1));
        //
        rec1 = TRec.of("name", TStr.some());
        assertFalse(rec1.constant());
        assertEquals(TRec.of("name", TStr.some(), "age", 29), rec1.and(rec2));
    }

    @Test
    void shouldOrCorrectly() {
        Rec rec1 = TRec.of("name", "marko");
        Rec rec2 = TRec.of("age", 29);
        Rec rec3 = (TRec) rec1.or(rec2);
        assertTrue(rec1.constant());
        assertTrue(rec2.constant());
        assertFalse(rec3.constant());
        assertEquals(rec3, rec1.or(rec2));
        assertEquals(rec3, rec3.or(rec3));
        ///
        rec1 = TRec.of("name", "marko");
        assertTrue(rec1.constant());
        assertEquals(rec3, rec1.or(rec2));
        //
        Rec rec4 = (TRec) TRec.of("name", TStr.some()).or(TRec.of("age", 29));
        assertFalse(rec4.constant());
        assertEquals(rec4, TRec.of("name", TStr.some()).or(rec2));
    }

    @Test
    void shouldSupportTypeReferenceInstance() {
        final Rec recordType = TRec.of("name", TStr.some(), "age", is(gt(32))).
                access(TInst.of("db").mult(TInst.of("get", "name")).mult(TInst.of("eq", "marko"))).
                inst(TInst.of("get", "outE"),
                        TInst.of("db").mult(TInst.of("get", "E")).mult(TInst.of("is", TInst.of("get", "outV").mult(TInst.of("eq", 1))))).
                inst(TInst.of(TStr.some(), "marko"),
                        TInst.of("db").mult(TInst.of("get", "E")).mult(TInst.of("is", TInst.of("get", "outV").mult(TInst.of("eq", 1)))));
        ///
        assertFalse(recordType.isType());
        assertTrue(recordType.isReference());
        assertFalse(recordType.isInstance());
        assertTrue(recordType.access(null).isType());
        ///
        assertFalse(recordType.constant());
        assertFalse(recordType.test(TRec.of("name", 2)));
        assertTrue(recordType.test(TRec.of("name", "marko", "age", 45)));

        assertFalse(TRec.of("name", TStr.some(), "age", TInt.some()).constant());
        assertFalse(TRec.of("name", "marko", "stats", TRec.of("age", TInt.some())).constant());
        assertTrue(TRec.of("name", "marko", "stats", TRec.of("age", 29)).constant());
        assertFalse(TRec.of("name", TStr.some(), "age", TInt.some()).constant());
        assertFalse(TRec.of("name", TStr.some(), "age", TInt.some()).constant());
    }

    @Test
    void shouldSupportQuantifiersInTest() {
        Rec person = TRec.of("name", TStr.some(), "age", TInt.some().q(qmark)).symbol("person");
        final Rec marko = TRec.of("name", "marko", "age", TInt.of(29));
        final Rec kuppitz = TRec.of("name", "kuppitz");
        assertTrue(person.test(marko));
        assertTrue(person.test(kuppitz));
        //
        person = TRec.of("name", TStr.some(), "age", TInt.some().q(zero)).symbol("person");
        assertFalse(person.test(marko));
        assertTrue(person.test(kuppitz));
        //
        person = TRec.of("name", TStr.some(), "age", TInt.some().q(star)).symbol("person");
        assertTrue(person.test(marko));
        assertTrue(person.test(kuppitz));
        //
        person = TRec.of("name", TStr.some(), "age", TInt.some().q(plus)).symbol("person");
        assertTrue(person.test(marko));
        assertFalse(person.test(kuppitz));
        //
        person = TRec.of("name", TStr.some(), "age", TInt.some().q(one)).symbol("person");
        assertTrue(person.test(marko));
        assertFalse(person.test(kuppitz));
    }

    @Test
    void shouldSupportQuantifiersInMatch() {
        Bindings bindings = new Bindings();
        Rec<Obj, Obj> person = TRec.of("name", TStr.some(), "age", TInt.some().q(qmark).as("a")).symbol("person");
        final Rec marko = TRec.of("name", "marko", "age", TInt.of(29));
        final Rec kuppitz = TRec.of("name", "kuppitz");
        assertTrue(person.match(bindings, marko));
        assertEquals(1, bindings.size());
        assertEquals(TInt.of(29), bindings.get("a"));
        bindings = new Bindings();
        assertTrue(person.match(bindings, kuppitz));
        bindings.clear();
        //
        person = TRec.of("name", TStr.some(), "age", TInt.none().as("a")).symbol("person");
        assertFalse(person.match(bindings, marko));
        assertEquals(0, bindings.size());
        assertTrue(person.match(bindings, kuppitz));
        System.out.println(bindings);
        assertEquals(0, bindings.size());
        //
        person = TRec.of("name", TStr.some(), "age", TInt.some().q(star).as("a")).symbol("person");
        assertTrue(person.match(bindings, marko));
        assertEquals(1, bindings.size());
        assertEquals(TInt.of(29), bindings.get("a"));
        bindings = new Bindings();
        assertTrue(person.match(bindings, kuppitz));
        assertEquals(0, bindings.size());

        //
        person = TRec.of("name", TStr.some(), "age", TInt.some().q(plus).as("a")).symbol("person");
        assertTrue(person.match(bindings, marko));
        assertEquals(1, bindings.size());
        assertEquals(TInt.of(29), bindings.get("a"));
        bindings = new Bindings();
        assertFalse(person.match(bindings, kuppitz));
        assertEquals(0, bindings.size());
        //
        person = TRec.of("name", TStr.some(), "age", TInt.some().as("a")).symbol("person");
        assertTrue(person.match(bindings, marko));
        assertEquals(1, bindings.size());
        assertEquals(TInt.of(29), bindings.get("a"));
        bindings = new Bindings();
        assertFalse(person.test(kuppitz));
        assertEquals(0, bindings.size());
    }

    @Test
    void shouldSupportComplexAndPatterns() {
        final Random random = new Random();
        Obj recordType = null;
        for (int i = 0; i < 100; i++) {
            recordType = TRec.of("name", TStr.some(), "age", TInt.gt(28));
            if (random.nextBoolean())
                recordType = recordType.and(TRec.of("name", TStr.some()).and(recordType));
            if (random.nextBoolean())
                recordType = TRec.some().and(TRec.of("name", TStr.some())).and(TRec.of("age", TInt.some()));
        }
        recordType = TRec.of("name", TStr.some(), "age", TInt.gt(28)).and(recordType);
        assertEquals(recordType, TRec.of("name", TStr.some(), "age", TInt.gt(28)));
        assertTrue(recordType.isType());
        assertFalse(recordType.isReference());
        assertFalse(recordType.isInstance());
    }

    @Test
    void shouldMatchNestedRecords1() {
        final Rec person = TRec.of("name", TStr.some().as("n1"), "age", TInt.some(),
                "phones", TRec.of(
                        "home", TInt.some().as("h1").or(TStr.some().as("h2")),
                        "work", TInt.gt(0).as("w1").or(TStr.some()))).as("x");

        final Rec marko = TRec.of("name", "marko", "age", 29, "phones", TRec.of("home", 123, "work", 34));
        assertTrue(person.test(marko));
        assertFalse(marko.test(person));
        assertTrue(marko.test(marko));
        assertFalse(person.test(person));
        final Bindings bindings = new Bindings();
        assertTrue(person.match(bindings, marko));
        assertEquals(4, bindings.size());
        assertEquals(TStr.of("marko"), bindings.get("n1"));
        assertEquals(TInt.of(123), bindings.get("h1"));
        assertEquals(TInt.of(34), bindings.get("w1"));
        assertEquals(marko, bindings.get("x"));
    }

    @Test
    void shouldMatchNestedRecords2() {
        Rec type1 = TRec.of("name", TStr.some().as("n"), "address", TRec.of("state", TStr.some().as("s"), "zipcode", TInt.some().as("z")));
        Rec rec1 = TRec.of("name", "marko", "address", TRec.of("state", "NM", "zipcode", 87506));
        assertTrue(rec1.constant());
        assertFalse(type1.constant());
        Bindings bindings = new Bindings();
        assertTrue(type1.test(rec1));
        assertEquals(0, bindings.size());
        assertTrue(type1.match(bindings, rec1));
        assertEquals(3, bindings.size());
        assertEquals(TStr.of("marko"), bindings.get("n"));
        assertEquals(TStr.of("NM"), bindings.get("s"));
        assertEquals(TInt.of(87506), bindings.get("z"));
    }

    @Test
    void shouldBindQuantifier() {
        final Rec person = TRec.of("name", TStr.some().as("x"), "age", TInt.some()).q(qmark).access(TInst.of("db").mult(TInst.of("is", TInst.of("get", "name").mult(TInst.of("eq", TStr.some().as("x"))))));
        assertEquals(qmark.apply(person.q()), person.q());
        final Bindings bindings = new Bindings();
        bindings.put("x", TStr.of("marko"));
        final Obj marko = person.bind(bindings);
        assertEquals(qmark.apply(person.q()), person.q());
        assertEquals(qmark.apply(marko.q()), marko.q());
    }

    @Test
    void shouldAndTypesAndInstances() {
        final Rec<?, ?> named = TRec.of("name", TStr.some()).symbol("named");
        final Rec<?, ?> aged = TRec.of("age", TInt.some());
        final Rec<?, ?> human = TRec.some().symbol("human");
        final Rec<?, ?> person = named.and(aged).and(human).symbol("person");
        final Rec<Obj, Obj> marko = TRec.of("name", "marko");
        //
        assertNotNull(named.get());
        assertEquals("named", named.symbol());
        assertTrue(named.isType());
        assertFalse(named.isReference());
        assertFalse(named.isInstance());
        //
        assertNotNull(aged.get());
        assertEquals(Tokens.REC, aged.symbol());
        assertTrue(aged.isType());
        assertFalse(aged.isReference());
        assertFalse(aged.isInstance());
        //
        assertNull(human.get());
        assertEquals("human", human.symbol());
        assertTrue(human.isType());
        assertFalse(human.isReference());
        assertFalse(human.isInstance());
        //
        assertNotNull(person.get());
        assertEquals("person", person.symbol());
        assertTrue(person.isType());
        assertFalse(person.isReference());
        assertFalse(person.isInstance());
        //
        assertTrue(marko.isInstance());
        assertFalse(marko.isType());
        assertEquals(Tokens.REC, marko.symbol());
        assertFalse(marko.isType());
        assertFalse(marko.isReference());
        assertTrue(marko.isInstance());
        //
        assertTrue(named.test(marko));
        assertFalse(aged.test(marko));
        assertTrue(human.test(marko));
        assertFalse(person.test(marko));
        assertTrue(marko.test(marko));
        //
        marko.type(named);
        assertEquals(named, marko.type());
        assertThrows(RuntimeException.class, () -> marko.type(aged));
        marko.type(human);
        assertEquals(human, marko.type());
        assertThrows(RuntimeException.class, () -> marko.type(person));
        assertThrows(RuntimeException.class, () -> marko.type(marko));
        //
        marko.put(TStr.of("age"), TInt.of(29));
        marko.type(named);
        marko.type(aged);
        marko.type(human);
        marko.type(person);
        assertEquals(Tokens.REC, marko.symbol());
        assertEquals("person", marko.type().symbol());
        //
        Rec<Obj, Obj> marko2 = (Rec) marko.and(TRec.of("state", "ca")); // TODO!!
        assertNotEquals(marko, marko2);
        assertEquals(person, marko2.type());
        assertTrue(person.test(marko2));
        Rec dweller = (Rec) person.and(TRec.of("state", TStr.of("nm").or(TStr.of("az"))));
        assertThrows(RuntimeException.class, () -> marko.type(dweller));
        assertEquals(person, marko2.type());
        marko2.put(TStr.of("state"), TStr.of("nm"));
        marko2.type(dweller);
        assertEquals(dweller, marko2.type());
        assertNotEquals(marko, marko2);
        //
        /*System.out.println(named);
        System.out.println(aged);
        System.out.println(human);
        System.out.println(person);
        System.out.println(dweller);
        System.out.println(marko);
        System.out.println(marko2);*/

    }

    @Test
    void shouldOrTypesAndInstances() {
        final Rec<?, ?> named = TRec.of("name", TStr.some()).symbol("named");
        final Rec<?, ?> aged = TRec.of("age", TInt.some()).inst(TInst.of("get", "age"), TInst.of("get", "years"));
        final Rec<?, ?> human = TRec.some().symbol("human");
        final Obj object = TObj.some().inst(TInst.of("get", "name"), TInst.of("get", "alias"));
        final Obj person = named.or(aged).and(human).or(object).symbol("person");
        final Rec<Obj, Obj> marko = TRec.of("name", "marko");
        final Rec<Obj, Obj> kuppitz = TRec.of("age", 25);
        final Rec<?, ?> markoKuppitz = (TRec) marko.or(kuppitz);
        //
        assertEquals("named", named.symbol());
        assertEquals(Tokens.REC, aged.symbol());
        assertEquals("person", person.symbol());
        assertEquals(Tokens.REC, markoKuppitz.symbol());
        //
        assertTrue(named.test(marko));
        assertFalse(aged.test(marko));
        assertTrue(human.test(marko));
        assertTrue(person.test(marko));
        assertTrue(marko.test(marko));
        assertTrue(object.test(marko));
        assertFalse(marko.test(object));
        //
        assertFalse(named.test(kuppitz));
        assertTrue(aged.test(kuppitz));
        assertTrue(human.test(kuppitz));
        assertTrue(person.test(kuppitz));
        assertTrue(kuppitz.test(kuppitz));
        assertTrue(object.test(kuppitz));
        assertFalse(kuppitz.test(object));
        //
        assertTrue(markoKuppitz.isType());
        assertTrue(markoKuppitz.test(marko));
        assertTrue(markoKuppitz.test(kuppitz));
        //
        marko.type(markoKuppitz);
        marko.type(person);
        kuppitz.type(markoKuppitz);
        kuppitz.type(person);
        //
        marko.type(named);
        assertFalse(marko.inst(new Bindings(), TInst.of("get", "name")).isPresent());
        marko.type(person);
        // assertFalse(marko.inst(new Bindings(), TInst.of("get", "age")).isPresent()); // TODO: only instructions for the bound type should be available
        assertEquals(TInst.of("get", "alias"), marko.inst(new Bindings(), TInst.of("get", "name")).get());


    }

    @Test
    void shouldAndWithObject() {
        final Rec<?, ?> person = TRec.of("name", TStr.some(), "age", TInt.some()).symbol("person");
        final Obj object = TObj.some().inst(TInst.of("get", "name"), TInst.of("get", "alias"));
        final Rec personObject = (Rec) person.and(object);
        final Rec<Obj, Obj> marko = TRec.of("name", "marko", "age", 29);
        //
        assertTrue(person.test(marko));
        assertTrue(object.test(marko));
        assertTrue(personObject.test(marko));
        //
        assertTrue(person.isType());
        assertTrue(object.isType());
        assertTrue(marko.isInstance());
        //
        assertEquals("person", person.symbol());
        assertEquals(Tokens.OBJ, object.symbol());
        assertEquals(Tokens.REC, personObject.symbol());
        assertTrue(personObject.get() instanceof PAnd);
        assertEquals(2, ((PAnd) personObject.get()).predicates().size());
        //
        assertNull(person.instructions());
        assertEquals(1, object.instructions().size());
// TODO: merge conjunctions?        assertEquals(1, personObject.instructions().size());
        //
        assertFalse(marko.inst(new Bindings(), TInst.of("get", "name")).isPresent());
        marko.type(personObject);
        assertEquals(TInst.of("get", "alias"), marko.inst(new Bindings(), TInst.of("get", "name")).get());
    }

    @Test
    void shouldSupportRecursiveTypeTesting() {
        final Rec<Obj, Obj> person = TRec.of("name", TStr.some(), "friend", TRec.some().symbol("person")).symbol("person");
        person.put(TStr.of("friend"), person);
        assertDoesNotThrow(person::toString); // check for stack overflow
        final Rec<Obj, Obj> marko = TRec.of("name", "marko");
        final Rec<Obj, Obj> kuppitz = TRec.of("name", "kuppitz", "friend", marko);
        assertEquals(marko, kuppitz.get(TStr.of("friend")));
        assertFalse(person.test(marko));
        marko.put(TStr.of("friend"), kuppitz);
        assertEquals(marko, kuppitz.get(TStr.of("friend")));
        assertEquals(kuppitz, marko.get(TStr.of("friend")));
        marko.type(person);
        kuppitz.type(person); // stackoverflow without type breaker
        assertTrue(person.test(marko));
        assertTrue(person.test(kuppitz));
    }

    @Test
    void shouldSupportRecursiveTypeMatching() {
        final Rec<Obj, Obj> person = TRec.of("name", TStr.some().as("a"), "friend", TRec.some().symbol("person")).symbol("person");
        person.put(TStr.of("friend"), person.as("b").q(qmark));
        assertDoesNotThrow(person::toString); // check for stack overflow
        final Rec<Obj, Obj> marko = TRec.of("name", "marko");
        marko.type(person);
        final Rec<Obj, Obj> kuppitz = TRec.of("name", "kuppitz", "friend", marko);
        kuppitz.type(person);
        assertEquals(marko, kuppitz.get(TStr.of("friend")));
        assertTrue(person.test(marko));
        assertTrue(person.test(kuppitz));
        marko.put(TStr.of("friend"), kuppitz);
        assertEquals(marko, kuppitz.get(TStr.of("friend")));
        assertEquals(kuppitz, marko.get(TStr.of("friend")));
        assertEquals(person, marko.type());
        assertEquals(person, kuppitz.type());
        assertTrue(person.test(marko));
        assertTrue(person.test(kuppitz));
        ///
        Bindings bindings = new Bindings();
        assertTrue(person.match(bindings, marko));
        assertEquals(2, bindings.size());
        assertEquals("marko", bindings.get("a").get());
        assertEquals(kuppitz, bindings.get("b"));
        ///
        bindings = new Bindings();
        person.match(bindings, kuppitz);
        assertEquals(2, bindings.size());
        assertEquals("kuppitz", bindings.get("a").get());
        assertEquals(marko, bindings.get("b"));
    }

    @Test
    void shouldCarrySymbols() {
        final Rec<?, ?> person = TRec.of("name", TStr.some()).symbol("person");
        final Rec<?, ?> aged = TRec.of("age", TInt.some());
        assertEquals("person", person.symbol());
        assertEquals(Tokens.REC, aged.symbol());
        final Rec<?, ?> personAndAged = (Rec) person.and(aged);
        assertEquals(Tokens.REC, personAndAged.symbol());
        final PAnd and = personAndAged.get();
        assertEquals(2, and.predicates().size());
        assertEquals("person", and.<TRec>get(0).symbol());
        assertEquals(Tokens.REC, and.<TRec>get(1).symbol());

    }

    @Test
    void shouldSupportNoneEnds() {
        final Rec<?, ?> a = TRec.of("name", "marko", "age", 29, TObj.none(), 32);
        assertEquals(3, a.<PMap>get().size());
        assertTrue(TRec.of("name", "marko").test(a));
        assertTrue(TRec.of("age", TInt.some()).test(a));
        assertFalse(TRec.of("name", "marko", "boo", TInt.some()).test(a));
        assertFalse(TRec.of("age", TObj.none()).test(a));
        assertFalse(TRec.of(TObj.none(), TObj.none()).test(a));
        assertTrue(TRec.of(TObj.none(), 32).test(a));
        assertEquals(TObj.none(), TRec.of("name", "null").get(TStr.of("blob")));
    }

}
