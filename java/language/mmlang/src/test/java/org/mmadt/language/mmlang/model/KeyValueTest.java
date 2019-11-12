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
package org.mmadt.language.mmlang.model;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.language.mmlang.Compiler;
import org.mmadt.language.mmlang.util.TestArgs;
import org.mmadt.machine.object.impl.TModel;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mmadt.language.mmlang.model.TypingTest.verifyTyping;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class KeyValueTest {


    private static final TModel model = TModel.of(Compiler.asInst(TypingTest.class.getResourceAsStream("kv.mm")));

    private static final String REF = "[ref,int <= [db][count]\n" +
            " -> [sum] => ]";
    private final static TestArgs[] TEST_PARAMETERS = new TestArgs[]{
            new TestArgs("[db]"),
            new TestArgs("[error]", "[db][put,0,'test']", RuntimeException.class),
            new TestArgs("[error]", "[db][order,[gt,[get,0]]][put,0,'test']", RuntimeException.class),
            new TestArgs("[db]", "[db][order,[gt,[get,0]]]"),
            new TestArgs("[db][db][get,1]"),
            new TestArgs("[ref,v{?} <= [db][is,[get,0][eq,'marko']]]", "[db][is,[get,0][eq,'marko']]"),
            new TestArgs("[ref,v{?} <= [db][is,[get,0][eq,10.0]]]", "[db][is,[get,0][eq,10.0]]"),
            new TestArgs("[db][is,[get,0][eq,10]]"),
            new TestArgs(REF, "[db][count]"),
            new TestArgs("[start,1]", "[db][count][count]"), // determined from quantifier
            new TestArgs("[start,1]", "[db][count][count][count]"), // determined from quantifier
            new TestArgs(REF, "[db][count][sum]"),
            new TestArgs("[db]", "[db][order,[gt,[get,0]]][dedup,[get,0]][dedup,[get,0]][order,[gt,[get,0]]]"),
            //  new TestArgs("[db][order,[lt,[get,0]]][order,[lt,[get,0]]]" + REF, "[db][order,[lt,[get,0]]][dedup,[get,0]][dedup,[get,0]][order,[lt,[get,0]]][count]"),
            //  new TestArgs(REF, "[db][order,[gt,[get,0]]][dedup,[get,0]][dedup,[get,0]][order,[lt,[get,0]]][count]"), // TODO: lt shouldn't work
    };

    @TestFactory
    Stream<DynamicTest> testParse() {
        return Stream.of(TEST_PARAMETERS)
                .map(tp -> DynamicTest.dynamicTest(tp.input, () -> {
                    assumeFalse(tp.ignore);
                    if (null != tp.ex)
                        assertThrows(tp.ex, () -> verifyTyping(model.query(Compiler.asInst(model, tp.input))));
                    else
                        assertEquals(Compiler.asInst(model, tp.expected), verifyTyping(model.query(Compiler.asInst(model, tp.input))));
                }));
    }
}
