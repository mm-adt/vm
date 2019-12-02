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
import org.mmadt.machine.object.model.atomic.Real;
import org.mmadt.util.ProcessArgs;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.machine.object.impl.___.gt;
import static org.mmadt.machine.object.impl.___.id;
import static org.mmadt.machine.object.impl.___.mult;
import static org.mmadt.machine.object.impl.___.plus;
import static org.mmadt.machine.object.impl.___.zero;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TRealTest implements TestUtilities {

    private final static ProcessArgs[] PROCESSING = new ProcessArgs[]{
            ProcessArgs.args(List.of(1.0f, 2.0f, 3.0f, 4.0f), TReal.of(1.0f, 2.0f, 3.0f, 4.0f)),
            ProcessArgs.args(List.of(1.1f), TReal.of(1.1f)),
            ProcessArgs.args(List.of(-1.1f), TReal.of(1.1f).neg()),
            ProcessArgs.args(List.of(0.0f), TReal.of(1.1f).zero()),
            ProcessArgs.args(List.of(0.0f), TReal.of(1.1f).mult(zero())),
            ProcessArgs.args(List.of(2.1f, 1.0f), TReal.of(1.1f).<Real>branch(id(), zero()).plus(1.0f)),
            ProcessArgs.args(List.of(4.2f), TReal.of(1.1f).<Real>branch(id(), zero()).plus(1.0f).is(gt(2.0f)).mult(2.0f)),
            // ProcessArgs.of(List.of(TReal.of(1.1f).q(2)), TReal.of(1.1f).mult(one()).q(2)), // TODO: quantifier needs to also be appended to access
            ProcessArgs.args(List.of(4.2f), TReal.of(1.0f).plus(plus(plus(1.2f)))),
            ProcessArgs.args(List.of(4.2f), TReal.of(1.0f).plus(plus(plus(1.2f))).mult(1.0f)),
            ProcessArgs.args(List.of(4.2f), TReal.of(1.0f).plus(plus(plus(1.2f))).mult(1.0f).is(gt(TReal.of(4.0f).plus(0.1f)))),
            ProcessArgs.args(List.of(5.2f), TReal.of(1.0f).plus(plus(plus(1.2f))).mult(1.0f).is(gt(TReal.of(4.0f).plus(0.1f))).plus(1.0f)),
            ProcessArgs.args(List.of(), TReal.of(1.0f).plus(plus(plus(1.2f))).mult(1.0f).is(gt(TReal.of(4.0f).plus(0.1f).plus(0.1f)))),
            ProcessArgs.args(List.of(false), TReal.of(1.0f).plus(1.2f).gt(plus(0.1f))),
            ProcessArgs.args(List.of(false), TReal.of(1.0f).plus(1.2f).gt(plus(0.1f)).plus(false)),
            ProcessArgs.args(List.of(true), TReal.of(1.0f).plus(1.2f).gt(plus(0.1f)).plus(true)),
    };

    @TestFactory
    Stream<DynamicTest> testProcessing() {
        return Stream.of(PROCESSING).map(obj -> DynamicTest.dynamicTest(obj.input.toString(), () -> assertEquals(obj.expected, submit(obj.input))));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final static Real[] INSTANCES = new Real[]{
            TReal.of(23.4f),
            TReal.of(41.3f).q(2),
            TReal.of().zero()
    };

    @TestFactory
    Stream<DynamicTest> testInstances() {
        return Stream.of(INSTANCES).map(obj -> DynamicTest.dynamicTest(obj.toString(), () -> assertTrue(obj.isInstance())));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final static Real[] TYPES = new Real[]{
            TReal.of(),
            TReal.of().q(1, 45),
            TReal.of().q(1, 45)
    };

    @TestFactory
    Stream<DynamicTest> testTypes() {
        return Stream.of(TYPES).map(obj -> DynamicTest.dynamicTest(obj.toString(), () -> assertTrue(obj.isType())));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final static Real[] REFERENCES = new Real[]{
            TReal.of(1.46f, 13.02f).plus(TReal.of(2.0f)).div(TReal.of(1.4f)),
            TReal.of(23.0f, 56.0f, 11.0f),
            TReal.of(23.0f).plus(mult(2.0f))
    };

    @TestFactory
    Stream<DynamicTest> testReferences() {
        return Stream.of(REFERENCES).map(obj -> DynamicTest.dynamicTest(obj.toString(), () -> assertTrue(obj.isReference())));
    }


    @Test
    void testType() {
        validateTypes(TReal.of());
    }

    @Test
    void testIsA() {
        validateIsA(TReal.of());
    }


}
