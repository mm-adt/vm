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

package org.mmadt.language.mmlang.util;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TestArgs {
    public final boolean ignore;
    public final String expected;
    public final String input;
    public final Class<? extends Exception> ex;

    public TestArgs(final String input) {
        this(input, input);
    }

    public TestArgs(final boolean ignore, final String input) {
        this(ignore, input, input, null);
    }

    public TestArgs(final String expected, final String input) {
        this(false, expected, input, null);
    }

    public TestArgs(final String expected, final String input, final Class<? extends Exception> ex) {
        this(false, expected, input, ex);
    }

    public TestArgs(final boolean ignore, final String expected, final String input) {
        this(ignore, expected, input, null);
    }

    public TestArgs(final boolean ignore, final String expected, final String input, final Class<? extends Exception> ex) {
        this.ignore = ignore;
        this.expected = expected;
        this.input = input;
        this.ex = ex;
    }
}
