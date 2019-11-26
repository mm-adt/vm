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
import org.mmadt.machine.object.impl.composite.TQ;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.language.__.mult;
import static org.mmadt.language.__.plus;
import static org.mmadt.language.__.start;
import static org.mmadt.machine.object.model.composite.Q.Tag.qmark;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class SumTest extends AbstractTest {

    @Test
    void startX3_2_1X_multX2X_sum() {
        assertEquals(qs(12), submit(start(3, 2, 1).mult(2).sum()));
    }

    //@Test
    void xxx_sum() {
        assertEquals(List.of(new TQ<>(TInt.of(2).access(plus(TInt.of().q(1, 3).access(mult(1)))), TInt.of(7).access(plus(TInt.of().q(1, 3).access(mult(3)))))),
                submit(start(1, 1, TInt.of().q(1, 3), TInt.of(5).q(qmark)).sum()));
    }

}