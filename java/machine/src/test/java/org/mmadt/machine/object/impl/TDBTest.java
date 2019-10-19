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
import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.type.Bindings;
import org.mmadt.machine.object.model.type.PAnd;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.machine.object.model.composite.Q.Tag.star;
import static org.mmadt.machine.object.model.composite.Q.Tag.one;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TDBTest {

    @Test
    void shouldInflateNestedRecursiveTypes() {
        final TModel model = TModel.of("ex");
        final Inst bc =
                TInst.of("define", "person", TRec.of("name", TStr.some(), "age", TInt.some(), "friend", model.sym("person"))).mult(
                        TInst.of("define", "people", model.sym("person").q(star))).mult(
                        TInst.of("define", "db", TRec.of("persons", model.sym("people"))));

        model.model(bc);
        final TRec<Str, Obj> people = model.get("people");
        final TRec<Str, Obj> person = model.get("person");
        //
        assertEquals("people", model.sym("people").symbol());
        assertTrue(people.q().isStar());
        assertEquals("person", model.sym("person").symbol());
        assertEquals(person.q(one).q(), person.q());
        assertNotEquals(person, people);
        assertNotEquals(person.toString(), people.toString());
        //
        assertEquals(TStr.some(), person.get(TStr.of("name")));
        assertEquals(TInt.some(), person.get(TStr.of("age")));
        final Obj friend = person.get(TStr.of("friend"));
        assertEquals("person", friend.symbol());
        assertEquals(person, TSym.fetch(friend));
        //
        //System.out.println(people + "\n\n" + person + "\n\n" + friend);
        assertDoesNotThrow(people::toString); // check for stack overflow
        assertDoesNotThrow(person::toString); // check for stack overflow
        assertDoesNotThrow(friend::toString); // check for stack overflow
        //
        Rec<Obj, Obj> marko = TRec.of("name", "marko", "age", 29);
        final Rec<Obj, Obj> kuppitz = TRec.of("name", "kuppitz", "age", 25, "friend", marko);
        assertFalse(person.test(marko));
        assertFalse(person.test(kuppitz)); // since marko's type isn't the expected type, it checks if marko is a type of person
        //
        assertEquals(Tokens.REC, marko.symbol());
        marko.put(TStr.of("friend"), kuppitz);
        marko.type(person);
        kuppitz.type(person);
        assertTrue(person.test(marko));
        assertTrue(person.test(kuppitz)); // since marko's type isn't the expected type, it checks if marko is a type of person
        assertTrue(person.test(marko));
        ///////
        final Rec<Obj, Obj> marko2 = TRec.of("name", "marko", "age", 29);
        final Rec kuppitz2 = TRec.of("name", "kuppitz", "age", 25, "friend", marko);
        assertTrue(person.test(kuppitz2)); // since marko's type is the expected type, it assumes correct structure beneath (avoids infinite recursion)
        assertFalse(person.test(marko2));
        marko2.put(TStr.of("friend"), kuppitz2);
        assertTrue(person.test(kuppitz2)); // since marko's type is the expected type, it assumes correct structure beneath (avoids infinite recursion)
        assertTrue(person.test(marko2));
    }

    @Test
    void shouldMatchNestedRecursiveTypes() {
        // @person&[name:@string,age:@int,friend:@person&[age:@int~a,friend:@person&[age:@int~b]~c]~d]
        final TModel model = TModel.of("ex");
        final Inst bc =
                TInst.of("define", "person", TRec.of("name", TStr.some(), "age", TInt.some(),
                        "friend", model.sym("person").and(TRec.of("age", TInt.some().as("a"),
                                "friend", model.sym("person").and(TRec.of("age", TInt.some().as("b"))).as("c"))).as("d"))).mult(
                        TInst.of("define", "people", model.sym("person").q(star))).mult(
                        TInst.of("define", "db", TRec.of("persons", model.sym("people"))));

        model.model(bc);
        //
        final TRec<Obj, Obj> person = model.get("person");
        final TObj friend = (TObj) person.get(TStr.of("friend"));
        assertDoesNotThrow(person::toString); // check for stack overflow
        assertDoesNotThrow(friend::toString); // check for stack overflow
        //
        final Rec<Obj, Obj> marko = TRec.of("name", "marko", "age", 29);
        final Rec<Obj, Obj> kuppitz = TRec.of("name", "kuppitz", "age", 25, "friend", marko);
        assertFalse(person.test(kuppitz)); // tests the nested type to determine if it has substructure
        assertFalse(person.test(marko));
        assertThrows(RuntimeException.class, () -> marko.type(person));
        assertThrows(RuntimeException.class, () -> kuppitz.type(person));

        marko.put(TStr.of("friend"), kuppitz);
        marko.type(person);

        assertTrue(person.test(kuppitz));
        assertTrue(person.test(marko));
        assertFalse(friend.test(TRec.of("age", 30, "friend", marko)));
        Bindings bindings = new Bindings();
        kuppitz.type(person);
        person.match(bindings, kuppitz);
        //System.out.println(person + "!!!" + kuppitz);
        assertEquals(4, bindings.size());
        assertEquals(TInt.of(29), bindings.get("a"));
        assertEquals(TInt.of(25), bindings.get("b"));
        assertEquals(kuppitz, bindings.get("c"));
        assertEquals(marko, bindings.get("d"));
        //
        bindings = new Bindings();
        friend.match(bindings, marko);
        assertEquals(4, bindings.size());
        assertEquals(TInt.of(29), bindings.get("a"));
        assertEquals(TInt.of(25), bindings.get("b"));
        assertEquals(kuppitz, bindings.get("c"));
        assertEquals(marko, bindings.get("d"));
    }

    @Test
    void shouldSupportSecondDegreeRecursiveTypes() {
        final TModel model = TModel.of("ex");
        final Inst bc =
                TInst.of("define", "person", TRec.of("name", TStr.some(), "age", TInt.some(), "company", model.sym("company"))).mult(
                        TInst.of("define", "company", TRec.of("title", TStr.some(), "ceo", model.sym("person").and(TRec.of("age", TInt.gt(30)))))).mult(
                        TInst.of("define", "db", model.sym("company").q(star)));

        System.out.println(bc);
        model.model(bc);
        final TRec person = model.get("person");
        final TRec company = model.get("company");
        final TRec ceo = (TRec) company.get(TStr.of("ceo"));
        //
        assertDoesNotThrow(model::toString); // check for stack overflow
        assertDoesNotThrow(person::toString); // check for stack overflow
        assertDoesNotThrow(company::toString); // check for stack overflow
        assertDoesNotThrow(ceo::toString); // check for stack overflow
        //
        assertNotEquals(person, ceo);
        assertNotEquals(person, ((PAnd) ceo.get()).get(1));
    }

    @Test
    void shouldSupportSubtyping() {
        final TModel model = TModel.of("ex");
        final Inst bc =
                TInst.of("define", "element", TRec.of("id", TInt.some(), "label", TStr.some())).mult(
                        TInst.of("define", "vertex", model.sym("element").and(TRec.of("outV", model.sym("vertex"), "inV", model.sym("vertex"))))).mult(
                        TInst.of("define", "db", model.sym("vertex").q(star)));

        model.model(bc);
        final TRec<Obj, Obj> element = model.get("element");
        assertEquals(TStr.some(), element.get(TStr.of("label")));
        final TRec<Obj, Obj> vertex = model.get("vertex");
        assertEquals(vertex, TSym.fetch(vertex.get(TStr.of("outV")))); // TODO: fetch?
    }
}
