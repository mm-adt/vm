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

package org.mmadt;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.processor.util.FastProcessor;
import org.mmadt.util.IteratorUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface TestUtilities {

    public default <E extends Obj> List<E> submit(final E obj) {
        return IteratorUtils.list(FastProcessor.process(obj));
    }

    public default void validateTypes(final Obj obj) {
        final List<Obj> list = List.of(TBool.of(), TInt.of(), TReal.of(), TStr.of(), TLst.some(), TRec.some());
        for (final Obj o : list) {
            if (obj.getClass().equals(o.getClass())) {
                assertEquals(o, obj);
                assertEquals(o.symbol(), obj.symbol());
            } else {
                assertNotEquals(o, obj);
                assertNotEquals(o.symbol(), obj.symbol());
            }
        }
        assertTrue(obj.access().isOne());
        assertTrue(obj.a(TObj.all()).java());
    }

    public default void validateIsA(final Obj obj) {
        final List<Obj> list = List.of(TBool.of(true), TInt.of(1), TReal.of(1.0), TStr.of("a"), TLst.of("a", 1), TRec.of("a", 1));
        for (final Obj o : list) {
            if (obj.getClass().equals(o.getClass())) {
                assertTrue(o.a(obj).java());
            } else {
                assertFalse(o.a(obj).java());
            }
        }
        assertTrue(obj.a(obj).java());
    }
}
