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

package org.mmadt.language.compiler;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.language.Query;
import org.mmadt.machine.object.impl.TModel;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.util.BytecodeHelper;
import org.mmadt.util.TestArgs;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mmadt.language.__.eq;
import static org.mmadt.language.__.gt;
import static org.mmadt.language.__.id;
import static org.mmadt.language.__.map;
import static org.mmadt.language.__.plus;
import static org.mmadt.language.__.start;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TypeTest {

    private static final Obj NONE = TObj.none();

    private final static TestArgs[] TEST_PARAMETERS = new TestArgs[]{
            new TestArgs<>(List.of(
                    NONE, NONE),
                    start()),
            new TestArgs<>(List.of(
                    NONE, TInt.of(1)),
                    start(1)),
            new TestArgs<>(List.of(
                    NONE, TInt.some(2)),
                    start(1, 2)),
            new TestArgs<>(List.of(
                    NONE, TInt.of(6)),
                    start(1, 2, 3).plus(1).q(2).q()),
            new TestArgs<>(List.of(
                    NONE, TInt.some(2), TInt.some(2)),
                    TInt.of(1, 2).plus(TInt.of(7))),
           // new TestArgs<>(List.of(
           //         NONE, /*TInt.some(2),*/ TInt.zeroInt().q(2)),
           //         TInt.of(1, 2).plus(TInt.of(3)).zero()),
           // new TestArgs<>(List.of(
           //         NONE, /*TInt.some(2),*/ TInt.oneInt().q(2)),
           //         TInt.of(1, 2).one()),
            new TestArgs<>(List.of(
                    NONE, TInt.some(2), TInt.some(2), TInt.some(1, 2)),
                    start(1, 2).plus(TInt.of(7)).dedup()),
            new TestArgs<>(List.of(
                    NONE, TInt.some(2), TInt.some(2), TInt.some(1, 2), TInt.some()),
                    start(1, 2).plus(7).dedup().count()),
            new TestArgs<>(List.of(
                    NONE, TInt.some(2), TInt.some(2), TInt.some(1, 2), TInt.some(), TInt.some()),
                    start(1, 2).plus(7).dedup().count().mult(5)),
            new TestArgs<>(List.of(
                    NONE, TInt.some(2), TInt.some(2), TInt.some(1, 2), TInt.some(), TInt.some(10)),
                    start(1, 2).plus(7).dedup().count().mult(5).q(10)),
            //new TestArgs<>(List.of( // TODO: 0 quantifier handling
            //        NONE, TInt.zeroInt()),
            //        start(1, 2).plus(7).dedup().count().mult(5).q(0)),
            new TestArgs<>(List.of(
                    NONE, /*TInt.some(2), TInt.some(2), TInt.some(1, 2), TInt.some(),*/ TInt.of(1)),
                    start(1, 2).plus(7).dedup().count().count()),
            new TestArgs<>(List.of(
                    NONE, TInt.some(4), TInt.some(4), TInt.some(1, 4), TInt.some(), TInt.some()),
                    start(1, 2, 3, 4).plus(7).dedup().count().sum()),
            new TestArgs<>(List.of(
                    NONE, TInt.some(4), TInt.some(8), TBool.some(8)),
                    start(1, 2, 3, 4).plus(7).q(2).gt(5)),
            new TestArgs<>(List.of(
                    NONE, TInt.some(3), TInt.some(3), TBool.some(3)),
                    TInt.of(1, 2, 3).plus(TInt.of(7)).gt(TInt.of(5)).id()),
            // TODO: if you know that the the filter is always going to return true, then its an identity (not a filter) and thus, quantifier isn't lowsided to zero.
            new TestArgs<>(List.of(
                    NONE, TBool.of(true).q(7), List.of(List.of(NONE, TBool.of(true))), TBool.of(true).q(0, 7), TBool.of(true).q(0, 7)),
                    start(TBool.of(true).q(7)).is(id()).is(true)),
            ///// NESTED BYTECODE
            new TestArgs<>(List.of(
                    NONE, TInt.some(4), TInt.some(4), TInt.some(1, 4), List.of(List.of(TInt.some(), TBool.some())), TInt.some(0, 4)),
                    start(1, 2, 3, 4).plus(7).dedup().is(gt(5))),
            new TestArgs<>(List.of(
                    NONE, TInt.some(4), List.of(List.of(TInt.some(), TInt.some(), TBool.some())), TBool.some(4), List.of(List.of(TBool.some(), TBool.some())), TBool.some(0, 4)),
                    start(1, 2, 3, 4).map(plus(3).gt(2)).is(id())),
            // TODO: if we know the EQ is a constant, then we know its constant{0,4}
            new TestArgs<>(List.of(
                    NONE, TInt.some(4), List.of(List.of(TInt.some(), List.of(List.of(TInt.some(), TInt.some(), TBool.some())), TBool.some())), TBool.some(4), List.of(List.of(TBool.some(), TBool.some())), TBool.some(0, 4)),
                    start(1, 2, 3, 4).map(map(plus(3).gt(2))).is(eq(true).id())),
            new TestArgs<>(List.of(
                    NONE, TStr.some().q(4), List.of(List.of(TStr.some(), TStr.some())), TRec.of(TStr.some(), TInt.some())),
                    start("a", "b", "c", "d").groupCount(plus("c"))),


    };

    @TestFactory
    Stream<DynamicTest> testTypes() {
        return Stream.of(TEST_PARAMETERS)
                .map(tp -> DynamicTest.dynamicTest(tp.input.toString(), () -> {
                    assumeFalse(tp.ignore);
                    // System.out.println(tp.input.bytecode() + "\n=>" + tp.expected);
                    assertEquals(tp.expected, BytecodeHelper.domainRangeNested(Rewriting.rewrite(TModel.of("ex"), tp.input instanceof Query ? ((Query) tp.input).bytecode() : ((Obj) tp.input).access())));
                }));
    }
}
