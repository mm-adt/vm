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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.language.__.gt;
import static org.mmadt.language.__.start;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class CountTest extends AbstractTest {

    @Test
    void start_count() {
        assertEquals(qs(0), submit(start().count()));
    }

    @Test
    void startXa_b_cX_count() {
        assertEquals(qs(3), submit(start("a", "b", "c").count()));
    }

    @Test
    void startXa_b_cX_count_count() {
        assertEquals(qs(1), submit(start("a", "b", "c").count().count()));
    }

    @Test
    void startXa_b_cX_isXgtXaXX_count() {
        assertEquals(qs(2), submit(start("a", "b", "c").is(gt("a")).count()));
    }


}
