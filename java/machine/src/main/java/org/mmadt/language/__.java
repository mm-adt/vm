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

package org.mmadt.language;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class __ {


    public static Traversal a(final Object type) {
        return create().a(type);
    }

    public static Traversal branch(final Object... branches) {
        return create().branch(branches);
    }

    public static Traversal count() {
        return create().count();
    }

    public static Traversal drop(final Object key) {
        return create().drop(key);
    }

    public static Traversal start(final Object... starts) {
        return create().start(starts);
    }

    public static Traversal eq(final Object object) {
        return create().eq(object);
    }

    public static Traversal get(final Object key) {
        return create().get(key);
    }

    public static Traversal groupCount(final Object key) {
        return create().groupCount(key);
    }

    public static Traversal gt(final Object object) {
        return create().gt(object);
    }

    public static Traversal id() {
        return create().id();
    }

    public static Traversal is(final Object bool) {
        return create().is(bool);
    }

    public static Traversal map(final Object object) {
        return create().map(object);
    }

    public static Traversal minus(final Object object) {
        return create().minus(object);
    }

    public static Traversal mult(final Object object) {
        return create().mult(object);
    }

    public static Traversal one() {
        return create().one();
    }

    public static Traversal plus(final Object object) {
        return create().plus(object);
    }

    public static Traversal put(final Object key, final Object value) {
        return create().put(key, value);
    }

    public static Traversal sum() {
        return create().sum();
    }

    public static Traversal type() {
        return create().type();
    }

    public static Traversal zero() {
        return create().zero();
    }

    ///

    private static Traversal create() {
        return new Traversal();
    }

}
