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
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.testing.LanguageArgs;
import org.mmadt.util.IteratorUtils;

import javax.script.ScriptEngine;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.testing.LanguageArgs.args;
import static org.mmadt.testing.LanguageArgs.ints;
import static org.mmadt.testing.LanguageArgs.recs;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ModelTest {

    private final static LanguageArgs[] MODELS = new LanguageArgs[]{
            args(ints(2000), "10 =[=[1000~x]]=> [map,x][plus,x]"),
            args(ints(1110), "10 =[=[1000~x;100~y]]=> [map,10][plus,x][plus,y]"),
            //args(ints(1110), "10 =[=[1000~x;(int~y<=[map,100])]]=> [map,10][plus,x][plus,y]"),
            //args(ints(1110), "10 =[=[1000~x;(int~y<=[map,x][plus,100])]]=> [map,10][plus,y]"),
            //args(ints(1110), "10 =[=[1000~x;[map,x][plus,100]~y]]=> [map,10][plus,y]"),
            args(recs(TStr.of("x"), TInt.of(1000).label("x"), TStr.of("y"), TInt.of(100).label("y")), "10 =[=[1000~x;100~y]]=> [map,10][plus,x][plus,y][state]"),
    };


    @TestFactory
    Stream<DynamicTest> testModeling() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(MODELS).map(query -> DynamicTest.dynamicTest(query.input, () -> assertEquals(query.expected, IteratorUtils.list((Iterator<Obj>) engine.eval(query.input)))));
    }
}
