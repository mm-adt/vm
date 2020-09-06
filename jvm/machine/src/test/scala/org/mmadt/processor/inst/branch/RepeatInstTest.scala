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

package org.mmadt.processor.inst.branch

import org.mmadt.language.obj.Obj.intToInt
import org.mmadt.language.obj.`type`.__._
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory.int

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class RepeatInstTest extends BaseInstTest(

  testSet("[repeat]",
    comment("times"),
    testing(5, repeat(plus(1))(10), 15, "5[repeat,+1,10]"),
    testing(int(5, 6), repeat(plus(1))(10), int(15, 16), "[5,6][repeat,+1,10]"),
    testing(0, int.repeat(plus(2).mult(5))(10), 24414060, "0 => int[repeat,[plus,2][mult,5],10]"),
    comment("checking"),
    testing(5, repeat(plus(1))(is(lt(10))), 10, "5[repeat,+1,is<10]"),
    testing(int(5, 6), repeat(plus(1))(is(lt(10))), 10.q(2), "[5,6](+1)^(is<10)"),
    testing(int(5, 6), repeat(plus(2))(is(lt(10))), int(10, 11), "[5,6](+2)^(is<10)"),
    comment("quantifiers"),
    testing(5, repeat(plus(1))(3).q(10), 8.q(10), "5[repeat,[plus,1],3]{10}"),
    testing(5.q(2), repeat(plus(1))(3).q(10), 8.q(20), "5{2}[repeat,[plus,1],3]{10}"),
    testing(5.q(2), repeat(plus(1).q(2))(3).q(10), 8.q(160), "5{2}[repeat,[plus,1]{2},3]{10}"),
    testing(6, int.repeat(plus(1))(int.is(lt(9))).q(10), 9.q(10), "6[repeat,[plus,1],int[is<9]]{10}"),
    testing(6, int.repeat(plus(1).q(2))(int.is(lt(9))).q(10), 9.q(80), "6[repeat,[plus,1]{2},int[is<9]]{10}"),
    testing(6.q(3), int.q(3).repeat(plus(1).q(2))(int.is(lt(9))).q(10), 9.q(240), "6{3} => int{3}[repeat,[plus,1]{2},int[is<9]]{10}"),
    comment("vars"),
    testing(7, int.to('x).repeat(int.to('y).plus(int.from('x)))(int.is(lt(10))).mult(from('x)), 98, "7 => int<x>[repeat,int<y>[plus,int<.x>],int[is<10]][mult,x]"),
    testing(8, to('x).repeat(to('y).plus(from('x)))(is(lt(10))).mult(from('x)), 128, "8<x>[repeat,<y>[plus,x],is<10][mult,x]"),
    testing(int(5, 6), to('x).repeat(to('y).plus(from('x)))(is(lt(10))).mult(from('x)), int(50, 72), "[5,6]<x>[repeat,<y>[plus,x],is<10][mult,x]"),
    comment("branching"),
    testing(2, int.repeat(branch(plus(1) `,` mult(2)))(1).plus(10), int(13, 14), "2 => int([+1,*2])^(1)[plus,10]"),
    testing(2, int.repeat(branch(plus(1) `,` mult(2)))(2).plus(10), int(14, 16, 15, 18), "2 => int([+1,*2])^(2)[plus,10]"),
    testing(2, int.plus(1).plus(-1).repeat(branch(plus(1).q(2) `,` mult(2)))(2).plus(10), int(14.q(4), 16.q(2), 15.q(2), 18), "2 => int[plus,1][plus,-1]([+{2}1,*2])^(2)[plus,10]"),
    testing(2, int.repeat(branch(plus(1) `,` mult(2)))(3).plus(10), int(15, 18, 17, 22, 16, 20, 19, 26), "2 => int([+1,*2])^(3)[plus,10]"),
  ),
)
