/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.mmlang.model;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.language.mmlang.Compiler;
import org.mmadt.object.model.composite.Inst;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Validates mm-lang models by parsing them and determing if the {@link String} representation of the {@link Inst}
 * is equivalent to the original {@link String} representation from the file.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ModelTest {

    private final static String[] SCRIPTS = new String[]{
            "org/mmadt/language/mmlang/model/pg.mm",
            "org/mmadt/language/mmlang/model/kv.mm",
            "org/mmadt/language/mmlang/model/bc1.mm",
            "org/mmadt/language/mmlang/model/bc2.mm",
            "org/mmadt/language/mmlang/model/bc3.mm",
            "org/mmadt/language/mmlang/model/db1.mm",
            "org/mmadt/language/mmlang/model/social.mm",
            "org/mmadt/language/mmlang/model/test.mm",
            "org/mmadt/language/mmlang/model/ex.mm"
    };


    @TestFactory
    Stream<DynamicTest> testParseUsingComplexScripts() {
        Function<String, String> reformat = (String str) -> str
                .replaceAll("/\\*(.|\\n)*?\\*/", "")
                .replaceAll("//.*", "")
                .replaceAll("[ \r\n\t]", "")
                .replaceAll("(-+)>", "\n $1> ")
                .replaceAll("([^-=])>", "$1\n  > ")
                .replaceAll("]\\[define", "]\n[define");
        return Stream.of(SCRIPTS)
                .map(scriptName -> DynamicTest.dynamicTest(scriptName, () -> {
                    Inst script2 = Compiler.asInst(reformat.apply(loadScriptResource(scriptName)));
                    Inst result2 = Compiler.asInst(openScriptResource(scriptName));
                    assertEquals(script2, result2);
                    //
                    if (!scriptName.endsWith("ex.mm") && !scriptName.endsWith("pg.mm")) { // it will never work because there is so much operator overloading
                        String script = reformat.apply(loadScriptResource(scriptName));
                        String result = reformat.apply(Compiler.asInst(openScriptResource(scriptName)).toString());
                        assertEquals(script, result);
                    }
                }));
    }

    private static InputStream openScriptResource(String name) {
        return ClassLoader.getSystemResourceAsStream(name);
    }

    private static String loadScriptResource(String name) throws IOException {
        InputStream is = openScriptResource(name);
        assert (is != null);
        try (InputStreamReader isr = new InputStreamReader(is);
             BufferedReader reader = new BufferedReader(isr)) {
            return reader.lines().reduce("", (a, b) -> a + "\n" + b);
        }
    }
}
