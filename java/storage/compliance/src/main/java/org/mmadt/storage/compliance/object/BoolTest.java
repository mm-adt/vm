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

package org.mmadt.storage.compliance.object;

import org.junit.jupiter.api.Test;
import org.mmadt.machine.object.impl.TObjFactory;
import org.mmadt.machine.object.model.ObjFactory;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.type.algebra.WithOrderedRing;
import org.mmadt.storage.compliance.util.TestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.language.__.start;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class BoolTest<A extends WithOrderedRing<A>> {

    @Test
    void testCoreFactory() {
        this.testInstanceReferenceType(TObjFactory.of());
        this.testQuantifiers(TObjFactory.of());
    }

    void testInstanceReferenceType(final ObjFactory<A> o) {
        final ObjFactory.BoolFactory bools = o.bools();
        assertTrue(bools.quantifier() instanceof Int);
        //////
        Bool instance = bools.of(true);
        Bool reference = bools.of(true, false, true, false);
        Bool type = bools.star();
        TestHelper.validateKinds(instance, reference, type);
        //////
        instance = bools.of(false).q(2);
        reference = bools.of(true, false, true, false);
        type = bools.of().q(45);
        TestHelper.validateKinds(instance, reference, type);
        //////
        instance = bools.of(false).neg();
        reference = bools.of(true, false).q(2, 10);
        type = bools.one();
        TestHelper.validateKinds(instance, reference, type);
    }

    void testQuantifiers(final ObjFactory<A> o) {
        final ObjFactory.BoolFactory<A> bools = o.bools();
        final WithOrderedRing<A> q = o.quantifier();

        assertEquals(q.one(), bools.of(true).q().object());
        assertEquals(TestHelper.incr(q.one(), 1), bools.of(true, false).q().object());
        assertEquals(TestHelper.incr(q.one(), 2), bools.of(true, false, true).q().object());
    }
}
