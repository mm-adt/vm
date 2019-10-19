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
import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.impl.atomic.TStr;
import org.mmadt.object.model.atomic.Int;
import org.mmadt.object.model.composite.Q;
import org.mmadt.object.model.composite.Rec;
import org.mmadt.object.model.type.Bindings;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.object.impl.composite.TQ.plus;
import static org.mmadt.object.impl.composite.TQ.star;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TStreamTest {

    @Test
    void shouldHaveCorrectQuantification() {
        Int type = TInt.gt(10).q(5, 7);
        Int instance1 = TInt.of(11, 12, TInt.of(13).q(4));
        Int instance2 = TInt.of(TInt.of(11), 12, TInt.of(13).q(2));
        Int instance3 = TInt.of(TInt.of(11), TInt.of(12), TInt.of(1).q(2));
        Int instance4 = TInt.of(11, TInt.of(12), TInt.of(1).q(4));
        Int instance5 = TInt.of(TInt.of(11), TInt.of(12), TInt.of(11).q(5));
        Int instance6 = TInt.of(TInt.of(11), TInt.of(12), TInt.of(11).q(6));
        Int instance7 = TInt.of(TInt.of(11).q(2), TInt.of(12).q(3), TInt.of(11).q(6));
        final List<Int> instances = List.of(instance1, instance2, instance3, instance4, instance5, instance6, instance7);
        //
        assertFalse(type.isInstance());
        assertTrue(type.isType());
        instances.forEach(i -> {
            assertTrue(i.isInstance());
            assertFalse(i.isReference());
            assertFalse(i.isType());
        });
        //
        final Q q = instance1.q().one();
        assertEquals(q, instance1.q());
        assertEquals(q, instance2.q());
        assertEquals(q, instance3.q());
        assertEquals(q, instance4.q());
        assertEquals(q, instance5.q());
        assertEquals(q, instance6.q());
        assertEquals(q, instance7.q());
        //
       /* assertTrue(type.test(instance1));
        assertFalse(type.test(instance2));
        assertFalse(type.test(instance3));
        assertFalse(type.test(instance4));
        assertTrue(type.test(instance5));
        assertFalse(type.test(instance6));
        assertFalse(type.test(instance7));
        //
        instance1.type(type);
        assertEquals(type, instance1.type());
        assertNull(instance2.type());
        assertThrows(RuntimeException.class, () -> instance2.type(type));
        //
        TInt type2 = TInt.some().gte(12).or(TInt.some().lte(11)).q(6);
        assertTrue(type2.test(instance1));
        assertFalse(type2.test(instance2));
        assertFalse(type2.test(instance3));
        assertTrue(type2.test(instance4));*/

    }

    //  @Test
    void shouldMatchCorrectly() {
        Rec type = TRec.of("name", TStr.some(), "age", TInt.some().as("x"), "hobbies", TStr.some().q(star).as("y"));
        Rec instance1 = TRec.of(
                TRec.of("name", "marko", "age", 29),
                TRec.of("name", "stephen", "age", 29, "hobbies", "coding"));
        //
        assertFalse(type.test(instance1));
        assertTrue(type.q(2).test(instance1));
        assertFalse(type.q(3).test(instance1));
        assertTrue(type.q(star).test(instance1));
        assertTrue(type.q(1, 3).test(instance1));
        assertFalse(type.q(3, 4).test(instance1));
        //
        Bindings bindings = new Bindings();
        assertTrue(type.q(2).match(bindings, instance1));
        assertEquals(TInt.of(29), bindings.get("x"));
        assertEquals(TStr.of("coding"), bindings.get("y"));
        assertEquals(2, bindings.size());
        //
        Rec instance2 = TRec.of(
                TRec.of("name", "marko", "age", 29),
                TRec.of("name", "stephen", "age", 29, "hobbies", TStr.of("coding", "family")));
        //
        assertFalse(type.test(instance2));
        assertTrue(type.q(2).test(instance2));
        assertFalse(type.q(3).test(instance2));
        assertTrue(type.q(star).test(instance2));
        assertTrue(type.q(1, 3).test(instance2));
        assertFalse(type.q(3, 4).test(instance2));
        // TODO: assertEquals(instance2.get(TStr.of("hobbies")));
        //
        bindings = new Bindings();
        assertTrue(type.q(star).match(bindings, instance2));
        assertEquals(TInt.of(29), bindings.get("x"));
        assertEquals(TStr.of(TStr.of("coding"), TStr.of("family")), bindings.get("y"));
        assertEquals(2, bindings.size());
        System.out.println(bindings);

    }

    // @Test
    void shouldTestWithOrCorrectly() {
        Rec type = TRec.of("name", TStr.some(), "age", TInt.some().as("x"), "hobbies", TStr.some().q(star).as("y"));
        Rec instance1 = TRec.of(
                TRec.of("name", "marko", "age", 29),
                TRec.of("name", "stephen", "age", 29, "hobbies", "coding"));
        //
        assertFalse(type.test(instance1));
        assertTrue(type.q(2).test(instance1));
        assertFalse(type.q(3).test(instance1));
        assertTrue(type.q(star).test(instance1));
        assertTrue(type.q(1, 3).test(instance1));
        assertFalse(type.q(3, 4).test(instance1));
        //
        Bindings bindings = new Bindings();
        assertTrue(type.q(plus).match(bindings, instance1));
        assertEquals(TInt.of(29), bindings.get("x"));
        assertEquals(TStr.of("coding"), bindings.get("y"));
        assertEquals(2, bindings.size());
        //
        Rec instance2 = TRec.of(
                TRec.of("name", "marko", "age", 29),
                TRec.of("name", "stephen", "age", 29, "hobbies", TStr.of("coding", "family")));
        //
        assertTrue(instance2.isInstance());
        assertFalse(type.test(instance2));
        assertTrue(type.q(2).test(instance2));
        assertFalse(type.q(3).test(instance2));
        assertTrue(type.q(star).test(instance2));
        assertTrue(type.q(1, 3).test(instance2));
        assertFalse(type.q(3, 4).test(instance2));
        //
        bindings = new Bindings();
        assertTrue(type.q(1, 7).match(bindings, instance2));
        assertEquals(TInt.of(29), bindings.get("x"));
        assertEquals(TStr.of("coding", "family"), bindings.get("y"));
        assertEquals(2, bindings.size());
        System.out.println(bindings);
    }

    //@Test
    void shouldSupportBinding() {
        Rec type = TRec.of("name", TStr.some(), "age", TInt.some().as("x"), "hobbies", TStr.some().q(star).as("y"));
        Rec instance1 = TRec.of(
                TRec.of("name", "marko", "age", 29),
                TRec.of("name", "stephen", "age", 29, "hobbies", TStr.of(TStr.of("coding"), TStr.of("family"))));
        //
        Bindings bindings = new Bindings();
        assertTrue(type.q(plus).match(bindings, instance1));
        assertEquals(TInt.of(29), bindings.get("x"));
        assertEquals(TStr.of("coding", "family"), bindings.get("y"));
        assertEquals(2, bindings.size());
        //
        assertTrue(type.bind(bindings).isType());
        System.out.println(type.bind(bindings));
    }

}
