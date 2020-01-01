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
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.model.atomic.Int;

import javax.script.ScriptEngine;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mmadt.language.mmlang.util.ParserArgs.args;
import static org.mmadt.language.mmlang.util.ParserArgs.ints;
import static org.mmadt.language.mmlang.util.ParserArgs.lsts;
import static org.mmadt.language.mmlang.util.ParserArgs.recs;
import static org.mmadt.machine.object.impl.__.map;
import static org.mmadt.machine.object.impl.__.plus;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class BindingTest {

    private final static Int oneX = ints(1).label("x");
    private final static Int twoY = ints(2).label("y");

    private final static ParserArgs[] BINDINGS = new ParserArgs[]{
            args(oneX, "1~x"),
            args(ints().<Int>access(plus(ints().label("x").access(map(ints().label("x"))))), "int~x => x + x"),
            args(oneX, "1~x => [map,x]"),
            args(ints(2), "1~x => [plus,x]"),
            args(ints(2), "1~x => [plus,int~x]"),
            args(ints().<Int>access(plus(TInt.of().label("x"))), "int~x => [plus,obj~x]"),
            args(ints().<Int>access(plus(TInt.of().label("x"))), "int~x => [plus,x]"),
            args(twoY, "1~x => [plus,x][as,y]"),
            args(ints(3), "1~x => [plus,x+x]"),
            args(ints(4), "[1~x;2~y] => [map,y*(x+x)]"),
            args(ints(6), "[1~x;2~y] => [map,(y*(x+x))+2]"),
            args(ints(4), "1~x => x*(x+x)+2"),
            args(ints(4), "1~x => int~x*(x+x)+2"),
            //
            args(lsts(List.of(oneX, oneX)), "1~x => [map,[x;x]]"),
            args(recs(Map.of(oneX, oneX)), "1~x => [map,[x:x]]"),
            args(lsts(List.of(oneX, Map.of(oneX, oneX))), "1~x => [map,[x;[x:x]]]"),
            args(recs(Map.of(oneX, List.of(oneX, oneX))), "1~x => [map,[x:[x;x]]]"),
            args(lsts(List.of(oneX, Map.of(oneX, twoY))), "[1~x:2~y] => [map,[x;[x:y]]]"),
            args(recs(Map.of(oneX, List.of(oneX, twoY))), "[1~x:2~y] => [map,[x:[x;y]]]"),
            ///
            args(oneX, "obj{0} => [start,1~x][map,x]"),
            args(lsts(List.of(oneX, oneX)), "obj{0} => [start,1~x][map,[x;x]]"),
            args(recs(Map.of(oneX, oneX)), "obj{0} => [start,1~x][map,[x:x]]"),
            //////////////
            args(lsts(List.of(oneX, twoY, twoY, twoY)), "[1~x;2~y] => [plus,[y;y]]"),
            args(recs(Map.of(oneX, twoY, twoY, List.of(oneX, twoY))), "[1~x:2~y] => [plus,[y:[x;y]]]"),
    };


    @TestFactory
    Stream<DynamicTest> testBindings() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(BINDINGS).map(query -> query.execute(engine));
    }
}
