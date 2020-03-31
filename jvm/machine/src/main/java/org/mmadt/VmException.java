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

package org.mmadt;

import org.mmadt.language.LanguageException;
import org.mmadt.processor.ProcessorException;
import org.mmadt.storage.StorageException;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class VmException extends RuntimeException {

    public VmException(final String message) {
        super(message);
    }

    public String header() {
        if (this instanceof LanguageException) {
            return "language";
        } else if (this instanceof ProcessorException) {
            return "processor";
        } else if (this instanceof StorageException) {
            return "storage";
        } else
            throw new IllegalStateException("Unknown exception: " + this);
    }
}