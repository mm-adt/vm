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
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.util.IteratorUtils;

import javax.script.ScriptEngine;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.language.mmlang.util.ParserArgs.args;
import static org.mmadt.language.mmlang.util.ParserArgs.ints;
import static org.mmadt.language.mmlang.util.ParserArgs.strs;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CompositeTest {

    private final static ParserArgs[] COMPOSITE = new ParserArgs[]{

            /////////
            // LST //
            /////////
            args(ints(3), "[1;2] => [x;y] => (x + y)"),
            args(TLst.of(ints(1).label("x"), ints(2).label("y")).label("z"), "[1;2] => [x;y] => z"),
            args(TLst.of(ints(1).label("x"), ints(2).label("y")).label("z"), "[1;2] => [x;y] => lst~z"),
            args(ints(6), "[1;2] => [x;y] => (x + (y + (x + y)))"),
            // args(TRec.of(Map.of(ints(1).label("x"),ints(2).label("y"))), "[1;2] => [x;y] => [map,[x:y]]"),
            // args(ints(12), "[1~x;2~y] => [x;y] => [map,x][plus,y][plus,0] => int~z => [explain]"),

            /////////
            // REC //
            /////////
            args(TRec.of("name", strs("marko").label("x")), "['name':'marko'] => ['name':str~x]"),
            args(TRec.of("name", strs("marko")), "['name':'marko'] => [str:'marko']"),
            args(TRec.of("name", strs("marko").label("x")), "['name':'marko'] => [str:'marko'~x]"),
            args(TRec.of("name", strs("marko").label("x"), "age", ints(29).label("y")), "['name':'marko','age':29] => ['name':str~x,'age':int~y]"),
            args(TRec.of("name", strs("marko").label("x"), "age", ints(29).label("y")).label("z"), "['name':'marko','age':29] => ['name':str~x,'age':int~y]~z"),
            args(TRec.of("name", TRec.of("first", strs("marko").label("x")), "age", ints(29).label("y")).label("z"), "['name':['first':'marko'],'age':29] => ['name':['first':str~x],'age':int~y]~z"),
            args(TRec.of("name", TRec.of("first", strs("marko")), "age", ints(29).label("y")).label("z"), "['name':['first':'marko'],'age':29] => ['name':['first':str],'age':int~y] => z"),
            args(TRec.of("name", TRec.of("first", strs("marko")), "age", ints(29).label("y")).label("z"), "['name':['first':'marko','last':'rodster'~y],'age':29] => ['name':['first':str],'age':int~y] => rec~z"),
    };

    @TestFactory
    Stream<DynamicTest> testComposites() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(COMPOSITE).map(query -> DynamicTest.dynamicTest(query.input, () -> assertEquals(query.expected, IteratorUtils.list((Iterator<Obj>) engine.eval(query.input)))));
    }
}
