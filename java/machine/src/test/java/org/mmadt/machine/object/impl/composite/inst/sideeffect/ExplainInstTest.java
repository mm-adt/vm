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

package org.mmadt.machine.object.impl.composite.inst.sideeffect;

import org.junit.jupiter.api.Test;
import org.mmadt.machine.object.impl.TSym;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.processor.util.FastProcessor;

import static org.mmadt.machine.object.impl.__.gt;
import static org.mmadt.machine.object.impl.__.id;
import static org.mmadt.machine.object.impl.__.plus;


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ExplainInstTest {

    @Test
    void testVisually() {
        FastProcessor.process(TInt.of().plus(15).is(TInt.of().mult(TInt.of().plus(22).mult(id())).gt(2)).lt(17).is(TBool.of().eq(true)).explain()).forEachRemaining(obj -> {
        });

        /*FastProcessor.process(TInt.of().state().<Int>write(TInt.of(55).label("x")).<Int>set(null).plus(TSym.of("x")).is(TInt.of().mult(TInt.of().plus(22).mult(id())).gt(2)).lt(TSym.of("x")).is(TBool.of().eq(true)).explain()).forEachRemaining(obj -> {
        });

        FastProcessor.process(TInt.of().state().<Int>write(TInt.of().label("x")).plus(TSym.of("x")).is(TInt.of().mult(TInt.of().plus(22).mult(id())).gt(2)).lt(TSym.of("x")).is(TBool.of().eq(true)).explain()).forEachRemaining(obj -> {
        });*/

        FastProcessor.process(TInt.of().plus(2).mult(3).branch(TRec.of(gt(3), plus(300), gt(100), plus(23))).is(gt(1)).explain()).forEachRemaining(obj -> {
        });
    }
}
