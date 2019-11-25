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
import static org.mmadt.machine.object.impl.___.plus;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TRealTest implements TestUtilities {

    private final static ProcessArgs[] TEST_PARAMETERS = new ProcessArgs[]{
            ProcessArgs.of(List.of(1.0f, 2.0f, 3.0f, 4.0f), TReal.of(1.0f, 2.0f, 3.0f, 4.0f)),
            ProcessArgs.of(List.of(1.1f), TReal.of(1.1f)),
            ProcessArgs.of(List.of(TReal.of(1.1f).q(2)), TReal.of(1.1f).mult(1.0f).q(2)),
            ProcessArgs.of(List.of(4.2f), TReal.of(1.0f).plus(plus(plus(1.2f)))),
            ProcessArgs.of(List.of(4.2f), TReal.of(1.0f).plus(plus(plus(1.2f))).mult(1.0f)),
            ProcessArgs.of(List.of(5.2f), TReal.of(1.0f).plus(plus(plus(1.2f))).mult(1.0f).is(gt(TReal.of(4.0f).plus(0.1f))).plus(1.0f)),
            ProcessArgs.of(List.of(), TReal.of(1.0f).plus(plus(plus(1.2f))).mult(1.0f).is(gt(TReal.of(4.0f).plus(0.1f).plus(0.1f)))),
            ProcessArgs.of(List.of(false), TReal.of(1.0f).plus(1.2f).gt(plus(0.1f))),
            ProcessArgs.of(List.of(false), TReal.of(1.0f).plus(1.2f).gt(plus(0.1f)).plus(false)),
            ProcessArgs.of(List.of(true), TReal.of(1.0f).plus(1.2f).gt(plus(0.1f)).plus(true)),
    };

    @TestFactory
    Stream<DynamicTest> testTypes() {
        return Stream.of(TEST_PARAMETERS).map(tp -> DynamicTest.dynamicTest(tp.input.toString(), () -> assertEquals(tp.expected, submit(tp.input))));
    }

    @Test
    void testInstanceReferenceType() {
        validateKinds(TReal.of(23.4f), TReal.of(1.46f, 13.02f).plus(TReal.of(2.0f)).div(TReal.of(1.4f)), TReal.some());
        validateKinds(TReal.of(41.3f).q(2), TReal.of(23.0f, 56.0f, 11.0f), TReal.of().q(1, 45));
    }

    @Test
    void testType() {
        validateTypes(TReal.some());
    }

    @Test
    void testIsA() {
        validateIsA(TReal.some());
    }


}
