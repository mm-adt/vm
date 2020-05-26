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

import org.mmadt.language.LanguageException;

import javax.script.ScriptEngineFactory;
import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface mmADTScriptEngineFactory extends ScriptEngineFactory {

    @Override
    public default Object getParameter(String key) {
        throw new LanguageException(this.getClass().getCanonicalName() + " needs to implements getParameter(String key)");
    }

    @Override
    public default List<String> getExtensions() {
        return List.of("txt");
    }

    @Override
    public default List<String> getNames() {
        return List.of(this.getLanguageName());
    }

    @Override
    public default List<String> getMimeTypes() {
        return List.of("txt");
    }

    @Override
    public default String getOutputStatement(String toDisplay) {
        return toDisplay;
    }

    @Override
    public default String getEngineName() {
        return this.getLanguageName();
    }

    @Override
    public default String getEngineVersion() {
        return this.getLanguageVersion();
    }

    @Override
    public mmADTScriptEngine getScriptEngine();

}
