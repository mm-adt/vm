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

package org.mmadt.process.compliance;

import org.junit.jupiter.api.Test;
import org.mmadt.machine.object.impl.atomic.TInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.language.__.a;
import static org.mmadt.language.__.and;
import static org.mmadt.language.__.gt;
import static org.mmadt.language.__.plus;
import static org.mmadt.language.__.start;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class AndTest extends AbstractTest {

    @Test
    void startX0_1_2X_plusX1X_isXandXgtX0X__aXintX__plusX2X_gtX0XXX() {
        assertEquals(objs(2, 3), submit(start(0, 1, 2).plus(1).is(and(gt(1), a(TInt.some()), plus(2).gt(0)))));
        assertEquals(objs(2, 3), submit(start(0, 1, 2).plus(1).is(gt(1).and(a(TInt.some())).and(plus(2).gt(0)))));
    }
}
