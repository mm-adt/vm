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

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.TestUtilities;
import org.mmadt.util.ProcessArgs;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.machine.object.impl.___.gt;
import static org.mmadt.machine.object.impl.___.gte;
import static org.mmadt.machine.object.impl.___.lt;
import static org.mmadt.machine.object.impl.___.lte;
import static org.mmadt.machine.object.impl.___.plus;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TIntTest implements TestUtilities {

    private final static ProcessArgs[] TEST_PARAMETERS = new ProcessArgs[]{
            ProcessArgs.of(List.of(1), TInt.of(1)),
            // ProcessArgs.of(List.of(1), TInt.of(1).sum()),
            // ProcessArgs.of(List.of(10), TInt.of(1,2,3,4).sum()),
            ProcessArgs.of(List.of(4), TInt.of(1).plus(plus(plus(1)))),
            ProcessArgs.of(List.of(50), TInt.of(1).plus(4).mult(10)),
            ProcessArgs.of(List.of(50), TInt.of(1).plus(4).mult(10).is(gt(plus(-50)))),
            ProcessArgs.of(List.of(true), TInt.of(1).plus(4).mult(10).gt(plus(-50))),
            ProcessArgs.of(List.of(true), TInt.of(1).plus(4).mult(10).gt(plus(plus(-60)))),
            ProcessArgs.of(List.of(true), TInt.of(1).plus(4).mult(10).gt(plus(plus(-60))).is(true)),
            ProcessArgs.of(List.of(), TInt.of(1).plus(4).mult(10).gt(plus(plus(-60))).is(false)),
            ProcessArgs.of(List.of(50, 51), TInt.of(49, 50).is(gt(plus(-1))).plus(1)),
            ProcessArgs.of(List.of(49, 50), TInt.of(49, 50).is(gt(plus(-1)))),
            ProcessArgs.of(List.of(49, 50), TInt.of(49, 50).is(gte(plus(-1)))),
            ProcessArgs.of(List.of(), TInt.of(49, 50).is(lt(plus(-1)))),
            ProcessArgs.of(List.of(), TInt.of(49, 50).is(lt(plus(-1))).map(32)),
            ProcessArgs.of(List.of(49, 50), TInt.of(49, 50).is(lt(plus(1)))),
            ProcessArgs.of(List.of(10, 10), TInt.of(49, 50).is(lt(plus(1))).map(1).plus(plus(8))),
            ProcessArgs.of(List.of(10, 10), TInt.of(49, 50).is(lt(plus(1))).map(TInt.of(0, 1, 2).plus(1).plus(0)).plus(plus(8))), // TODO: dangerous as assuming order
            ProcessArgs.of(List.of(49, 50), TInt.of(49, 50).is(lte(plus(1)))),
            ProcessArgs.of(List.of(), TInt.of(49, 50).is(gt(plus(1))))
    };

    @TestFactory
    Stream<DynamicTest> testTypes() {
        return Stream.of(TEST_PARAMETERS).map(tp -> DynamicTest.dynamicTest(tp.input.toString(), () -> assertEquals(tp.expected, submit(tp.input))));
    }

    @Test
    void testInstanceReferenceType() {
        validateKinds(TInt.of(23), TInt.of(1, 2).plus(TInt.of(2)).minus(TInt.of(7)), TInt.some());
        validateKinds(TInt.of(4).q(2), TInt.of(23, 56, 11), TInt.of().q(45));
    }

    @Test
    void testType() {
        validateTypes(TInt.some());
    }

    @Test
    void testIsA() {
        validateIsA(TInt.some());
    }
}
