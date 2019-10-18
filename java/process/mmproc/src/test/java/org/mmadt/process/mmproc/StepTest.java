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
import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.impl.composite.TRec;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.atomic.Int;
import org.mmadt.object.model.composite.Inst;
import org.mmadt.processor.Processor;
import org.mmadt.processor.compiler.IR;
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
        final Inst bytecode = start(TRec.of("a", 1, "b", 2)).put("c", 3).drop("b").bytecode();
        final IR<Int, Int> ir = print(new IR<>(bytecode));
        final Processor<Int, Int> processor = new ProcProcessor(Map.of()).mint(ir);
        assertEquals(List.of(TRec.of("a", 1, "c", 3)), IteratorUtils.list(processor.iterator(TInt.none())));
    }

    @Test
    void testGroupCount() {
        final Inst bytecode = start(0, 0, 2).plus(1).mult(1).plus(0).groupCount(plus(2).plus(-3).plus(3)).bytecode();
        final IR<Int, Int> ir = print(new IR<>(bytecode));
        final Processor<Int, Int> processor = new ProcProcessor(Map.of()).mint(ir);
        assertEquals(List.of(TRec.of(3, 2, 5, 1)), IteratorUtils.list(processor.iterator(TInt.none())));
    }

    @Test
    void testMinus() {
        final Inst bytecode = start(1, 2, 3).minus(2).branch(is(eq(-1)), plus(1), is(eq(0)), id(), is(eq(1)), minus(1)).plus(1).bytecode();
        final IR<Int, Int> ir = print(new IR<>(bytecode));
        final Processor<Int, Int> processor = new ProcProcessor(Map.of()).mint(ir);
        assertEquals(List.of(TInt.of(1), TInt.of(1), TInt.of(1)), IteratorUtils.list(processor.iterator(TInt.none())));
    }

    @Test
    void testOne() {
        final Inst bytecode = start(0, 1, 2).mult(one()).plus(one()).bytecode();
        final IR<Int, Int> ir = print(new IR<>(bytecode));
        final Processor<Int, Int> processor = new ProcProcessor(Map.of()).mint(ir);
        assertEquals(List.of(TInt.of(1), TInt.of(2), TInt.of(3)), IteratorUtils.list(processor.iterator(TInt.none())));
    }

    @Test
    void testPlus() {
        final Inst bytecode = start(0, 1, 2).plus(2).plus(-1).plus(1).bytecode();
        final IR<Int, Int> ir = print(new IR<>(bytecode));
        final Processor<Int, Int> processor = new ProcProcessor(Map.of()).mint(ir);
        assertEquals(List.of(TInt.of(2), TInt.of(3), TInt.of(4)), IteratorUtils.list(processor.iterator(TInt.none())));
    }

    @Test
    void testZero() {
        final Inst bytecode = start(0, 1, 2).plus(zero()).mult(zero()).bytecode();
        final IR<Int, Int> ir = print(new IR<>(bytecode));
        final Processor<Int, Int> processor = new ProcProcessor(Map.of()).mint(ir);
        assertEquals(List.of(TInt.of(0), TInt.of(0), TInt.of(0)), IteratorUtils.list(processor.iterator(TInt.none())));
    }

    private static <S extends Obj, E extends Obj> IR<S, E> print(final IR<S, E> ir) {
        if (true) { // TODO: Ultimately, all of this needs to be put in a general test-suite package
            System.out.println(ir.bytecode());
            System.out.println(ir.functions() + "\n");
        }
        return ir;
    }

}
