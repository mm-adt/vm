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

package org.mmadt.object.impl.type;

import org.junit.jupiter.api.Test;
import org.mmadt.object.impl.TModel;
import org.mmadt.object.impl.TObj;
import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.impl.atomic.TStr;
import org.mmadt.object.impl.composite.TInst;
import org.mmadt.object.impl.composite.TRec;
import org.mmadt.object.model.composite.Inst;
import org.mmadt.object.model.type.Bindings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mmadt.object.impl.composite.TQ.star;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class InstructionsTest {

    @Test
    void shouldMergeInstructions() {
        TModel model = TModel.of("ex");
        TObj a = TRec.of("name", TStr.some()).symbol("a").inst(TInst.of("get", "outE"), TInst.of("error"));
        TObj b = TRec.of("age", TInt.some()).symbol("b").inst(TInst.of("get", "inE"), TInst.of("get", "outE"));
        model.define("a", a);
        model.define("b", b);

        System.out.println(model.get("a").toString());
        System.out.println(model.get("b").toString());
        System.out.println(a.and(b));
    }

    @Test
    void shouldSupportInstructionDefinitions() {
        final Inst bc = TInst.of("define", "ff", TInst.of("is", TObj.some())).mult(
                TInst.of("define", "db", TInt.some().q(star).inst(TInst.of("filter", TInst.some().symbol("ff")), TInst.of("get", TStr.of("name")))));
        final TModel model = TModel.of("ex").model(bc);
        //assertEquals(1, db.definitions().size());
        assertEquals(TInst.of("is", TObj.some()), model.get("ff"));
        assertNotNull(model.get("db"));
        assertEquals("db", model.sym("db").symbol());
        assertEquals(TInst.of("get", "name"), model.get("db").inst(new Bindings(), TInst.of("filter", TInst.of("is", TInst.of("eq", 32)))).get());
    }
}
