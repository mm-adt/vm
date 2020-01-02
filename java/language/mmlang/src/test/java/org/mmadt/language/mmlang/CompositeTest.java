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
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.util.IteratorUtils;

import javax.script.ScriptEngine;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.language.mmlang.util.ParserArgs.args;
import static org.mmadt.language.mmlang.util.ParserArgs.bools;
import static org.mmadt.language.mmlang.util.ParserArgs.ints;
import static org.mmadt.language.mmlang.util.ParserArgs.lsts;
import static org.mmadt.language.mmlang.util.ParserArgs.objs;
import static org.mmadt.language.mmlang.util.ParserArgs.recs;
import static org.mmadt.language.mmlang.util.ParserArgs.strs;
import static org.mmadt.machine.object.impl.__.get;
import static org.mmadt.machine.object.impl.__.map;
import static org.mmadt.machine.object.impl.__.plus;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CompositeTest {

    private final static Int oneX = ints(1).label("x");
    private final static Int twoY = ints(2).label("y");
    private final static Int threeZ = ints(3).label("z");
    private final static Lst alst = lsts(List.of(ints(), recs(Map.of(bools(), ints())))).label("z");

    private final static ParserArgs[] COMPOSITE = new ParserArgs[]{

            /////////
            // LST //
            /////////
            args(lsts(lsts(1, 2), 3, 4), "[[1;2];3;4]"),
            args(lsts(1, lsts(2, 3), 4), "[1;[2;3];4]"),
            args(lsts(List.of(1, lsts(2, 3)), lsts(4)), "[[1;[2;3]];[4]]"),
            args(ints(3), "[1;2] => [x;y] => (x + y)"),
            args(lsts(ints(1).label("x"), ints(2).label("y")).label("z"), "[1;2] => [x;y] => z"),
            args(lsts(ints(1).label("x"), ints(2).label("y")).label("z"), "[1;2] => [x;y] => lst~z"),
            args(ints(6), "[1;2] => [x;y] => (x + (y + (x + y)))"),
            args(objs(), "[1;2] => [x;y] => [x:y]"),
            args(recs(Map.of(ints(1).label("x"), ints(2).label("y"))), "[1;2] => [x;y] => [map,[x:y]]"),
            args(twoY, "[1~x;2~y;3~z] => [get,1]"),
            args(oneX, "[1~x;2~y;3~z] => [get,0]"),
            args(oneX, "[1~x;2~y;3~z] => [map,[x;y]][get,0]"),
            args(objs(List.of("c", 1), List.of("c", 2)), "obj{0} => [start,['a';1],['b';2]][put,[start,1][plus,2][plus,[neg]],[start,'c'][plus,[zero]]]"),
            args(ints(3).label("z"), "[1~x;2~y] => [x;y] => (int <= [map,x][plus,y][plus,0][as,int~z])"),
            args(ints(-3).label("z"), "[1~x;2~y] => [x;y] => (x + y + 0 * -1) => int~z"),
            args(ints(3).label("z"), "[1~x;2~y] => [x;y] => (x + y + 0) => int~z"),
            args(ints(3), "[1;2]~z => ((int <= (z.0)) + (int <= (z.1)))"),
            args(ints(3), "[1;2]~z => (z.0) => + (z.1)"),
            args(ints(3), "[1;2]~z => (z.0) + (z.1)"),
            args(ints(3), "[1;[2.2:2]]~z => (z.0) + (z.1.(2.2))"),
            args(ints(3), "[1;[2.2:2]]~z => (z.0) + (z.(z.0).(2.2))"),
            args(ints(3), "[1;[2.2:2]]~z => (z.0) => [plus,z.(z.0).(2.2)]"),
            args(ints(3), "[1;[2.2:2]] => z => (z.(z.0).(2.2)) => int~w => (z.0) => [plus,w]"),
            args(ints(3), "[1;[true:2]]~z => (z.0) + (z.1.true + 0)"),
            args(ints().<Int>access(map(alst).mult(get(0)).mult(plus(ints().access(map(alst).mult(get(1)).mult(get(bools())))))), "[int;[bool:int]]~z => (z.0) + (z.1.bool) => [explain]"),

            /////////
            // REC //
            /////////
            args(recs(Map.of(recs(Map.of(1, 2)).label("x"), 3, 4, 5)), "obj{0} => [start,[[1:2]~x:3,4:5]][map,[x:3,4:5]]"),
            args(recs(Map.of(recs(1, 2), 3, 4, 5)), "[[1:2]:3,4:5]"),
            args(recs(Map.of(1, recs(2, 3))), "[1:[2:3]]"),
            args(recs(Map.of(1, recs(2, 3)), lsts(4)), "[[1:[2:3]]:[4]]"),
            args(lsts(List.of(ints().label("x"), ints().label("y"), ints().label("x"), ints().access(map(ints().label("y")).mult(plus(ints().label("x")))))), "[int~x;int~y] => [plus,[x;int <= [map,y][plus,x]]]"),
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
