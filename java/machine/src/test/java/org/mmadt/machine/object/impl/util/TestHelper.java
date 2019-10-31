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

package org.mmadt.machine.object.impl.util;

import org.mmadt.machine.object.model.Obj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class TestHelper {

    private TestHelper() {
        // static helper class
    }

    public static void validateKinds(final Obj instance, final Obj reference, final Obj type) {
        assertEquals(instance.getClass(),reference.getClass());
        assertEquals(reference.getClass(),type.getClass());

        assertTrue(instance.isInstance());
        assertFalse(instance.isReference());
        assertFalse(instance.isType());

        assertFalse(reference.isInstance());
        assertTrue(reference.isReference());
        assertFalse(reference.isType());

        assertFalse(type.isInstance());
        assertFalse(type.isReference());
        assertTrue(type.isType());
    }
}
