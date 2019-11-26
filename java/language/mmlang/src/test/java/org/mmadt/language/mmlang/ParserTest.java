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

package org.mmadt.language.mmlang;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.language.mmlang.jsr223.mmLangScriptEngine;
import org.mmadt.language.mmlang.util.ParserArgs;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.inst.map.PlusInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.util.IteratorUtils;

import javax.script.ScriptEngine;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ParserTest {

    private final static ParserArgs[] PARSING = new ParserArgs[]{
            ParserArgs.of(List.of(11), "11"),
            ParserArgs.of(List.of(0), "11 => [zero]"),
            ParserArgs.of(List.of(), "11 => [zero] => 11"),
            ParserArgs.of(List.of(11), "11 => [zero] => [plus,11]"),
            ParserArgs.of(List.of(11), "11 => int"),
            ParserArgs.of(List.of(TInt.of(11).label("a")), "11 => int~a"),
            ParserArgs.of(List.of(11), "11 => [plus,[zero]]"),
            ParserArgs.of(List.of(11), "11 + 0"),
            ParserArgs.of(List.of(30), "11 + 4 * 2"),
            ParserArgs.of(List.of(30), "11 => [plus,4] => [mult,2]"),
            ParserArgs.of(List.of(30), "11 => [plus,4][mult,2] => int => [id]"),
            ParserArgs.of(List.of(30), "11 => ([plus,4] * [mult,2]) => int => [id]"), // TODO: do we have binary operator precedence with => and <= being lowest?
            ParserArgs.of(List.of(PlusInst.create(11)), "[plus,11]"),
            ParserArgs.of(List.of(-1, -2, -3), "1 => (([id] + [plus,1] + [plus,2]) * [neg]) => [plus,[zero]] => int"),
            ParserArgs.of(List.of(TInt.of(1).label("a"), TInt.of(2).label("a"), TInt.of(3).label("a")), "1 => ([id] + [plus,1] + [plus,2]) => int~a"),
    };


    @TestFactory
    Stream<DynamicTest> testParsing() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(PARSING).map(query -> DynamicTest.dynamicTest(query.input, () -> {
            assertEquals(query.expected, IteratorUtils.list((Iterator<Obj>) engine.eval(query.input)));
        }));
    }

}
