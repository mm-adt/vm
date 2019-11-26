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

package org.mmadt.language.compiler;

import org.junit.jupiter.api.Test;
import org.mmadt.machine.object.impl.TModel;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.type.Bindings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mmadt.machine.object.model.composite.Q.Tag.qmark;
import static org.mmadt.machine.object.model.composite.Q.Tag.star;


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class RewritingTest {

    static void verifyTyping(final Inst bc) {
        // instruction domain=>range
        assertEquals(bc.domain(), TObj.none());
        Inst previous = null;
        for (final Inst inst : bc.iterable()) {
            //System.out.println(bc.and().get(i).domain() + " ==> " + bc.and().get(i).range());
            assertNotEquals(inst.range(), TObj.none()); // just to make sure we are not propagating none's through
            if (null != previous)
                assertEquals(previous.range(), inst.domain());
            else
                assertEquals(TObj.none(), inst.domain());
            previous = inst;
        }
        // bytecode domain=>range
        assertEquals(bc.domain(), TObj.none());
        // assertEquals(bc.range(), bc.tail().range());
    }

    @Test
    void testBasicTyping() {
        final TModel model = TModel.of(TInst.of("define","db",TRec.of("persons", TRec.of("name", TStr.of(), "age", TInt.of()).q(star))));
        final Inst bc =
                TInst.of("db").mult(
                        TInst.of("get", "persons")).mult(
                        TInst.of("is", TInst.of("get", "name").mult(TInst.of("eq", "marko")))).mult(
                        TInst.of("get", "age")).mult(
                        TInst.of("gt", 29)).mult(
                        TInst.of("is", TInst.of("eq", true)));
        final Inst newBc = Rewriting.rewrite(model, bc);
        assertEquals(bc.toString(), newBc.toString());
        verifyTyping(newBc);
    }

    @Test
    void testReferenceRewrite() {
        // ORIGINAL: [[db][get,persons][is,[[get,name][eq,marko]]][get,age][gt,29][is,[[eq,true]]]]
        final Rec people = (TRec) TRec.of("name", TStr.of(), "age", TInt.of()).q(star).
                access(TInst.of("db").mult(TInst.of("get", "persons"))).
                inst(TInst.of("is", TInst.of("get", "name").mult(TInst.of("eq", TStr.of().label("x")))),
                        TInst.of("ref", TRec.of("name", TStr.of().label("x"), "age", TInt.of()).
                                q(qmark).
                                access(TInst.of("db").mult(TInst.of("get", "persons")).mult(TInst.of("is", TInst.of("get", "name").mult(TInst.of("eq", TStr.of().label("x"))))))));

        final Bindings bindings = new Bindings();
        final TModel model = TModel.of(TInst.of("define","db",TRec.of("persons", people).access(TInst.of("db"))));
        bindings.clear();
        final Inst bytecode =
                TInst.of("db").mult(
                        TInst.of("get", "persons")).mult(
                        TInst.of("is", TInst.of("get", "name").mult(TInst.of("eq", "marko")))).mult(
                        TInst.of("get", "age")).mult(
                        TInst.of("gt", 29)).mult(
                        TInst.of("is", TInst.of("eq", true)));
        final Inst newBc = Rewriting.rewrite(model, bytecode, bindings);
        // REWROTE: [[ref,[name:marko,age:@int]? <= [[db][get,persons][is,[[get,name][eq,marko]]]]][get,age][gt,29][is,[[eq,true]]]]
        assertEquals(
                TInst.of("ref", TRec.of("name", "marko", "age", TInt.of()).q(qmark).access(TInst.of("db").mult(TInst.of("get", "persons").mult(TInst.of("is", TInst.of("get", "name").mult(TInst.of("eq", "marko"))))))).mult(
                        TInst.of("get", "age")).mult(
                        TInst.of("gt", 29)).mult(
                        TInst.of("is", TInst.of("eq", true))).toString(), newBc.toString());
    }
}
