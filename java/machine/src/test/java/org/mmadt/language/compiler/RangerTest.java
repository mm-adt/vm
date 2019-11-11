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
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class RangerTest {

    private final static Model MODEL = TModel.of("ex");

    @Test
    void testRanges() {
        assertEquals(TInt.some(), Ranger.getRange(TInst.of(Tokens.ID), TInt.some(), MODEL));
        assertEquals(TInt.some(2), Ranger.getRange(TInst.of(Tokens.ID).q(2), TInt.some(), MODEL));
        //
        // assertEquals(TInt.some().q(qmark), Ranger.getRange(TInst.of(Tokens.IS,TInt.some()), TInt.some(), MODEL));
        // assertEquals(TInt.some().q(0, 10), Ranger.getRange(TInst.of(Tokens.IS,TInt.some(2)), TInt.some(10), MODEL));
//        assertEquals(TInt.some().q(0, 30), Ranger.getRange(TInst.of(Tokens.IS,TInt.some(10)).q(3), TInt.some(10), MODEL));
        //
        // assertEquals(TInt.some(3), Ranger.getRange(TInst.of(Tokens.PLUS,TInt.some(3)), TInt.some(3), MODEL));
        // assertEquals(TInt.some(12), Ranger.getRange(TInst.of(Tokens.PLUS,TInt.some(3)).q(4), TInt.some(3), MODEL));
        //
        //assertEquals(TInt.some(4), Ranger.getRange(TInst.of(Tokens.MULT,TInt.some(4)), TInt.some(4), MODEL));
        // assertEquals(TInt.some(-8), Ranger.getRange(TInst.of(Tokens.MULT,TInt.some(4)).q(-2), TInt.some(4), MODEL));
        // //
        //assertEquals(TStr.some().q(5), Ranger.getRange(TInst.of(Tokens.MINUS,TInt.some(5)), TStr.some().q(5), MODEL));
        // assertEquals(TStr.some().q(50), Ranger.getRange(TInst.of(Tokens.MINUS,TInt.some(5)).q(10), TStr.some().q(5), MODEL));
        //
        assertEquals(TStr.none(), Ranger.getRange(TInst.of(Tokens.RANGE, 0, 2), TStr.none(), MODEL));
        assertEquals(TStr.some(), Ranger.getRange(TInst.of(Tokens.RANGE, 0, 2), TStr.some(), MODEL));
        assertEquals(TInt.some(2), Ranger.getRange(TInst.of(Tokens.RANGE, 0, 2), TInt.some(2), MODEL));
        assertEquals(TInt.some(2), Ranger.getRange(TInst.of(Tokens.RANGE, 0, 2), TInt.some(3), MODEL));
        assertEquals(TInt.some(5), Ranger.getRange(TInst.of(Tokens.RANGE, 3, 5), TInt.some(6), MODEL));
        assertEquals(TInt.some(4), Ranger.getRange(TInst.of(Tokens.RANGE, 3, 5), TInt.some(4), MODEL));
        assertEquals(TInt.some(3), Ranger.getRange(TInst.of(Tokens.RANGE, 3, 5), TInt.some(3), MODEL));
        // assertEquals(TInt.none(), Ranger.getRange(TInst.of(Tokens.RANGE, 3, 5), TInt.some(2), MODEL));
    }
}
