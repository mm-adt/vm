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

package org.mmadt.language.compiler;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.TestUtilities;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.util.InstHelper;
import org.mmadt.util.TestArgs;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.machine.object.model.composite.Q.Tag.qmark;
import static org.mmadt.util.TestArgs.args;
import static org.mmadt.util.TestArgs.bools;
import static org.mmadt.util.TestArgs.ints;
import static org.mmadt.util.TestArgs.none;
import static org.mmadt.util.TestArgs.objs;
import static org.mmadt.util.TestArgs.process;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TypeTest implements TestUtilities {

    private final static TestArgs[] TEST_PARAMETERS = new TestArgs[]{
            args(objs(none, none), none),
            args(objs(ints(), ints()), ints()),
            args(objs(1, 1), ints(1)),
            args(objs(2, 2), ints(2)),
            args(objs(3, 3), ints(3)),
            args(objs(none, ints().q(2)), ints(1, 2)),
            args(objs(none, ints().q(3)), ints(1, 2, 3)),
            args(objs(none, ints().q(2), ints().q(2)), ints(1, 2).plus(3)),
            args(objs(0, 0), ints(1).plus(3).zero()),
            args(objs(ints(0).q(2), ints(0).q(2)), ints(1, 2).plus(3).zero()),
            args(objs(ints(), ints(), objs(objs(ints(), ints(), bools())), ints().q(qmark), bools().q(qmark)), process(ints().plus(1).is(ints().mult(2).gt(3)).lt(4))),

            /*args(objs(none, ints().q(2), ints().q(3, 4), ints(0).q(3, 4)), ints(1, 2).<Int>q(3, 4).plus(5).zero()),

            new TestArgs<>(List.of(
                    none, TInt.of().q(3), TInt.of().q(6), new TQ<>(TInt.of(6))),
                    TInt.of(1, 2, 3).q(2).q()),
            new TestArgs<>(List.of(
                    none, TInt.of().q(2), TInt.of().q(2)),
                    TInt.of(1, 2).plus(7)),
            new TestArgs<>(List.of(
                    none, TInt.of().q(2), TInt.of().q(2), TInt.zeroInt().q(2)),
                    TInt.of(1, 2).plus(3).zero()),
            new TestArgs<>(List.of(
                    none, TInt.of().q(2), TInt.oneInt().q(2)),
                    TInt.of(1, 2).one()),
            new TestArgs<>(List.of(
                    none, TInt.of().q(2), TInt.of().q(2), TInt.of().q(1, 2)),
                    TInt.of(1, 2).plus(TInt.of(7)).dedup()),
            new TestArgs<>(List.of(
                    none, TInt.of().q(2), TInt.of().q(2), TInt.of().q(10, 20)),
                    TInt.of(1, 2).plus(TInt.of(7)).dedup().q(10)),
            new TestArgs<>(List.of(
                    none, TInt.of().q(2), TInt.of().q(2), TInt.of().q(1, 2), TInt.of()),
                    TInt.of(1, 2).plus(7).dedup().count()),
            new TestArgs<>(List.of(
                    none, TInt.of().q(2), TInt.of().q(2), TInt.of().q(1, 2), TInt.of(), TInt.of()),
                    start(1, 2).plus(7).dedup().count().mult(5)),
            new TestArgs<>(List.of(
                    none, TInt.of().q(2), TInt.of().q(2), TInt.of().q(1, 2), TInt.of(), TInt.of().q(10)),
                    start(1, 2).plus(7).dedup().count().mult(5).q(10)),
            new TestArgs<>(List.of( // TODO: 0 quantifier handling
                    none, TInt.of().q(2), TInt.of().q(2), TInt.of().q(1, 2), TInt.of(), TInt.none()), // TOOD: just drop the whole pipeline to none
                    start(1, 2).plus(7).dedup().count().mult(5).q(0)),
            new TestArgs<>(List.of(
                    none, TInt.of().q(2), TInt.of().q(2), TInt.of().q(1, 2), TInt.of(), TInt.of(1)),
                    start(1, 2).plus(7).dedup().count().count()),
            new TestArgs<>(List.of(
                    none, TInt.of().q(4), TInt.of().q(4), TInt.of().q(1, 4), TInt.of(), TInt.of()),
                    start(1, 2, 3, 4).plus(7).dedup().count().sum()),
            new TestArgs<>(List.of(
                    none, TInt.of().q(4), TInt.of().q(8), TBool.of().q(8)),
                    start(1, 2, 3, 4).plus(7).q(2).gt(5)),
            new TestArgs<>(List.of(
                    none, TInt.of().q(3), TInt.of().q(3), TBool.of().q(3)),
                    TInt.of(1, 2, 3).plus(7).gt(5).id()),
            new TestArgs<>(List.of(
                    none, TBool.of(true).q(7), List.of(List.of(TBool.of(true), TBool.of(true))), TBool.of(true).q(0, 7), TBool.of(true).q(0, 7)),
                    start(TBool.of(true).q(7)).is(id()).is(true)),
            new TestArgs<>(List.of(
                    none, TInt.of().q(4), TInt.of().q(4), TInt.of().q(1, 4), List.of(List.of(TInt.of(), TBool.of())), TInt.of().q(0, 4)),
                    start(1, 2, 3, 4).plus(7).dedup().is(gt(5))),
            new TestArgs<>(List.of(
                    none, TInt.of().q(4), List.of(List.of(TInt.of(), TInt.of(), TBool.of())), TBool.of().q(4), List.of(List.of(TBool.of(), TBool.of())), TBool.of().q(0, 4)),
                    TInt.of(1, 2, 3, 4).map(TInt.of().plus(3).gt(2)).is(___.plus(true))),
            new TestArgs<>(List.of(
                    none, TInt.of().q(4), List.of(List.of(TInt.of(), TInt.of(), TBool.of())), TBool.of().q(4), List.of(List.of(TBool.of(), TBool.of())), TBool.of().q(0, 28)),
                    start(1, 2, 3, 4).map(plus(3).gt(2)).is(id()).q(7)),
            new TestArgs<>(List.of(
                    none, TInt.of().q(4), List.of(List.of(TInt.of(), List.of(List.of(TInt.of(), TInt.of(), TBool.of())), TBool.of())), TBool.of().q(4), List.of(List.of(TBool.of(), TBool.of())), TBool.of().q(0, 4)),
                    null),//TInt.of(1, 2, 3, 4).map(TBool.of().map(TInt.of().plus(3).gt(2))).is(eq(true).id())),
            new TestArgs<>(List.of(
                    none, TInt.of().q(4), List.of(List.of(TStr.of(), TStr.of())), TRec.of(TStr.of(), TInt.of())),
                    start("a", "b", "c", "d").groupCount(plus("c"))),
            new TestArgs<>(List.of(
                    none, TInt.of().q(4), List.of(List.of(TStr.of().q(4), TStr.of().q(4)), List.of(TStr.of().q(4), TStr.of().q(4)), List.of(TStr.of().q(4), TStr.of().q(4)), List.of(TStr.of().q(4), TStr.of().q(4))), TStr.of().q(8), List.of(List.of(TStr.of(), TStr.of())), TStr.of().q(8)),
                    null),//TStr.of("a", "b", "c", "d").<Str>branch(ID(), ID(), ID(), ID()).plus(TStr.of())),*/
    };

    @TestFactory
    Stream<DynamicTest> testTypes() {
        return Stream.of(TEST_PARAMETERS).map(args -> DynamicTest.dynamicTest(args.input.toString(), () -> assertEquals(args.expected, InstHelper.domainRangeNested(((Obj) args.input).access()))));
    }
}
