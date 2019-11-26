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

package org.mmadt.language.mmlang.jsr223;

import org.mmadt.language.mmlang.Parser;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.processor.util.FastProcessor;
import org.mmadt.util.EmptyIterator;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.support.ParsingResult;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class mmLangScriptEngine extends AbstractScriptEngine {
    private static final Parser PARSER = Parboiled.createParser(Parser.class);
    private final ParseRunner RUNNER = new BasicParseRunner<>(PARSER.Source());

    @Override
    public Iterator<Obj> eval(final String script, final ScriptContext context) throws ScriptException {
        final ParsingResult result = RUNNER.run(script);
        if (!result.valueStack.isEmpty()) {
            // TODO: context bindings are Obj.env();
            final Obj obj = (Obj) result.valueStack.pop();
            // System.out.println("PROCESSING: " + obj);
            return FastProcessor.process(obj);
        }
        return EmptyIterator.instance();
    }

    @Override
    public Iterator<Obj> eval(final Reader reader, final ScriptContext context) throws ScriptException {
        try {
            return this.eval(new BufferedReader(reader).readLine(), context);
        } catch (final IOException e) {
            throw new ScriptException(e.getMessage());
        }
    }

    @Override
    public Bindings createBindings() {
        return null;
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return new mmLangScriptEngineFactory();
    }
}
