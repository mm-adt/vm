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

package org.mmadt.machine.object.impl.atomic;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.TestUtilities;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.util.ProcessArgs;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.machine.object.impl.__.a;
import static org.mmadt.machine.object.impl.__.eq;
import static org.mmadt.machine.object.impl.__.gt;
import static org.mmadt.machine.object.impl.__.gte;
import static org.mmadt.machine.object.impl.__.is;
import static org.mmadt.machine.object.impl.__.lt;
import static org.mmadt.machine.object.impl.__.lte;
import static org.mmadt.machine.object.impl.__.mult;
import static org.mmadt.machine.object.impl.__.plus;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.qmark;
import static org.mmadt.util.ProcessArgs.args;
import static org.mmadt.util.TestArgs.ints;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TIntTest implements TestUtilities {

    private final static ProcessArgs[] PROCESSING = new ProcessArgs[]{
            // instances
            args(List.of(1), TInt.of(1)),
            args(List.of(-1), TInt.of(1).neg()),
            args(List.of(0), TInt.of(1).zero()),
            args(List.of(1), TInt.of().one()),
            args(List.of(20), TInt.of(2).mult(10)),
            args(List.of(1), TInt.of(1).sum()),
            args(List.of(10), TInt.of(1, 2, 3, 4).sum()),
            args(List.of(4), TInt.of(1).plus(plus(plus(1)))),
            args(List.of(50), TInt.of(1).plus(4).mult(10)),
            args(List.of(50), TInt.of(1).plus(4).mult(10).is(gt(plus(-50)))),
            args(List.of(true), TInt.of(1).plus(4).mult(10).gt(plus(-50))),
            args(List.of(true), TInt.of(1).plus(4).mult(10).gt(plus(plus(-60)))),
            args(List.of(true), TInt.of(1).plus(4).mult(10).gt(plus(plus(-60))).is(true)),
            args(List.of(), TInt.of(1).plus(4).mult(10).gt(plus(plus(-60))).is(eq(false))),
            // args(List.of(), TInt.of(1).plus(4).mult(10).gt(plus(plus(-60))).is(false)),
            // references
            args(List.of(50, 51), TInt.of(49, 50).is(gt(plus(-1))).plus(1)),
            args(List.of(49, 50), TInt.of(49, 50).is(gt(plus(-1)))),
            args(List.of(49, 50), TInt.of(49, 50).is(gte(plus(-1)))),
            args(List.of(), TInt.of(49, 50).is(lt(plus(-1)))),
            // args(List.of(), TInt.of(49, 50).is(lt(plus(-1))).map(32)),
            args(List.of(49, 50), TInt.of(49, 50).is(lt(plus(1)))),
            //args(List.of(10, 10), TInt.of(49, 50).is(lt(plus(1))).map(1).plus(plus(8))),
            //       args(List.of(10, 10), TInt.of(49, 50).is(lt(plus(1))).map(TInt.of(0, 1, 2).plus(1).plus(0)).plus(plus(8))),
            args(List.of(49, 50), TInt.of(49, 50).is(lte(plus(1)))),
            args(List.of(), TInt.of(49, 50).is(gt(plus(1)))),
            // type
            args(List.of(), TInt.none().plus(TInt.of())),
            args(List.of(TInt.of().plus(TInt.of())), TInt.of().plus(TInt.of())),
            args(List.of(TInt.of().plus(mult(3))), TInt.of().plus(TInt.of().mult(3))),
            args(List.of(ints(10).q(qmark)), TInt.of().plus(mult(3)).is(gt(45)).map(10)), // TODO: instance requires stable quantification
            args(List.of(ints(10)), TInt.of().plus(mult(3)).map(10)),
            // predicate type
            args(List.of(), TInt.of(20).is(a(TInt.of(is(gt(50)))))),
            args(List.of(20), TInt.of(20).as(TInt.of(is(lt(50))))),

            // state
            args(List.of(TInt.of(1).symbol("age").binding("a")), TInt.of(1).as(TInt.of().symbol("age").binding("a"))),
    };


    @TestFactory
    Stream<DynamicTest> testProcessing() {
        return Stream.of(PROCESSING).map(obj -> DynamicTest.dynamicTest(obj.input.toString(), () -> assertEquals(obj.expected, submit(obj.input))));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final static Int[] INSTANCES = new Int[]{
            TInt.of(2),
            TInt.of(55),
            TInt.of(0)
    };

    @TestFactory
    Stream<DynamicTest> testInstances() {
        return Stream.of(INSTANCES).map(obj -> DynamicTest.dynamicTest(obj.toString(), () -> assertTrue(obj.isInstance())));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final static Int[] TYPES = new Int[]{
            TInt.of(),
            TInt.of().q(45),
            TInt.of().q(0),
            TInt.of(is(lt(50))).q(2)

    };

    @TestFactory
    Stream<DynamicTest> testTypes() {
        return Stream.of(TYPES).map(obj -> DynamicTest.dynamicTest(obj.toString(), () -> assertTrue(obj.isType())));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final static Int[] REFERENCES = new Int[]{
            TInt.of(1, 2).plus(TInt.of(2)).minus(TInt.of(7)),
            TInt.of(23, 56, 11),
    };

    @TestFactory
    Stream<DynamicTest> testReferences() {
        return Stream.of(REFERENCES).map(obj -> DynamicTest.dynamicTest(obj.toString(), () -> assertTrue(obj.isReference())));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    void testType() {
        validateTypes(TInt.of());
    }

    @Test
    void testIsA() {
        validateIsA(TInt.of());
    }
}
