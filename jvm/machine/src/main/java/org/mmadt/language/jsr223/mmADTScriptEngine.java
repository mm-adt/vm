/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.jsr223;

import org.mmadt.language.obj.Obj;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.Reader;
import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface mmADTScriptEngine extends ScriptEngine {

    @Override
    public default Iterator<Obj> eval(String script) throws ScriptException {
        return this.eval(script, this.getContext());
    }

    @Override
    public Iterator<Obj> eval(Reader reader) throws ScriptException;

    @Override
    public Iterator<Obj> eval(String script, ScriptContext context) throws ScriptException;

    @Override
    public Iterator<Obj> eval(Reader reader, ScriptContext context) throws ScriptException;


}
