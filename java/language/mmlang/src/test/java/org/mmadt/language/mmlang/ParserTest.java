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
            ParserArgs.of(List.of(11), "11 => int"),
            ParserArgs.of(List.of(30), "11 => [plus,4] => [mult,2]"),
            ParserArgs.of(List.of(30), "11 => [plus,4][mult,2] => int => [id]"),
    };


    @TestFactory
    Stream<DynamicTest> testParsing() {
        return Stream.of(PARSING).map(query -> DynamicTest.dynamicTest(query.input, () -> {
            ScriptEngine engine = new mmLangScriptEngine();
            assertEquals(query.expected, IteratorUtils.list((Iterator<Obj>) engine.eval(query.input)));
        }));
    }

}
