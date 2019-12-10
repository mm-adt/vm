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

package org.mmadt.language.mmlang;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.language.mmlang.jsr223.mmLangScriptEngine;
import org.mmadt.language.mmlang.util.ParserArgs;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.impl.ext.composite.TPair;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.util.IteratorUtils;

import javax.script.ScriptEngine;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.language.mmlang.util.ParserArgs.args;
import static org.mmadt.language.mmlang.util.ParserArgs.ints;
import static org.mmadt.language.mmlang.util.ParserArgs.objs;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ReduceTest {

    private final static ParserArgs[] REDUCING = new ParserArgs[]{
            // count
            args(ints(1),
                    "1 => [count]"),
            args(ints(3),
                    "1 => [[plus,1] + [plus,2] + [plus,3]][count]"),
            args(ints(1),
                    "1 => [[plus,1] + [plus,2] + [plus,3]][count][count]"),
            args(ints(1),
                    "1 => [[plus,1] + [plus,2] + [plus,3]][count][count][count]"),
            args(objs(1, 1, 1),
                    "1 => [[plus,1][count] + [plus,2][count] + [plus,3][count]]"),
            args(TPair.of(0, 5),
                    "int{5} => [is,[gt,10]][count]"),
            // sum
            args(ints(2),
                    "2 => [sum]"),
            args(ints(9),
                    "1 => [[plus,1] + [plus,2] + [plus,3]][sum]"),
            args(objs(2, 3, 4),
                    "1 => [[plus,1][sum] + [plus,2][sum] + [plus,3][sum]]"),
            args(objs(6),
                    "1 => [[plus,1][sum] + [plus,2][sum] + [plus,3][count]][sum]"),
            args(ints(3),
                    "1 => [[plus,1][sum] + [plus,2][sum] + [plus,3][sum]][count]"),
            args(ints(3).<Int>label("x"),
                    "1 => [[plus,1][sum] + [plus,2][sum] + [plus,3][sum]][count][sum]~x"),
            // groupCount
            args(TRec.of(1, 1),
                    "1 => [groupCount]"),
            args(ints(1),
                    "1 => [groupCount][count]"),
            args(TRec.of(1, 2, 2, 3, 3, 4),
                    "1 => [[id] + [id] + [plus,1]{2} + [plus,1] + [plus,2]{2} + [plus,2] + [plus,2]][groupCount]"),
            args(TRec.of(2, 2, 3, 3, 4, 4),
                    "1 => [[id] + [id] + [plus,1]{2} + [plus,1] + [plus,2]{2} + [plus,2] + [plus,2]][groupCount,[plus,1]]"),
            args(TRec.of(0, 9),
                    "1 => [[id] + [id] + [plus,1]{2} + [plus,1] + [plus,2]{2} + [plus,2] + [plus,2]][groupCount,[plus,2][zero]]"),
            args(TRec.of(0, 9),
                    "int => [[id] + [id] + [plus,1]{2} + [plus,1] + [plus,2]{2} + [plus,2] + [plus,2]][groupCount,[zero]]"),
            // args(TRec.of(), "obj{0} => [groupCount]")
    };


    @TestFactory
    Stream<DynamicTest> testReducing() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(REDUCING).map(query -> DynamicTest.dynamicTest(query.input, () -> assertEquals(query.expected, IteratorUtils.list((Iterator<Obj>) engine.eval(query.input)))));
    }
}
