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

package org.mmadt.language.jsr223;

import org.mmadt.VmException;
import org.mmadt.language.LanguageException;
import org.mmadt.language.Tokens;
import org.mmadt.language.obj.Obj;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface mmADTScriptEngine extends ScriptEngine {

    @Override
    public default Bindings createBindings() {
        return new SimpleBindings();
    }

    @Override
    public default Obj eval(String script) throws VmException {
        return this.eval(script, this.getContext());
    }

    @Override
    public default Obj eval(Reader reader) throws VmException {
        return this.eval(reader, this.getContext());
    }

    @Override
    public default Obj eval(String script, ScriptContext context) throws VmException {
        return this.eval(script, context.getBindings(ScriptContext.ENGINE_SCOPE));
    }

    @Override
    public Obj eval(String script, Bindings bindings) throws VmException;

    @Override
    public default Obj eval(Reader reader, ScriptContext context) throws VmException {
        try {
            return eval(new BufferedReader(reader).readLine(), context);
        } catch (IOException e) {
            throw new LanguageException(e.getMessage());
        }
    }

    public default Optional<Obj> evalMeta(String line) {
        if (line.trim().equals(Tokens.COLON())) {
            final Obj obj = (Obj) this.getContext().getBindings(ScriptContext.ENGINE_SCOPE).get(Tokens.COLON());
            this.getContext().getBindings(ScriptContext.ENGINE_SCOPE).remove(Tokens.COLON());
            return Optional.ofNullable(obj);
        } else {
            final Obj newValue = this.eval(line.trim().substring(1));
            this.getContext().getBindings(ScriptContext.ENGINE_SCOPE).put(Tokens.COLON(), newValue);
            return Optional.ofNullable(newValue);
        }
    }


}
