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

package org.mmadt.language.mmlang.model;

import org.junit.jupiter.api.Test;
import org.mmadt.language.compiler.Rewriting;
import org.mmadt.language.mmlang.Compiler;
import org.mmadt.object.impl.TModel;
import org.mmadt.object.impl.TObj;
import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.impl.composite.TInst;
import org.mmadt.object.model.Model;
import org.mmadt.object.model.composite.Inst;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TypingTest {

    static Inst verifyTyping(final Inst bc) {
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
        assertEquals(bc.range(), bc.<Inst>last().range());
        // assertEquals(bc.range(), bc.<Inst>tail().range());
        return bc;
    }

    @Test
    void testRewriteSimple() {
        final TModel model = TModel.of(Compiler.asInst("[define,db,['persons':['name':str,'age':int]{*}]]"));
        final Inst oldBc = Compiler.asInst("[db][get,'persons'][is,[get,'name'][eq,'marko']][get,'age'][gt,29]");
        final Inst newBc = model.query(oldBc);
        assertEquals(oldBc, newBc); // no rewrites so the bytecode shouldn't change
        verifyTyping(newBc);
    }

    @Test
    void testRewrite() {
        final TModel model = TModel.of(Compiler.asInst("[define,db,['persons':['name':str,'age':int]{*}\n" +
                " -> [is,[get,'name'][eq,str~x]] => [ref,['name':str~x,'age':int]{?} <= [db][get,'persons'][is,[get,'name'][eq,str~x]]]]]"));
        final Inst oldBc = Compiler.asInst("[db][get,'persons'][is,[get,'name'][eq,'marko']][get,'age'][gt,29]");
        final Inst newBc = model.query(oldBc);
        assertEquals("[ref,['name':'marko','age':int]{?} <= [db][get,'persons'][is,[get,'name'][eq,'marko']]][get,'age'][gt,29]", newBc.toString());
        verifyTyping(newBc);
    }

    @Test
    void testBC1() {
        final TModel model = TModel.of(Compiler.asInst(TypingTest.class.getResourceAsStream("db1.mm")));
        final Inst oldBc = Compiler.asInst(model, Compiler.asString(TypingTest.class.getResourceAsStream("bc1.mm")));
        final Inst newBc = model.query(oldBc);
        verifyTyping(newBc);
    }

    @Test
    void testBC2() {
        Model model = TModel.of(Compiler.asInst(TypingTest.class.getResourceAsStream("social.mm")));
        final Inst oldBc = Compiler.asInst(TypingTest.class.getResourceAsStream("bc2.mm"));
        final Inst newBc = Rewriting.rewrite(model, oldBc);
        assertFalse(newBc.toString().contains("~x"));
        System.out.println(oldBc);
        System.out.println(newBc);
        assertEquals(1L, java.util.stream.Stream.of(newBc).count());
        assertEquals(TInst.of("start", TInt.of(50)), newBc.head());
        verifyTyping(newBc);
    }

    @Test
    void testBC3() {
        Model model = TModel.of(Compiler.asInst(TypingTest.class.getResourceAsStream("pg.mm")));
        final Inst oldBc = Compiler.asInst(TypingTest.class.getResourceAsStream("bc3.mm"));
        final Inst newBc = Rewriting.rewrite(model, oldBc);
        verifyTyping(newBc);
        final Inst newBc2 = Rewriting.rewrite(model, newBc);
        verifyTyping(newBc2);
        final Inst newBc3 = Rewriting.rewrite(model, newBc2);
        verifyTyping(newBc3);
    }
}
