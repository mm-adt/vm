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
import org.mmadt.testing.LanguageArgs;

import javax.script.ScriptEngine;
import java.util.Map;
import java.util.stream.Stream;

import static org.mmadt.testing.LanguageArgs.args;
import static org.mmadt.testing.LanguageArgs.except;
import static org.mmadt.testing.LanguageArgs.ints;
import static org.mmadt.testing.LanguageArgs.recs;
import static org.mmadt.testing.LanguageArgs.strs;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class SymbolTest {

    private final static LanguageArgs[] SYMBOLS = new LanguageArgs[]{
            args(recs(Map.of("name", TStr.of(), "age", TInt.of())).symbol("person"), "person~['name':str,'age':int]"),
            args(recs(Map.of("name", "marko", "age", 29)).symbol("person"), "[['name':'marko','age':29];person~['name':str,'age':int]] => [get,0][as,person~rec]"),
            args(recs(Map.of("name", "marko", "age", 29)).symbol("person"), "person <=[=[['name':str,'age':int]~person]][map,['name':'marko','age':29]][as,person~rec]"),
            except(new RuntimeException(), "person <=[=[['name':str,'age':int]~person]][map,['name':'marko','age':29]][as,person~rec][put,'age','29asString']"),
            args(recs(Map.of("name", "marko", "age", 29)).symbol("person"), "person <=[=[['name':str,'age':int]~person]][map,['name':'marko','age':29]][as,person~obj][put,'age',29]"),
            args(recs(Map.of("name", "marko", "age", 30)).symbol("person"), "person <=[=[int[is>0]~age;['name':str,'age':age]~person]][map,['name':'marko','age':29]][as,person~rec][put,'age',30]"),
            except(new RuntimeException(), "person <=[=[int[is>0]~age;['name':str,'age':age]~person]][map,['name':'marko','age':29]][as,person~rec][put,'age',-4]"),

            /////

            args(ints(), "1 => [plus,2][type]"),
            args(recs(Map.of("name", TStr.of(), "age", TInt.of())).binding("person"), "person <=[=[int[is>0]~age;['name':str,'age':age]~person]][map,['name':'marko','age':29]][as,person~rec][put,'age',30][type]"),
            args(strs("person"), "person <=[=[int[is>0]~age;['name':str,'age':age]~person]][map,['name':'marko','age':29]][as,person~rec][put,'age',30][type][bind]"),
    };

    @TestFactory
    Stream<DynamicTest> testSymbols() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(SYMBOLS).map(query -> query.execute(engine));
    }
}
