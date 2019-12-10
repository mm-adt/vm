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
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.util.IteratorUtils;

import javax.script.ScriptEngine;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.language.mmlang.util.ParserArgs.args;
import static org.mmadt.language.mmlang.util.ParserArgs.ints;
import static org.mmadt.machine.object.impl.__.as;
import static org.mmadt.machine.object.impl.__.count;
import static org.mmadt.machine.object.impl.__.mult;
import static org.mmadt.machine.object.impl.__.plus;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class LabelTest {

    private final static ParserArgs[] LABELS = new ParserArgs[]{
            // count
            args(as(ints()).mult(count()), "int~[count]"),
            args(ints().<Obj>mapFrom(plus(2).domain(ints())), "int => int~[plus,2]"),
            args(ints().label("x").<Obj>mapFrom(plus(2).domain(TSym.of("x"))), "int => int~x~[plus,2]"),
            args(ints().<Int>label("x").plus(2).<Int>as(TInt.of().label("y")).mult(3), "int => int~x~[plus,2]~y~[mult,3]"),
            args(as(ints().label("x")).mult(count()), "int~x~[count]"),
            args(as(ints()).mult(plus(10)).mult(as(TSym.of("y"))).mult(mult(20)).mult(as(TInt.of())), "int~[plus,10]~y~[mult,20]~int"),
            args(as(ints()).mult(plus(10)).mult(as(TInt.of().label("y"))).mult(mult(20)).mult(as(TInt.of())), "int~[plus,10]~int~y~[mult,20]~int"),
            args(as(ints()).mult(plus(10)).mult(as(TSym.of("y"))).mult(mult(20)).mult(as(TInt.of())), "int~[plus,10]~y~[mult,20]~int"),
            args(as(ints()).mult(plus(10)).mult(as(TSym.of("y"))).mult(as(TSym.of("z"))).mult(mult(20)).mult(as(TInt.of())), "int~[plus,10]~y~z~[mult,20]~int"),
    };


    @TestFactory
    Stream<DynamicTest> testLabels() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(LABELS).map(query -> DynamicTest.dynamicTest(query.input, () -> assertEquals(query.expected, IteratorUtils.list((Iterator<Obj>) engine.eval(query.input)))));
    }
}
