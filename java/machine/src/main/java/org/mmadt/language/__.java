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


    public static Query a(final Object type) {
        return create().a(type);
    }

    public static Query and(final Object... branches) {
        return create().and(branches);
    }

    public static Query branch(final Object... branches) {
        return create().branch(branches);
    }

    public static Query count() {
        return create().count();
    }

    public static Query drop(final Object key) {
        return create().drop(key);
    }

    public static Query start(final Object... starts) {
        return create().start(starts);
    }

    public static Query eq(final Object object) {
        return create().eq(object);
    }

    public static Query get(final Object key) {
        return create().get(key);
    }

    public static Query groupCount(final Object key) {
        return create().groupCount(key);
    }

    public static Query gt(final Object object) {
        return create().gt(object);
    }

    public static Query gte(final Object object) {
        return create().gte(object);
    }

    public static Query id() {
        return create().id();
    }

    public static Query is(final Object bool) {
        return create().is(bool);
    }

    public static Query lt(final Object object) {
        return create().lt(object);
    }

    public static Query lte(final Object object) {
        return create().lte(object);
    }

    public static Query map(final Object object) {
        return create().map(object);
    }

    public static Query minus(final Object object) {
        return create().minus(object);
    }

    public static Query mult(final Object object) {
        return create().mult(object);
    }

    public static Query one() {
        return create().one();
    }

    public static Query or(final Object... branches) {
        return create().or(branches);
    }

    public static Query plus(final Object object) {
        return create().plus(object);
    }

    public static Query put(final Object key, final Object value) {
        return create().put(key, value);
    }

    public static Query reduce(final Object seed, final Object reduce) {
        return create().reduce(seed, reduce);
    }

    public static Query sum() {
        return create().sum();
    }

    public static Query type() {
        return create().type();
    }

    public static Query zero() {
        return create().zero();
    }

    ///

    private static Query create() {
        return new Query();
    }

}
