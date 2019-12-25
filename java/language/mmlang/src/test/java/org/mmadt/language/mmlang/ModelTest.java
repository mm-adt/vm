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
import org.mmadt.machine.object.impl.TSym;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.impl.composite.inst.branch.ChooseInst;
import org.mmadt.machine.object.impl.composite.inst.filter.IdInst;
import org.mmadt.machine.object.impl.composite.inst.map.ModelInst;
import org.mmadt.machine.object.impl.composite.inst.map.PlusInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.util.IteratorUtils;

import javax.script.ScriptEngine;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.language.mmlang.util.ParserArgs.args;
import static org.mmadt.language.mmlang.util.ParserArgs.ints;
import static org.mmadt.language.mmlang.util.ParserArgs.objs;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ModelTest {

    private final static ParserArgs[] MODELS = new ParserArgs[]{
            args(ModelInst.create("cpu", TRec.of(Map.of(TSym.of("a"), 1)), IdInst.create()), "[=cpu,[a:1],[id]]"),
            args(ModelInst.create("cpu", TRec.of(Map.of(TSym.of("weight"), 0.0f, TSym.of("path"), TLst.of())), ChooseInst.create(TRec.of("a", PlusInst.create(2)))), "[=cpu,[weight:0.0,path:[;]],[choose,['a':[plus,2]]]]"),
            args(ints(11), "10 => [=cpu,[:],[plus,1]]"),
            args(ints(11), "10 => [=cpu,[plus,1]]"),
            args(objs(), "10 => [=cpu,[[int;int] -> [id] | obj -> obj{0}]]"),
            args(objs(TLst.of(1, 2)), "[1;2] => [=cpu,[[int;int] -> [id] | obj -> obj{0}]]"),
            args(objs(TLst.of(ints(1).label("x"), ints(2).label("y"))), "[1;2] => [=cpu,[[int~x;int~y] -> [int~x;int~y] | obj -> obj{0}]]"),
            args(objs(), "[1;2] => [=cpu,[[int~x;str~y] -> [id] | obj -> obj{0}]]"),
            args(objs(TLst.of(ints(2), ints(1))), "[1;2] => [=cpu,[[int;int]~z -> [[map,z][get,1];[map,z][get,0]] | obj -> obj{0}]]"),
            args(objs(TLst.of(ints(2).label("y"), ints(1).label("x"))), "[1;2] => [=cpu,[[int~x;int~y]~z -> [[map,y];[map,x]] | obj -> obj{0}]]"),
            // args(objs(TLst.of(ints(2).label("y"), ints(1).label("x"))), "[1;2] => [=cpu,[[int~x;int~y]~z -> [y;x] | obj -> obj{0}]]"),
            // args(ints(500), "10 => [=cpu,[weight->50|path->20]][mult,weight]"),
            // args(ints(20), "10 => [=cpu,[weight->50|path->20]][mult,weight][map,path]"),
    };


    @TestFactory
    Stream<DynamicTest> testModeling() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(MODELS).map(query -> DynamicTest.dynamicTest(query.input, () -> assertEquals(query.expected, IteratorUtils.list((Iterator<Obj>) engine.eval(query.input)))));
    }
}
