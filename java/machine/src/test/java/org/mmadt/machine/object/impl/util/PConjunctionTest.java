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

import org.junit.jupiter.api.Test;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.TSym;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.type.Bindings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.machine.object.model.composite.Q.Tag.star;
import static org.mmadt.machine.object.model.composite.Q.Tag.qmark;
import static org.mmadt.machine.object.model.composite.Q.Tag.zero;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class PConjunctionTest {
    @Test
    void shouldToString() {
        assertEquals("'marko'", TStr.of("marko").or(TStr.of("marko")).toString());
        assertEquals("'marko'{+}", TStr.of("marko").q(star).or(TStr.of("marko")).toString());
        assertEquals("'marko'{*}", TStr.of("marko").q(star).or(TStr.of("marko").q(zero)).toString());
        assertEquals("'marko'{0}", TStr.of("marko").q(zero).or(TStr.of("marko").q(zero)).toString());
        //
        assertEquals("'marko'", TStr.of("marko").and(TStr.of("marko")).toString());
        assertEquals("'marko'{*}", TStr.of("marko").q(star).and(TStr.of("marko")).toString());
        assertEquals("'marko'{0}", TStr.of("marko").q(star).and(TStr.of("marko").q(zero)).toString());
        //
        assertEquals("'marko'|'stephen'", TStr.of("marko").or(TStr.of("stephen")).toString());
        assertEquals("'marko'{?}|'stephen'", TStr.of("marko").q(qmark).or(TStr.of("stephen")).toString());
        // assertEquals("'marko'{?}|'stephen'&gt('abc'){2}", TStr.of("marko").q(qmark).or(TStr.of("stephen")).and(TStr.some().gt("abc").q(2)).toString());
        assertEquals("gt(32)", TInt.some().and(TInt.gt(32)).toString()); // TODO: is this what we want?
        // assertEquals("'marko'{?}|'stephen'&(gt('abc'){2}|bool{?})", TStr.of("marko").q(qmark).or(TStr.of("stephen")).and(TStr.some().gt("abc").q(2).or(TBool.some().q(qmark))).toString());
        //
        assertEquals("gt(32){*}", TInt.some().q(star).and(TInt.gt(32)).toString()); // TODO: is this what we want?
        assertEquals("gt(32){*}", TInt.some().and(TInt.gt(32).q(star)).toString()); // TODO: is this what we want?
    }

    @Test
    void shouldMatchConjunctions() {
        final TSym<Rec<TStr, TObj>> vertex = TSym.of("vertex", TRec.of("id", TInt.some(), "label", TStr.some()));
        final Rec<TStr, TObj> idField = TRec.of("id", TInt.some().as("x"));
        final Bindings bindings = new Bindings();
        assertTrue(vertex.and(idField).match(bindings, vertex.and(TRec.of("id", 2))));
        assertEquals(TInt.of(2), bindings.get("x"));
    }

    @Test
    void shouldMatchConjunctionInstruction() {
        TSym<Rec<TStr, TObj>> vertex = TSym.of("vertex", TRec.of("id", TInt.some(), "label", TStr.some()));
        final Rec<TStr, TObj> idField = TRec.of("id", TInt.some().as("x"));
        final Obj and = vertex.and(idField);
        final Inst inst = TInst.of("eq", and);
        vertex = vertex.inst(inst, TInst.of("get", "id").mult(TInst.of("eq", TInt.some().as("x"))));
        //
        final Bindings bindings = new Bindings();
        assertTrue(vertex.and(idField).match(bindings, vertex.and(TRec.of("id", 2))));
        assertEquals(TInt.of(2), bindings.get("x"));
    }


}
