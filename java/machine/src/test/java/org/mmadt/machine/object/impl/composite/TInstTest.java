/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.machine.object.impl.composite;

import org.junit.jupiter.api.Test;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.model.composite.Inst;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.language.compiler.Tokens.COUNT;
import static org.mmadt.language.compiler.Tokens.PLUS;
import static org.mmadt.language.compiler.Tokens.START;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TInstTest {

    @Test
    void testInstanceReferenceType() {
        final Inst instance = TInst.of(PLUS, 52);
        final Inst reference = TInst.of(List.of(TInst.of(START, 3, 5), TInst.of(PLUS, 20), TInst.of(COUNT)));
        final Inst type = TInst.of(PLUS, TInt.of());

        assertTrue(instance.isInstance());
        assertFalse(instance.isReference());
        assertFalse(instance.isType());

        // assertFalse(reference.isInstance()); // TODO: insts are not being pushed off to <=[=]
        // assertTrue(reference.isReference());
        assertFalse(reference.isType());

        assertFalse(type.isInstance());
        assertFalse(type.isReference());
        assertTrue(type.isType());
    }

    @Test
    void shouldTest() {
        assertTrue(TInst.none().test(TInst.of(List.of())));
        assertFalse(TInst.none().test(TInst.of("get", "outV")));
        assertTrue(TInst.all().test(TInst.of("get", "outV")));
        assertTrue(TInst.all().mult(TInst.all()).test(TInst.of("get", "outV").mult(TInst.none())));
        assertTrue(TInst.some().test(TInst.of("get", "outV")));
        assertFalse(TInst.some().test(TInst.of("get", "outV").mult(TInst.of("get", "name"))));
        assertTrue(TInst.some().mult(TInst.some()).test(TInst.of("get", "outV").mult(TInst.of("get", "name"))));
        assertTrue(TInst.some().mult(TInst.of("get", TStr.of())).test(TInst.of("get", "outV").mult(TInst.of("get", "name"))));
    }

    /*@Test
    void testInstructionComposition() {
        final Inst a = TInst.of("db");
        final Inst b = is(get("name").eq("marko")).bytecode();
        assertEquals(a.q().one(), a.q());
        assertEquals(b.q().one(), b.q());
//        assertEquals(Q.one, b.get(TInt.oneInt()).quantifier());
        //
        final Inst c = a.mult(b);
//        assertEquals(Q.one, c.quantifier());
        assertEquals(a, c.peek());
        //
        final Inst d = TInst.of("get", "age");
        final Inst e = c.mult(d);
        assertTrue(e.get() instanceof TStream);
        for (final TInst inst : e.<TStream<TInst>>get()) {
            assertEquals(inst.q().one(), inst.q());
            if (inst.opcode().get().equals("is"))
                assertTrue(inst.get(TInt.oneInt()).q().isOne());
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
    }*/

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
        assertEquals(c, c.peek());
        assertEquals(a, c.get(TInt.oneInt()));
        assertEquals(b, c.get(TInt.twoInt()));
        final Inst d = TInst.of("count");
        final Inst e = a.plus(b).plus(d);
        //       assertEquals(Q.qone, e.quantifier());
        assertEquals(e, e.peek());


        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
        System.out.println(e);


    }
}
