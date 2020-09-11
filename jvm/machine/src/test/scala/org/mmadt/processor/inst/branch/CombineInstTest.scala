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

import org.mmadt.language.obj.Obj.{intToInt, tupleToRecYES}
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{testSet, _}
import org.mmadt.storage.StorageFactory._

class CombineInstTest extends BaseInstTest(
  testSet("[combine] ,-lst",
    IGNORING("eval-5")(1, -<(plus(1) `,` plus(2)).combine(mult(4) `,` plus(7)), 8 `,` 10, "1-<(+1,+2)=(*4,+7)"),
    IGNORING("eval-5")(lst(int(1)), >-.-<(plus(1) `,` plus(2)).combine(mult(4) `,` plus(7)), 8 `,` 10, "(1)>--<(+1,+2)=(*4,+7)"),
    testing(1, 8 `,` 10 `,`(16 `,` 18) /*-<(plus(1)`,`plus(2)`,`plus(3)-<(plus(4)`,`plus(5))).combine(plus(6)`,`plus(7)`,`combine(plus(8)`,`plus(9)))*/ , 8 `,` 10 `,`(16 `,` 18), "1-<(+1,+2,+3-<(+4,+5))=(+6,+7,=(+8,+9))"),
  ),
  testSet("[combine] ;-lst",
    testing(1 `;` 2 `;` 3, lst.combine(int.plus(1) `,` __ `,` int.plus(5)), 2.q(2) `,` 8, "(1;2;3)=(+1,_,+5)"),
    testing(1 `;` 2 `;` 3, lst.combine(int.plus(1) `;` int.plus(2) `;` int.plus(3)), 2 `;` 4 `;` 6, "(1;2;3)=(int+1;int+2;int+3)"),
    testing(1 `;` 2 `;` 3, lst.combine(int.plus(1) `;` int.plus(2)), 2 `;` 4 `;` 4, "(1;2;3)=> lst[combine,(int+1;int+2)]"),
    testing(1 `;` 2 `;` 3, lst.combine(plus(1) `,`), 2 `,` 3 `,` 4, "(1;2;3) => lst[combine,(+1)]"),
    testing(1 `;` 2 `;` 3, lst.combine(lst), zeroObj `,`, "(1;2;3)=lst"),
    testing(1 `;`(2 `,` 3) `;` 4, combine(int.plus(1) `;` >-.count `;` int.plus(10)), 2 `;` 2 `;` 14, "(1;(2,3);4)=(int[plus,1];>-[count];int[plus,10])"),
    testing(2 `;` 4, combine(plus(2) `;` mult(10)), 4 `;` 40, "(2;4)=(+2;*10)"),
    testing(1 `;` 2 `;`(3 `;`(4 `;` 5)), combine(__ `;` __ `;` combine(int `;` combine(plus(20) `;` plus(10)))), 1 `;` 2 `;`(3 `;`(24 `;` 15)), "(1;2;(3;(4;5)))=(_;_;=(int;=(+20;+10)))"),
  ),
  testSet("[combine] |-lst",
    testing(2 `;` 4, combine(plus(2) `|` mult(10)), 4 `|` zeroObj, "(2;4)=(+2|*10)"),
    testing(2.q(1) `|` 4, lst.combine(plus(2) `;` mult(10)), (4 `;` 40), "(2{1}|4) => lst=(+2;*10)"),
    testing(4 | zeroObj, combine(int.plus(2) | int.mult(10)), 6 | zeroObj, "(4|{0})=(int[plus,2]|int[mult,10])"),
  ),
  testSet("[combine] ;-rec",
    testing(str("a") -> int(1) `_;` str("b") -> int(2) `_;` str("c") -> int(3),
      rec.combine(plus("a") -> int.plus(1) `_;` str -> int.plus(2) `_;` zero -> int.plus(3)),
      str("aa") -> int(2) `_;` str("b") -> int(4) `_;` str("") -> int(6), "('a'->1;'b'->2;'c'->3)=(+'a'->+1;str->+2;[zero]->+3)"),
  ))