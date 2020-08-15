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

package org.mmadt.language;

import org.mmadt.VmException;
import org.mmadt.language.obj.Inst;
import org.mmadt.language.obj.Lst;
import org.mmadt.language.obj.Obj;
import org.mmadt.language.obj.Rec;
import org.mmadt.language.obj.type.Type;
import org.mmadt.language.obj.type.__;
import org.mmadt.language.obj.value.Value;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class LanguageException extends VmException {

    public LanguageException(final String message) {
        super(message);
    }

    public boolean equals(final Object other) {
        return other instanceof LanguageException && ((LanguageException) other).getMessage().equals(this.getMessage());
    }

    public int hashCode() {
        return this.getMessage().hashCode();
    }

    public static LanguageException parseError(final String message, final String source, final int row, final int column) {
        final String rowString = source.split("\n")[row - 1];
        final String rowSubstring = rowString.substring(Math.max(0, column - 10), Math.min(rowString.length(), column + 10));
        final String prefix = message + " at " + row + ":" + column;
        return new LanguageException(prefix + "\n" + rowSubstring + "\n" + Stream.generate(() -> " ").limit(Math.min(rowSubstring.length(), column) - 1).reduce((a, b) -> a + b).orElse("") + "^ near here");
    }

    public static void checkAnonymousTypeName(final Obj obj, final String name) {
        if (!(obj instanceof __) && name.equals(Tokens.anon()))
            throw new LanguageException(obj + " is not an anonymous type");
    }

    public static LanguageException typingError(final Obj source, final Type<?> target) {
        return new LanguageException(source + " is not " + (target.toString().matches("^[aeiouAEIOU].*") ? "an " : "a ") + target);
    }

    public static LanguageException unknownInstruction(final String op, final List<Obj> args) {
        return new LanguageException("[" + op + args.stream().map(Obj::toString).reduce("", (a, b) -> a + "," + b) + "] is an unknown instruction");
    }

    public static LanguageException unsupportedInstType(final Obj start, final Inst<?, ?> inst) {
        return new LanguageException(start + " is not supported by " + inst);
    }

    public static LanguageException typeError(final Obj source, final String message) {
        return new LanguageException(source + " instruction error: " + message);
    }

    public static LanguageException typeNoGround(final Obj source) {
        return new LanguageException("types are not grounded: " + source);
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

    public static void testTypeCheck(final Obj obj, Type<?> type) {
        if (!obj.range().test(type.domain()))
            throw LanguageException.typingError(obj, type);
    }

    public static boolean testIndex(final Lst<?> lst, final int index) {
        return !(index < 0) && lst.size() >= (index + 1);
    }

    public static class PolyException {
        public static LanguageException noHead() {
            return new LanguageException("empty polys do not have heads");
        }

        public static LanguageException noLast() {
            return new LanguageException("empty polys do not have lasts");
        }

        public static LanguageException noTail() {
            return new LanguageException("empty polys do not have tails");
        }

        public static void testIndex(final Lst<?> lst, final int index) {
            if (index < 0)
                throw new LanguageException("poly index must be 0 or greater: " + index);
            if (lst.size() < (index + 1))
                throw new LanguageException("poly index is out of bounds: " + index);
        }

        public static LanguageException noKeyValue(final Rec<?, ?> rec, final Obj key) {
            return new LanguageException("key doesn't match any rec keys: " + key);
        }
    }
}
