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

package org.mmadt.machine.object.impl.type.algebra;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.impl.composite.inst.map.MinusInst;
import org.mmadt.machine.object.impl.composite.inst.map.NegInst;
import org.mmadt.machine.object.impl.composite.inst.map.PlusInst;
import org.mmadt.machine.object.model.type.algebra.WithGroupPlus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.util.IteratorUtils.list;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class WithGroupPlusTest {

    private static final List<WithGroupPlus> TEST_ARGS = List.of(
            //////////////// INSTANCES
            TInst.of(Tokens.ID),
            TReal.of(1.0f),
            TInt.of(1),
            TBool.of(true),
            //////////////// REFERENCES
            TLst.of(TLst.of(1, "a", "x"), TLst.of(1, "c", 32), TLst.of(false, "abc", true)),
            TRec.of(TRec.of("a", 1, "b", 2), TRec.of("a", 2, "c", 3), TRec.of("e", 4)),
            // TInst.of(List.of(TInst.of(Tokens.START,TInt.of(1)), TInst.of(Tokens.NEG), TInst.of(Tokens.PLUS, TInt.of(32)))),
            TInt.of(1, 2, 3, 4, 5, 6),
            TReal.of(1.0f, 2.0f, 3.0f),
            TBool.of(true, false, false));

    static <A extends WithGroupPlus<A>> void validate(final A group) {
        WithMonoidPlusTest.validate(group);
        ///
        if (group.isInstance())
            testInstances(group);
        else if (group.isReference())
            testReferences(group);
        else
            throw new RuntimeException("Bad: " + group);
    }

    static <A extends WithGroupPlus<A>> void testInstances(final A group) {
        final A two = group.plus(group);
        final A three = group.plus(group).plus(group);
        final A four = group.plus(group).plus(group).plus(group);
        assertFalse(group.isZero());
        assertTrue(group.zero().isZero());
        assertNotEquals(group, group.zero());
        // x = x + 0
        assertEquals(group, group.plus(group.zero()));
        assertEquals(two, two.plus(group.zero()));
        // x = 0 + x
        assertEquals(group, group.zero().plus(group));
        assertEquals(two, group.zero().plus(two));
        // 0 = 0 + 0
        assertEquals(group.zero(), group.zero().plus(group.zero()));
        // (a+b)+c = a+(b+c)
        assertEquals(two.plus(three).plus(four), two.plus(three.plus(four)));
        // 0 = a - a
        assertEquals(group.zero(), group.minus(group));
        assertEquals(group.zero(), two.minus(two));
        // a = -(-a)
        assertEquals(group, group.neg().neg());
        assertEquals(two, two.neg().neg());
        assertEquals(three, three.neg().neg());
        // -a = 0 - a
        assertEquals(group.neg(), group.zero().minus(group));
        assertEquals(two.neg(), group.zero().minus(two));
        assertEquals(three.neg(), group.zero().minus(three));
    }

    static <A extends WithGroupPlus<A>> void testReferences(final A group) {
        A running = group;
        A second = group;
        for (int i = 0; i < 10; i++) {
//            assertEquals(ObjSet.create(running.access(running.access().mult(ZeroInst.create()).mult(NegInst.create()))), ObjSet.create(second.zero().neg()));
            assertEquals(running.access(running.access().mult(MinusInst.create(group))), second.minus(group));
            assertEquals(list(running.access(running.access().mult(PlusInst.create(group)).mult(NegInst.create())).iterable()), list(second.plus(group).neg().iterable()));
            assertEquals(running = running.access(running.access().mult(PlusInst.create(group.neg()))), second = second.plus(group.neg()));
        }
    }

    @TestFactory
    Stream<DynamicTest> testGroupPlus() {
        return TEST_ARGS.stream().map(algebra -> DynamicTest.dynamicTest(algebra.toString(), () -> validate(algebra)));
    }


}
