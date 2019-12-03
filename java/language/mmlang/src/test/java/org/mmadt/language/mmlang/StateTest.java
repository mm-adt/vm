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
class StateTest {

    private final static ParserArgs[] PARSING = new ParserArgs[]{
            args(ints().plus(3), "(int -> (x <= 3)) => [plus,x]"),
            args(ints().plus(3).plus(10), "int -> (x <= 3) -> (y <= 10) => [plus,x][plus,y]"),
            args(ints().mult(10).mult(10), "(int -> (z <= 10) -> ([plus,int] <= [mult,z])) => [plus,2][plus,3]"),
            args(TLst.of("x", "y", "a"), "[;] -> (x <= ['x';'y']) -> (y <= ['a']) => [plus,x][plus,y]"),

            args(ints(29).<Int>label("x"), "(['name':'marko','age':29] -> (person <= ['name':str,'age':int~x])) => [as,person][get,'age']"),
            //args(ints(29).<Int>label("x"), "(['name':'marko','age':29] -> (person <= ['name':str,'age':int~x])) <= (person['name':'marko','age':29]) => [get,'age']"),
            args(ints(1).<Int>label("x"), "(['name':'marko','age':29] -> (person <= ['name':str,'age':int~x])) => [as,person][get,'age'][map,['name':'a','age':1]][as,person][get,'age']"),
            args(ints(1).<Int>label("x"), "(['name':'marko','age':29] -> (person <= ['name':str,'age':int~x])) => [as,person][get,'age'][map,[map,[map,['name':'a','age':1]]][as,person]][get,'age']"),
            args(ints(1).<Int>label("x"), "(['name':'marko','age':29] -> (person <= ['name':str,'age':int~x])) => [as,person][get,'age'][map,['name':'a','age':1]][as,person][get,'age'][is,[eq,x]]"),
            args(objs(), "(['name':'marko','age':29] -> (person <= ['name':str,'age':int~x])) => [as,person][get,'age'][map,[map,[map,['name':'a','age':1]]][as,person]][get,'age'][is,[gt,x]]"),
            //args(ints(1).<Int>label("x"), "(['name':'marko','age':29] -> (person <= ['name':str,'age':int~x])) => [as,person][get,'age'][map,person.['name':'a','age':1]][get,'age']"),
    };

    @TestFactory
    Stream<DynamicTest> testState() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(PARSING).map(query -> DynamicTest.dynamicTest(query.input, () -> assertEquals(query.expected, IteratorUtils.list((Iterator<Obj>) engine.eval(query.input)))));
    }

}
