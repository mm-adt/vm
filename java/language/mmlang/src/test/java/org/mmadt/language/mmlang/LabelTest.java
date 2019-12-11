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
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Int;

import javax.script.ScriptEngine;
import java.util.List;
import java.util.stream.Stream;

import static org.mmadt.language.mmlang.util.ParserArgs.args;
import static org.mmadt.language.mmlang.util.ParserArgs.ints;
import static org.mmadt.language.mmlang.util.ParserArgs.objs;
import static org.mmadt.machine.object.impl.TSym.sym;
import static org.mmadt.machine.object.impl.__.mult;
import static org.mmadt.machine.object.impl.__.plus;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class LabelTest {

    private final static ParserArgs[] LABELS = new ParserArgs[]{
            args(ints(1).label("x"), "1=>x"),
            args(ints(1).label("x"), List.of(ints(1).label("x")), "1=>x"),
            args(ints(1).label("y"), "1=>x=>y"),
            args(ints(1).label("y"), List.of(ints(1).label("x"), ints(1).label("y")), "1=>x=>y"),
            args(ints(3), "1=>x=>y=>[plus,2]"),
            args(ints(3).label("z"), "1=>x=>y=>[plus,2]=>z"),
            args(objs(), "1=>x=>y=>[plus,2]=>x"),
            args(ints(1).label("x"), "1=>x=>y=>x"),
            args(ints(1).label("y"), "1=>x=>y=>x=>y"),
            args(ints(1).label("y"), "1=>x=>y=>[plus,0]=>y"),
            args(ints(1).label("y"), "1=>x=>y=>[plus,10][minus,2][minus,8]=>y"),
            args(objs(), "1=>x=>y=>[plus,10][minus,2][minus,9]=>y"),

            /////////////////////////////////////////////////////

            args(ints().<Obj>mapFrom(plus(2).domain(ints())),
                    "int=>int=>[plus,2]"),
            args(ints().<Obj>mapFrom(plus(2).domain(sym("x"))),
                    "int=>int~x=>[plus,2]"),
            args(ints().<Int>mapFrom(plus(2).range(sym("y")).mult(mult(3))),
                    "int~x=>[plus,2]=>y=>[mult,3]"),
            args(ints().<Int>mapFrom(TInt.of().plus(10).label("y").mult(20)),
                    "int=>[plus,10]=>y=>[mult,20]=>int"),
            args(ints().<Int>mapFrom(TInt.of().plus(10).label("y").mult(20)),
                    "int=>[plus,10]=>int~y=>[mult,20]=>int"),
            args(ints().<Int>mapFrom(TInt.of().plus(10).label("y").mult(20)),
                    "int=>[plus,10]=>y=>[mult,20]=>int"),
            args(ints().<Int>mapFrom(TInt.of().plus(10).label("y").label("z").mult(20)),
                    "int=>[plus,10]=>y=>z=>[mult,20]=>int"),
    };


    @TestFactory
    Stream<DynamicTest> testLabels() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(LABELS).map(query -> query.execute(engine));
    }
}
