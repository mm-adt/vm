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
import org.mmadt.machine.object.impl.TStream;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.type.Bindings;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.language.__.get;
import static org.mmadt.language.__.is;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TInstTest {

    @Test
    void shouldTest() {
        assertTrue(TInst.none().test(TInst.of(List.of())));
        assertFalse(TInst.none().test(TInst.of("get", "outV")));
        assertTrue(TInst.all().test(TInst.of("get", "outV")));
        assertTrue(TInst.all().mult(TInst.all()).test(TInst.of("get", "outV").mult(TInst.none())));
        assertTrue(TInst.some().test(TInst.of("get", "outV")));
        assertFalse(TInst.some().test(TInst.of("get", "outV").mult(TInst.of("get", "name"))));
        assertTrue(TInst.some().mult(TInst.some()).test(TInst.of("get", "outV").mult(TInst.of("get", "name"))));
        assertTrue(TInst.some().mult(TInst.of("get", TStr.some())).test(TInst.of("get", "outV").mult(TInst.of("get", "name"))));
    }

    @Test
    void shouldBindInstructions() {
        final TRec<Str, Obj> person = TRec.of(
                "name", TStr.some(),
                "age", TInt.gt(0)).inst(TInst.of("get", "name"), TInst.of("get", TStr.some().as("x")));
        final Bindings bindings = new Bindings();
        bindings.put("x", TStr.of("alias"));
        final Optional<Inst> bc = person.inst(bindings, TInst.of("get", "name"));
        assertTrue(bc.isPresent());
        assertEquals(TInst.of("get", "alias"), bc.get());
        assertTrue(person.isType());
        assertFalse(person.isReference());
        assertFalse(person.isInstance());
    }

    @Test
    void shouldBindAccess() {
        final TRec<Str, Obj> person = TRec.of(
                "name", TStr.some().as("y"),
                "age", TInt.gt(0)).access(TInst.of("db").mult(TInst.of("is", TInst.of("get", "name").mult(TInst.of("eq", TStr.some().as("y"))))));
        final Bindings bindings = new Bindings();
        bindings.put("y", TStr.of("marko"));
        Rec<Str, Obj> marko = person.bind(bindings);
        assertEquals(TStr.of("marko"), marko.get(TStr.of("name")));
        assertEquals(TInt.gt(0), marko.get(TStr.of("age")));
        assertFalse(marko.isInstance());
        assertTrue(marko.isReference());
        assertFalse(marko.isType());
        assertEquals(marko.access(), TInst.of("db").mult(TInst.of("is", TInst.of("get", "name").mult(TInst.of("eq", TStr.of("marko"))))));
        assertNotEquals(person, marko);
        assertEquals(person, person);
        assertEquals(marko, marko);
    }

    @Test
    void shouldOrInstructionsCorrectly() {
        final Int gt10 = TInt.gt(10).inst(TInst.of("add", 1), TInst.of("add", 2));
        final Int lt50 = TInt.lt(50).inst(TInst.of("add", 3), TInst.of("add", 4));
        final Int gte100 = TInt.gte(100).inst(TInst.of("sub", 1), TInst.of("sub", 2));
        final Int combo = (TInt) gt10.and(lt50).or(gte100);
        final Int i9 = TInt.of(9);
        final Int i11 = TInt.of(11);
        //
        assertFalse(gt10.test(i9));
        assertTrue(lt50.test(i9));
        assertFalse(gte100.test(i9));
        assertFalse(combo.test(i9));
        assertThrows(RuntimeException.class, () -> i9.type(combo));
        //
        assertTrue(gt10.test(i11));
        assertTrue(lt50.test(i11));
        assertFalse(gte100.test(i11));
        assertTrue(combo.test(i11));
        i11.type(combo);
        assertTrue(combo.test(i11));
        assertFalse(i11.inst(new Bindings(), TInst.of("sub", 1)).isPresent());
        assertTrue(i11.inst(new Bindings(), TInst.of("add", 1)).isPresent());
        assertTrue(i11.inst(new Bindings(), TInst.of("add", 3)).isPresent());

    }

    @Test
    void testInstructionComposition() {
        final Inst a = TInst.of("db");
        final Inst b = is(get("name").eq("marko")).bytecode();
        assertEquals(a.q().one(), a.q());
        assertEquals(b.q().one(), b.q());
//        assertEquals(Q.one, b.get(TInt.oneInt()).quantifier());
        //
        final Inst c = a.mult(b);
//        assertEquals(Q.one, c.quantifier());
        assertEquals(a, c.peak());
        //
        final Inst d = TInst.of("get", "age");
        final Inst e = c.mult(d);
        assertTrue(e.get() instanceof TStream);
        for (final TInst inst : e.<TStream<TInst>>get()) {
            assertEquals(inst.q().one(), inst.q());
            if (inst.opcode().get().equals("is"))
                assertEquals(new TQ(2, 2), inst.get(TInt.oneInt()).q());
            else if (inst.opcode().get().equals("db"))
                assertEquals(inst.q().zero(), inst.get(TInt.oneInt()).q());
            else {
                assertEquals("get", inst.opcode().get());
                assertEquals(inst.q().one(), inst.get(TInt.oneInt()).q());
            }
            assertEquals(Tokens.INST, inst.symbol());
        }
        assertEquals(c, a.mult(b));
        assertEquals(e, c.mult(d));
        assertEquals(e, a.mult(b).mult(d));
        assertEquals(e, a.mult(b.mult(d)));
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
        System.out.println(e);
    }

    @Test
    void testInstructionBranching() {
        final Inst a = TInst.of("get", "outE");
        final Inst b = TInst.of("is", TInst.of("get", "name").mult(TInst.of("eq", "marko")));
        assertEquals(a.q().one(), a.q());
        assertEquals(b.q().one(), b.q());
//        assertEquals(Q.qone, b.get(TInt.oneInt()).quantifier());
        //
        final Inst c = a.plus(b);
        assertEquals(c.q().one(), c.q());
        assertEquals(c, c.peak());
        assertEquals(a, c.get(TInt.oneInt()));
        assertEquals(b, c.get(TInt.twoInt()));
        final Inst d = TInst.of("count");
        final Inst e = a.plus(b).plus(d);
        //       assertEquals(Q.qone, e.quantifier());
        assertEquals(e, e.peak());


        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
        System.out.println(e);


    }
}
