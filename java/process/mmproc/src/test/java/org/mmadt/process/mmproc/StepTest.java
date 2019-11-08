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

package org.mmadt.process.mmproc;

import org.junit.jupiter.api.Test;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TQ;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.processor.Processor;
import org.mmadt.util.IteratorUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.language.__.eq;
import static org.mmadt.language.__.id;
import static org.mmadt.language.__.is;
import static org.mmadt.language.__.minus;
import static org.mmadt.language.__.one;
import static org.mmadt.language.__.plus;
import static org.mmadt.language.__.start;
import static org.mmadt.language.__.zero;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class StepTest {

    @Test
    void testDrop() {
        final Processor<Int, Int> processor = new ProcProcessor(Map.of()).mint(start(TRec.of("a", 1, "b", 2)).put("c", 3).drop("b").bytecode());
        assertEquals(List.of(TRec.of("a", 1, "c", 3)), IteratorUtils.list(processor.iterator(TInt.none())));
    }

    @Test
    void testGroupCount() {
        final Processor<Int, Int> processor = new ProcProcessor(Map.of()).mint(start(0, 0, 2).plus(1).mult(1).plus(0).groupCount(plus(2).plus(-3).plus(3)).bytecode());
        assertEquals(List.of(TRec.of(3, new TQ<>(TInt.of(2)), 5, new TQ<>(TInt.of(1)))), IteratorUtils.list(processor.iterator(TInt.none())));
    }

    //@Test
    void testMinus() {
        final Processor<Int, Int> processor = new ProcProcessor(Map.of()).mint(start(1, 2, 3).minus(2).branch(is(eq(-1)), plus(1), is(eq(0)), id(), is(eq(1)), minus(1)).plus(1).bytecode());
        assertEquals(List.of(TInt.of(1), TInt.of(1), TInt.of(1)), IteratorUtils.list(processor.iterator(TInt.none())));
    }

    @Test
    void testOne() {
        final Processor<Int, Int> processor = new ProcProcessor(Map.of()).mint(start(0, 1, 2).mult(one()).plus(one()).bytecode());
        assertEquals(List.of(TInt.of(1), TInt.of(2), TInt.of(3)), IteratorUtils.list(processor.iterator(TInt.none())));
    }

    @Test
    void testPlus() {
        final Processor<Int, Int> processor = new ProcProcessor(Map.of()).mint(start(0, 1, 2).plus(2).plus(-1).plus(1).bytecode());
        assertEquals(List.of(TInt.of(2), TInt.of(3), TInt.of(4)), IteratorUtils.list(processor.iterator(TInt.none())));
    }

    @Test
    void testZero() {
        final Processor<Int, Int> processor = new ProcProcessor(Map.of()).mint(start(0, 1, 2).plus(zero()).mult(zero()).bytecode());
        assertEquals(List.of(TInt.of(0), TInt.of(0), TInt.of(0)), IteratorUtils.list(processor.iterator(TInt.none())));
    }

}
