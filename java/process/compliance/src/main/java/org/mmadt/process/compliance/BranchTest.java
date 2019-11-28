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

package org.mmadt.process.compliance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.language.__.gt;
import static org.mmadt.language.__.id;
import static org.mmadt.language.__.is;
import static org.mmadt.language.__.plus;
import static org.mmadt.language.__.start;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class BranchTest extends AbstractTest {

    @Test
    void startX1X_branchXidX() {
        assertEquals(objs(1), submit(start(1).branch(id())));
    }

    @Test
    void startX1X_branchXid__idX() {
        assertEquals(objs(1, 1), submit(start(1).branch(id(), id())));
    }

    @Test
    void startX0_1_2X_branchXplusX1X__plusX2X_plusXn1X_plusX1X_isXaXboolXX_multX2X_plusX0X() {
        assertEquals(objs(4, 6, 6, 8), submit(start(0, 1, 2).branch(is(gt(1)).plus(1), plus(2).plus(-1).plus(1), is(false)).mult(2).plus(0)));
    }
}
