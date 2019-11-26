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

package org.mmadt.machine.object.impl.type;

import org.junit.jupiter.api.Test;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.type.Bindings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.language.__.gt;
import static org.mmadt.language.__.is;
import static org.mmadt.machine.object.model.composite.Q.Tag.star;
import static org.mmadt.machine.object.model.composite.Q.Tag.one;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class BindingsTest {

    @Test
    void shouldBindAtomics() {
        Obj type1 = TInt.of(is(gt(29))).label("x");
        System.out.println(type1);
        final Bindings bindings = new Bindings();
        assertFalse(type1.match(bindings, TInt.of(23)));
        assertEquals(0, bindings.size());
        assertTrue(type1.match(bindings, TInt.of(30)));
        assertEquals(1, bindings.size());
        assertEquals(TInt.of(30), bindings.get("x"));
        assertFalse(type1.match(bindings, TInt.of(23)));
        assertEquals(1, bindings.size());
        assertEquals(TInt.of(30), bindings.get("x"));
    }

    @Test
    void shouldBindRecords() {
        Rec type1 = TRec.of("name", TStr.of().label("x")); // [name:@string$x]
        Rec type2 = TRec.of("name", TStr.of()).label("x"); // [name:@string]$x
        Rec type3 = TRec.of("name", TStr.of().label("x"), "alias", TStr.of().label("x")); // [name:@string$x,alias:@string$x]
        Rec rec1 = TRec.of("name", "marko");
        Rec rec2 = TRec.of("name", "kuppitz", "alias", "guru");
        Rec rec3 = TRec.of("name", "marko", "alias", "marko");
        assertFalse(type1.constant());
        assertFalse(type2.constant());
        assertFalse(type3.constant());
        assertTrue(rec1.constant());
        assertTrue(rec2.constant());
        assertTrue(rec3.constant());

        final Bindings bindings = new Bindings();
        ///// TYPE 1 /////
        assertTrue(type1.match(bindings, rec1));
        assertEquals(1, bindings.size());
        assertEquals(TStr.of("marko"), bindings.get("x"));
        //
        assertFalse(type1.match(bindings, rec2));
        assertEquals(1, bindings.size());
        assertEquals(TStr.of("marko"), bindings.get("x"));
        bindings.clear();
        assertTrue(type1.match(bindings, rec2));
        assertEquals(1, bindings.size());
        assertEquals(TStr.of("kuppitz"), bindings.get("x"));
        ///// TYPE 2 /////
        bindings.clear();
        assertTrue(type2.match(bindings, rec1));
        assertEquals(1, bindings.size());
        assertEquals(rec1, bindings.get("x"));
        bindings.clear();
        //
        assertTrue(type2.match(bindings, rec2));
        assertEquals(rec2, bindings.get("x"));
        bindings.clear();
        assertTrue(type2.match(bindings, rec2));
        //System.out.println(type2);
        assertEquals(1, bindings.size());
        assertEquals(rec2, bindings.get("x"));
        ///// TYPE 3 /////
        bindings.clear();
        assertFalse(type3.match(bindings, rec1));
        assertEquals(0, bindings.size());
        //
        assertFalse(type3.match(bindings, rec2));
        assertEquals(0, bindings.size());
        assertTrue(type3.match(bindings, rec3));
        assertEquals(1, bindings.size());
        assertEquals(TStr.of("marko"), bindings.get("x"));
    }


    @Test
    void shouldBindInstructions() {
        Rec<?, ?> type1 = TRec.of("name", TStr.of().label("a"), "age", TInt.of().label("b"))
                .q(star)
                .inst(TInst.of("get", "age"),
                        TInst.of("db").mult(TInst.of("get", "ages")).mult(TInst.of("is", TInst.of("get", "name").mult(TInst.of("eq", TStr.of().label("a"))))).mult(TInst.of("get", "age")));
        Rec rec1 = TRec.of("name", "marko", "age", 29);
        final Bindings bindings = new Bindings();
        assertTrue(type1.match(bindings, rec1));
        assertEquals(TStr.of("marko"), bindings.get("a"));
        assertEquals(TInt.of(29), bindings.get("b"));
        assertEquals(2, bindings.size());
        final Rec type2 = type1.bind(bindings);
        assertNotEquals(type1, type2);
        assertNotEquals(type1.instructions(), type2.instructions());
        assertFalse(type1.constant());
        assertTrue(type2.constant());
        assertTrue(type2.instructions().toString().contains("marko"));
        assertTrue(type2.instructions().entrySet().iterator().next().getKey().constant());
        assertTrue(type2.instructions().entrySet().iterator().next().getValue().constant());
        assertTrue(type1.instructions().entrySet().iterator().next().getKey().constant());
        assertFalse(type1.instructions().entrySet().iterator().next().getValue().constant());
        //
        assertTrue(type1.q().isStar());
        assertTrue(type2.q().isStar());
        final TObj type3 = type2.q(one);
        assertEquals(type2.instructions(), type3.instructions());
        assertTrue(type1.q().isStar());
        assertTrue(type2.q().isStar());
        assertEquals(type3.q().one(), type3.q());
        assertNotEquals(type1, type2);
        assertNotEquals(type2, type3);
        assertNotEquals(type3, type1);
    }

    @Test
    void shouldBindAccess() {
        final Rec type1 = TRec.of("name", TStr.of().label("a"), "age", TInt.of())
                .access(TInst.of("get", "persons").mult(TInst.of("is", TInst.of("get", "name"))).mult(TInst.of("eq", TStr.of().label("a"))));
        assertNotEquals(TInst.none(), type1.access());
        Rec rec1 = TRec.of("name", "marko", "age", 29);
        assertFalse(type1.constant());
        assertTrue(rec1.constant());
        final Bindings bindings = new Bindings();
        assertTrue(type1.match(bindings, rec1));
        assertEquals(TStr.of("marko"), bindings.get("a"));
        assertEquals(1, bindings.size());
        final Obj type2 = type1.bind(bindings);
        assertNotEquals(type1, type2);
        assertFalse(type2.constant());
        assertFalse(type1.access().constant());
        assertTrue(type2.access().constant());
        type1.match(bindings, rec1);
        assertEquals(type2, type1.bind(bindings));
        assertNotSame(type2, type1.bind(bindings));
    }


}
