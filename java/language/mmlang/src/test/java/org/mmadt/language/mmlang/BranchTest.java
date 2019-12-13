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
import static org.mmadt.language.mmlang.util.ParserArgs.strs;
import static org.mmadt.machine.object.impl.__.branch;
import static org.mmadt.machine.object.impl.__.choose;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class BranchTest {

    private final static ParserArgs[] BRANCHING = new ParserArgs[]{
            args(TRec.of(1, "b"),
                    "1->'b'"),
            args(TRec.of(TRec.of(1, "b"), 2),
                    "1->'b'->2"),
            args(objs(1, 1),
                    "1 => [branch,[id],[id]]"),
            args(objs(1, 1),
                    "1 => [branch,1->[id],int->[id]]"),
            args(objs(1),
                    "1 => [branch,1->[id],2->[id]]"),
            args(objs(3),
                    "1 => [branch,1->[plus,2],3->[id]]"),
            args(objs(),
                    "2 => [branch,1->[plus,2],3->[id]]"),


            /////////////////////////////////////////////////////

            args(branch(1, 2, 3),
                    "[ 1 + 2 + 3 ]"),
            args(branch(1, 2, 3),
                    "[ + 1 + 2 + 3 ]"),
            args(choose(1, 2, 3),
                    "[ 1 | 2 | 3 ]"),
            args(choose(1, 2, 3),
                    "[ | 1 | 2 | 3 ]"),
            args(objs(3),
                    "1 => [ 1->[plus,2] + 3->[plus,4] ]"),
            args(objs(3),
                    "1 => [ + 1->[plus,2] + 3->[plus,4] ]"),
            args(objs(3),
                    "1 => [ 1->[plus,2] | 3->[plus,4] ]"),
            args(objs(3),
                    "1 => [ | 1->[plus,2] | 3->[plus,4] ]"),
            args(objs(ints(10).label("a")),
                    "10 => [ int~a | str~b | real~c ]"),
            args(objs(11, 12, 13),
                    "10 => [ int~a->[plus,1] + int~b->[plus,2] + int~c->[plus,3] ]"),
            args(objs(11, 12, 13),
                    "10 => [branch,[plus,1],[plus,2],[plus,3]]"),
            // args(objs(11, "ba", 3.0f), "(1,'b',2.0) => [ int~a->[plus,10] | str~b->[plus,'a'] | real~c->[plus,1.0] ]"),
            args(ints(10).<Int>label("a"),
                    "10 => [ int~a | str~b | real~c ] => [is,[and,[gt,9],[eq,[id]]]][id][id]"),
            args(ints(20),
                    "10 => [ int~a | str~b | real~c ] => [plus,[id]]"),
            args(ints(11),
                    "10 => [ int~a->[plus,1] | int~b->[plus,2] | int~c->[plus,3] ]"),
            args(objs(strs("marko rodriguez")),
                    "'marko' => [ int~a | str~b | real~c ] => [is,[a,str]][plus,' '][plus,'rodriguez']"),
            args(objs(strs("marko").label("b")),
                    "'marko' => [ int~a | str~b | real~c ] => [is,[a,str]]"),
            args(objs(1, 2, 3),
                    "1 => ([id] + [plus,1] + [plus,2])"),
            args(objs(-1, -2, -3),
                    "1 => (([id] + [plus,1] + [plus,2]) * [neg]) => [plus,[zero]] => int"),
            args(objs(ints(1).label("a"), ints(2).label("a"), ints(3).label("a")),
                    "1 => ([id] + [plus,1] + [plus,2]) => int~a"),

            /////////////////////////////////////////////////////

            // 4 representations of the same compilation
            args(objs(2, 2, 2), "2 => [branch,[int;2;[is > 1]]] => int"),
            args(objs(2, 2, 2), "2 => [int + 2 + [is > 1]] => int"),
            args(objs(2, 2, 2), "2 => [branch,int,2,[is > 1]] => int"),
            args(objs(2, 2, 2), "2 => [int->int + 2->2 + [is,[gt,1]]->[is > 1]] => int")

    };


    @TestFactory
    Stream<DynamicTest> testBranching() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(BRANCHING).map(query -> DynamicTest.dynamicTest(query.input, () -> assertEquals(query.expected, IteratorUtils.list((Iterator<Obj>) engine.eval(query.input)))));
    }
}
