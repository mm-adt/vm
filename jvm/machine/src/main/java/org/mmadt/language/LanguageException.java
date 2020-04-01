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
import org.mmadt.language.obj.Obj;
import org.mmadt.language.obj.type.Type;
import org.mmadt.language.obj.type.__;
import org.mmadt.language.obj.value.Value;
import org.mmadt.storage.StorageFactory;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class LanguageException extends VmException {

    public LanguageException(final String message) {
        super(message);
    }

    public static LanguageException typeError(final Obj source, final Type<?> target) {
        return new LanguageException(target + " is not a " + source);
    }

    public static void testDomainRange(final Type<?> range, final Type<?> domain) {
        if (!(domain instanceof __) &&
                !range.range().q(StorageFactory.qOne()).test(domain.range().q(StorageFactory.qOne())))
            throw LanguageException.typeError(range, domain);
    }

    public static void testTypeCheck(final Obj obj, Type<Obj> type) {
        if ((obj instanceof Type && !((Type<Obj>) obj).range().test(type)) || (obj instanceof Value && !obj.test(type)))
            throw LanguageException.typeError(obj, type);
    }
}
