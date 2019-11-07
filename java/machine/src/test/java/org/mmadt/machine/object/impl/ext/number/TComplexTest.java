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

package org.mmadt.machine.object.impl.ext.number;

import org.junit.jupiter.api.Test;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.model.atomic.Real;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.type.Bindings;
import org.mmadt.processor.util.FastProcessor;

import java.util.Optional;

import static org.mmadt.language.__.map;
import static org.mmadt.language.__.plus;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TComplexTest {

    @Test
    void testComplex() {
        final Lst<Real> complex = TLst.of(TReal.some().label("x"), TReal.some().label("xi")).
                inst(plus(TLst.of(TReal.some().label("y"), TReal.some().label("yi"))).bytecode(),
                        map(TLst.of()).put(TInt.of(0), map(TReal.some().label("x")).plus(TReal.some().label("y"))).put(TInt.of(1), map(TReal.some().label("xi")).plus(TReal.some().label("yi"))).bytecode());
        System.out.println(complex);
        Bindings bindings = new Bindings();
        bindings.put("x", TReal.of(2.0f));
        bindings.put("xi", TReal.of(3.0f));
        final Optional<Inst> match = complex.inst(bindings, plus(TLst.of(TReal.of(4.0f), TReal.of(5.0f))).bytecode());
        System.out.println(match);
        new FastProcessor<>(match.get()).iterator(TReal.of(1.0)).forEachRemaining(System.out::println);
    }
}
