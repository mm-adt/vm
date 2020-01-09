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
import org.mmadt.testing.LanguageArgs;

import javax.script.ScriptEngine;
import java.util.List;
import java.util.stream.Stream;

import static org.mmadt.machine.object.impl.__.as;
import static org.mmadt.machine.object.impl.__.mult;
import static org.mmadt.machine.object.impl.__.plus;
import static org.mmadt.testing.LanguageArgs.args;
import static org.mmadt.testing.LanguageArgs.ints;
import static org.mmadt.testing.LanguageArgs.objs;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class LabelTest {

    private final static LanguageArgs[] LABELS = new LanguageArgs[]{
            args(ints(1).binding("x"), "1=>x"),
            args(ints(1).binding("x"), List.of(ints(1).binding("x")), "1=>x"),
            args(ints(1).binding("y"), "1=>x=>y"),
            args(ints(1).binding("y"), List.of(ints(1).binding("x"), ints(1).binding("y")), "1=>x=>y"),
            args(ints(3), "1=>x=>y=>[plus,2]"),
            args(ints(3).binding("z"), "1=>x=>y=>[plus,2]=>z"),
            args(objs(), "1=>x=>y=>[plus,2]=>x"),
            args(ints(1).binding("x"), "1=>x=>y=>x"),
            args(ints(1).binding("y"), "1=>x=>y=>x=>y"),
            args(ints(1).binding("y"), "1=>x=>y=>[plus,0]=>y"),
            args(ints(1).binding("y"), "1=>x=>y=>[plus,10][minus,2][minus,8]=>y"),
            args(objs(), "1=>x=>y=>[plus,10][minus,2][minus,9]=>y"),

            /////////////////////////////////////////////////////

            args(ints().ref(plus(2)), List.of(), "int=>int=>[plus,2]"),
            args(ints().ref(plus(2)), List.of(ints().binding("x")), "int=>int~x=>[plus,2]"),
            args(ints().ref(plus(2).mult(as(ints().binding("y"))).mult(mult(3))), List.of(ints().binding("x"), ints().binding("y").ref(plus(2))), "int~x=>[plus,2]=>y=>[mult,3]"),
            args(ints().ref(plus(1).mult(as(ints().binding("y"))).mult(mult(20)).mult(as(ints()))), List.of(ints().binding("y").ref(plus(1))), "int=>[plus,1]=>y=>[mult,20]=>int"),
            args(ints().ref(plus(2).mult(as(ints().binding("y"))).mult(mult(20)).mult(as(ints()))), List.of(ints().binding("y").ref(plus(2))), "int=>[plus,2]=>int~y=>[mult,20]=>int"),
            args(ints().ref(plus(3).mult(as(ints().binding("y"))).mult(mult(20)).mult(as(ints()))), List.of(ints().binding("y").ref(plus(3))), "int=>[plus,3]=>y=>[mult,20]=>int"),
            args(ints().ref(plus(4).mult(as(ints().binding("y"))).mult(as(ints().binding("z"))).mult(mult(20)).mult(as(ints()))), List.of(ints().binding("y").ref(plus(4)), ints().binding("z").ref(plus(4).mult(as(ints().binding("y"))))),
                    "int=>[plus,4]=>y=>z=>[mult,20]=>int"),

            /////////////////////////////////////////////////////

            args(ints(6), List.of(ints(3).binding("x")), "3 => x => x + x"),
            args(ints(-6), List.of(ints(3).binding("x")), "3 => x => (-x + -x)"),
            args(ints(0), List.of(ints(3).binding("x")), "3 => x => -x + x"),
            args(ints(-9), List.of(ints(3).binding("x")), "3 => x => ((-x + x) + -x) * x"),
            args(ints(9), List.of(ints(3).binding("x")), "3 => x => (((-x + x) + -x) * -x)"),
            args(ints(-3), List.of(ints(3).binding("x")), "3 => x => (-x + -x + x)"),
            args(ints(-3), List.of(ints(3).binding("x")), "3 => x => -x - int~x + x"),
            args(ints(-3), List.of(ints(3).binding("x")), "3 => x => -x + (-int~x) + x"),
            args(ints(-3), List.of(ints(3).binding("x")), "3 => x => -x - x + x"),
            args(ints(-9), List.of(ints(3).binding("x")), "3 => x => (-x + -x + -x)"),
            args(ints(18), List.of(ints(3).binding("x")), "3 => x => (-x + -x * -x)"),
            args(ints(-18), List.of(ints(3).binding("x")), "3 => x => (-x + -x * x)"),
            args(ints(-18), List.of(ints(3).binding("x")), "3 => x => (-x + -x) * x"),
            args(ints(12), List.of(ints(3).binding("x"), ints(3).binding("y")), "3 => x => y => (x + x + y + y)"),
            args(ints(6), List.of(ints(3).binding("x"), ints(3).binding("y")), "3 => x => y => (x + y)"),
            args(ints(-18), List.of(ints(3).binding("x"), ints(3).binding("y")), "3 => x => y => ((y + y) * -x)"),

            /////////////////////////////////////////////////////

            args(ints(3).binding("x"), List.of(ints(3).binding("x")), "3 => x => [is < (3 + x)]"),
            args(ints(3).binding("x"), List.of(ints(3).binding("x")), "3 => x => [is < (3 + (1 + x))]"),
            args(ints(3).binding("x"), List.of(ints(3).binding("x")), "3 => x => [is < (3 + (x~int + 1))]"),
            args(objs(), "3 => x => [is > (3 + (int~x + 1))]"),
    };


    @TestFactory
    Stream<DynamicTest> testLabels() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(LABELS).map(query -> query.execute(engine));
    }
}
