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

package org.mmadt.util;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TestArgs<A, B> {
    public final boolean ignore;
    public final A expected;
    public final B input;

    public TestArgs(final B input) {
        this((A) input, input);
    }

    public TestArgs(final boolean ignore, final B input) {
        this(ignore, (A) input, input);
    }

    public TestArgs(final A expected, final B input) {
        this(false, expected, input);
    }

    public TestArgs(final boolean ignore, final A expected, final B input) {
        this.ignore = ignore;
        this.expected = expected;
        this.input = input;
    }
}
