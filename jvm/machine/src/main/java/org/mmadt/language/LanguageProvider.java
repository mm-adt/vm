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

package org.mmadt.language;

import org.mmadt.VmException;
import org.mmadt.language.jsr223.mmADTScriptEngine;
import org.mmadt.language.model.Model;
import org.mmadt.language.obj.Obj;

import java.util.Optional;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface LanguageProvider {

    String name();

    Model model();

    Optional<mmADTScriptEngine> getEngine();

    default <O extends Obj> O parse(final String script) throws VmException {
        try {
            return (O) getEngine().get().eval(script);
        } catch (VmException e) {
            throw e;
        } catch (Exception e) {
            throw new LanguageException(e.getMessage());
        }
    }

}
