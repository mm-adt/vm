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

package org.mmadt.util;

import org.mmadt.object.impl.TStream;
import org.mmadt.object.model.Obj;
import org.mmadt.processor.function.FilterFunction;
import org.mmadt.processor.function.FlatMapFunction;
import org.mmadt.processor.function.MapFunction;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class FunctionUtils {

    private FunctionUtils() {
        // static helper class
    }

    public static <S extends Obj> Optional<S> test(final FilterFunction<S> predicate, final S object) {
        return predicate.test(object) ? Optional.of(object.q(object.q().and(predicate.quantifier())).as(predicate.label())) : Optional.empty();
        // TODO: test to make sure quantifier is not 1 (save clock cycles)
    }

    public static <S extends Obj, E extends Obj> Iterator<E> flatMap(final FlatMapFunction<S, E> function, final S object) {
        return IteratorUtils.map(function.apply(object), e -> e.q(e.q().and(function.quantifier())).as(function.label()));
        // TODO: test to make sure quantifier is not 1 (save clock cycles)
    }

    public static <S extends Obj, E extends Obj> E map(final MapFunction<S, E> function, final S object) {
        return function.apply(object).q(object.q().and(function.quantifier())).as(function.label());
        // TODO: test to make sure quantifier is not 1 (save clock cycles)
    }

    public static <T extends Obj, U> T monad(final T obj, final UnaryOperator<U> operator) {
        return obj.set(TStream.check(IteratorUtils.stream((Iterable<T>) obj.iterable()).
                <T>map(x -> x.set(operator.apply(x.get()))).collect(Collectors.toList())));
    }

    public static <T extends Obj, U> T monad(final T objA, final T objB, final BinaryOperator<U> operator) {
        return objA.set(TStream.check(IteratorUtils.stream((Iterable<T>) objA.iterable()).
                <T>map(x -> x.set(operator.apply(x.get(), objB.head().get()))).collect(Collectors.toList())));
    }
}
