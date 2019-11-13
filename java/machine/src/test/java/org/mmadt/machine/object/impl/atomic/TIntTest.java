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

package org.mmadt.machine.object.impl.atomic;

import org.junit.jupiter.api.Test;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.composite.inst.sideeffect.ExplainInst;
import org.mmadt.machine.object.impl.util.TestHelper;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.util.IteratorUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.language.__.eq;
import static org.mmadt.language.__.gt;
import static org.mmadt.language.__.id;
import static org.mmadt.language.__.mult;
import static org.mmadt.language.__.start;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TIntTest {

    @Test
    void xxx() {
        // System.out.println(ExplainInst.create(TInt.some().access(start(3,5,6).plus(2).map(start(2,4,7).is(gt(6)).bytecode()))));
        // System.out.println(IteratorUtils.list(TInt.some().append(ExplainInst.create(TInt.some().access(start(3,5,6).plus(2).map(plus(4).plus(mult(1)).count()).count().sum().count()))).iterable()));
        // System.out.println(IteratorUtils.list(TInt.some().append(ExplainInst.create(TStr.some().access(start("a").branch(id(), id(), id(), id()).plus(id())))).iterable()));
        // start(1, 2).plus(7).dedup().count().obj().iterable().forEach(System.out::println);

        //start(TInt.some(5)).obj().iterable().forEach(System.out::println);
        //start(TInt.some(5)).plus(3).obj().iterable().forEach(System.out::println);
        //start(TInt.some(5)).plus(3).gt(50).obj().iterable().forEach(System.out::println);
        start(TInt.some(5)).plus(5).mult(5).is(gt(5)).plus(100).is(mult(45).minus(10).gt(0)).obj().iterable().forEach(System.out::println);
    }

    @Test
    void testInstanceReferenceType() {
        Int instance = TInt.of(23);
        Int reference = TInt.of(1, 2).plus(TInt.of(2)).minus(TInt.of(7));
        Int type = TInt.some();
        TestHelper.validateKinds(instance, reference, type);
        //////
        instance = TInt.of(4).q(2);
        reference = TInt.of(23, 56, 11);
        type = TInt.of().q(45);
        TestHelper.validateKinds(instance, reference, type);
    }

    @Test
    void shouldTest() {
        assertTrue(TInt.some().test(TInt.of(32)));
        assertFalse(TInt.some().test(TReal.of(43.0f)));
        assertTrue(TObj.all().test(TInt.of(-1)));
        assertNotEquals(TInt.some(), TBool.some());
        assertNotEquals(TInt.some(), TStr.some());
    }

    /*@Test
    void shouldMonoid() {
        final Obj x = start(TInt.of(1), 2, 3).is(gt(2)).mult(plus(34)).is(or(gt(1), gt(110))).obj();
        System.out.println(x);
        x.iterable().forEach(System.out::println);
    }

    @Test
    void shouldMonoid2() {
        Int t = TInt.of(1, 2, 3).inst(TInst.of(Tokens.IS), SumInst.create()).inst(TInst.of(Tokens.MULT, TInt.some()), PlusInst.create(23));
        final Obj x = start(t).is(gt(2)).mult(plus(34)).is(or(gt(1), gt(110), lt(10000))).obj();
        System.out.println(x);
        x.iterable().forEach(System.out::println);
    }*/
}
