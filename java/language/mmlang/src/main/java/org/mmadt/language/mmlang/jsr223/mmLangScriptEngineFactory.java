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

package org.mmadt.language.mmlang.jsr223;

import javax.script.ScriptEngineFactory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class mmLangScriptEngineFactory implements ScriptEngineFactory {

    /*private static mmLangScriptEngineFactory FACTORY = new mmLangScriptEngineFactory();

    public static mmLangScriptEngineFactory instance() {
        return FACTORY;
    }*/

    @Override
    public String getEngineName() {
        return "mmlang";
    }

    @Override
    public String getEngineVersion() {
        return "v0.1-alpha";
    }

    @Override
    public List<String> getExtensions() {
        return List.of("mm");
    }

    @Override
    public List<String> getMimeTypes() {
        return List.of("mm");
    }

    @Override
    public List<String> getNames() {
        return List.of("mm");
    }

    @Override
    public String getLanguageName() {
        return "mmlang";
    }

    @Override
    public String getLanguageVersion() {
        return "v0.1-alpha";
    }

    @Override
    public Object getParameter(final String key) {
        return null;
    }

    @Override
    public String getMethodCallSyntax(final String obj, final String m, final String... args) {
        return obj + " => " + " [" + m + "," + Arrays.toString(args) + "]";
    }

    @Override
    public String getOutputStatement(final String toDisplay) {
        return toDisplay;
    }

    @Override
    public String getProgram(final String... statements) {
        return Stream.of(statements).reduce("", (a, b) -> a + " " + b).trim();
    }

    @Override
    public mmLangScriptEngine getScriptEngine() {
        return new mmLangScriptEngine();
    }
}
