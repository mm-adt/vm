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

package org.mmadt.object.impl.type.feature;

import org.junit.jupiter.api.Test;
import org.mmadt.language.compiler.Tokens;
import org.mmadt.object.impl.atomic.TBool;
import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.impl.atomic.TReal;
import org.mmadt.object.impl.atomic.TStr;
import org.mmadt.object.impl.composite.TInst;
import org.mmadt.object.impl.composite.TLst;
import org.mmadt.object.model.type.feature.WithSemigroupPlus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class WithSemigroupPlusTest {

    @Test
    void shouldSupportSemigroupAxioms() {
        List.<WithSemigroupPlus>of(TLst.of(1), TInst.of(Tokens.ID), TStr.of("a"), TInt.of(1), TReal.of(1.0f), TBool.of(true)).forEach(semigroup -> {
            final WithSemigroupPlus two = semigroup.plus(semigroup);
            final WithSemigroupPlus three = two.plus(semigroup);
            final WithSemigroupPlus four = three.plus(semigroup);
            assertFalse(semigroup.isZero());
            assertTrue(semigroup.zero().isZero());
            // 0 + 0 = 0
            assertEquals(semigroup.zero(), semigroup.zero().plus(semigroup.zero()));
            // x + 0 = x
            assertEquals(two, two.plus(semigroup.zero()));
            // 0 + x = x
            assertEquals(two, semigroup.zero().plus(two));
            // (a+b)+c = a+(b+c)
            assertEquals(two.plus(three).plus(four), two.plus(three.plus(four)));
        });


    }
}
