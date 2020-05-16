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

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class LanguageException extends VmException {

    public LanguageException(final String message) {
        super(message);
    }

    public static LanguageException parseError(final String message, final String source, final int row, final int column) {
        final String rowString = source.split("\n")[row - 1];
        final String rowSubstring = rowString.substring(Math.max(0, column - 10), Math.min(rowString.length(), column + 10));
        final String prefix = message + " at " + row + ":" + column;
        return new LanguageException(prefix + "\n" + rowSubstring + "\n" + Stream.generate(() -> " ").limit(Math.min(rowSubstring.length(), column) - 1).reduce((a, b) -> a + b).orElse("") + "^ near here");
    }

    public static LanguageException typingError(final Obj source, final Type<?> target) {
        return new LanguageException(source + " is not " + (target.toString().matches("^[aeiouAEIOU].*") ? "an " : "a ") + target);
    }

    public static LanguageException unknownInstruction(final String op, final List<Obj> args) {
        return new LanguageException("[" + op + args.stream().map(Obj::toString).reduce("", (a, b) -> a + "," + b) + "] is an unknown instruction");
    }

    public static LanguageException typeError(final Obj source, final String message) {
        return new LanguageException(source + " instruction error: " + message);
    }

    public static LanguageException typeNoGround(final Obj source) {
        return new LanguageException("Types are not grounded: " + source);
    }

    public static LanguageException zeroLengthPath(final Obj source) {
        if (source instanceof Type<?>)
            return new LanguageException(source + " can not be decomposed beyond canonical form");
        else {
            assert (source instanceof Value<?>);
            return new LanguageException(source + " does not have an earlier computational state");
        }
    }

    public static LanguageException labelNotFound(final Obj source, final String label) {
        return new LanguageException(source + " does not contain the label '" + label + "'");
    }

    public static void testDomainRange(final Type<?> range, final Type<?> domain) {
        if (!(domain instanceof __) &&
                !range.range().q(StorageFactory.qOne()).test(domain.range().q(StorageFactory.qOne())))
            throw LanguageException.typingError(range, domain);
    }

    public static void testTypeCheck(final Obj obj, Type<?> type) {
        if (!StorageFactory.asType(obj).range().test(((type instanceof __) ? ((__) type).apply(obj) : type).domain()))
            throw LanguageException.typingError(obj, type);
    }
}
