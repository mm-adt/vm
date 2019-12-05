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
import org.mmadt.machine.object.model.Obj;
import org.mmadt.util.IteratorUtils;

import javax.script.ScriptEngine;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.language.mmlang.util.ParserArgs.args;
import static org.mmadt.language.mmlang.util.ParserArgs.ints;
import static org.mmadt.language.mmlang.util.ParserArgs.objs;
import static org.mmadt.language.mmlang.util.ParserArgs.strs;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class BranchTest {

    private final static ParserArgs[] BRANCHING = new ParserArgs[]{
            /////////////////// MAP TO => ///////////////////
            args(objs(1, 1), "1 => [branch,[id],[id]]"),
            // args(objs(1, 1), "1 => [branch,1->[id],1->[id]]"),
            args(objs(1), "1 => [branch,1->[id],2->[id]]"),
            args(objs(3), "1 => [branch,1->[plus,2],3->[id]]"),
            args(objs(), "2 => [branch,1->[plus,2],3->[id]]"),


            args(objs(3), "1 => [ 1->[plus,2] + 3->[plus,4] ]"),
            args(objs(3), "1 => [ 1->[plus,2] | 3->[plus,4] ]"),

            args(objs(ints(10).label("a")), "10 => [ int~a | str~b | real~c ]"),
            // args(objs(11, "ba", 3.0f), "(1,'b',2.0) => [ int~a->[plus,10] | str~b->[plus,'a'] | real~c->[plus,1.0] ]"),
            args(objs(ints(20).label("a")), "10 => [ int~a | str~b | real~c ] => [plus,[id]]"),
            args(objs(strs("marko rodriguez").label("b")), "'marko' => [ int~a | str~b | real~c ] => [is,[a,str]][plus,' '][plus,'rodriguez']"),

            args(objs(1, 2, 3), "1 => ([id] + [plus,1] + [plus,2])"),
            args(objs(-1, -2, -3), "1 => (([id] + [plus,1] + [plus,2]) * [neg]) => [plus,[zero]] => int"),
            args(objs(ints(1).label("a"), ints(2).label("a"), ints(3).label("a")), "1 => ([id] + [plus,1] + [plus,2]) => int~a"),

    };


    @TestFactory
    Stream<DynamicTest> testBranching() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(BRANCHING).map(query -> DynamicTest.dynamicTest(query.input, () -> assertEquals(query.expected, IteratorUtils.list((Iterator<Obj>) engine.eval(query.input)))));
    }
}
