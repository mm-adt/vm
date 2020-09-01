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

import org.mmadt.language.obj.Int
import org.mmadt.language.obj.`type`.__._
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{testSet, testing}
import org.mmadt.storage.StorageFactory.{int, str}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class LiftInstTest extends BaseInstTest(
  testSet("[lift] basics",
    testing(int, int.lift(int.plus(2)).plus(3), int.lift(int.plus(2)).plus(3), "int => int[lift,int[plus,2]][plus,3]"),
    testing(5, int.lift(int.plus(2)).plus(3), 10, "5 => int[lift,int[plus,2]][plus,3]"),
    testing(5, int.lift(int.plus(2)).plus(3).path.get(2), 7, "5 => int[lift,int[plus,2]][plus,3][path].2"),
    testing(int(5, 6), int.q(2).lift(int.q(2).plus(2)).plus(3), int(10, 11), "[5,6] => int{2}[lift,int{2}[plus,2]][plus,3]"),
    testing(int(5, 6), int.q(2).plus(1).lift(int.q(2).plus(2)).plus(3), int(11, 12), "[5,6] => int{2}[plus,1][lift,int{2}[plus,2]][plus,3]"),
    testing(int(5, 6), int.q(2).lift(int.plus(2)).plus(3).path.get(2), int(7, 8), "[5,6] => int{2}[lift,int{2}[plus,2]][plus,3][path].2"),
    testing(int(5, 6), int.q(2).lift[Int](plus(2)).plus(3).path.get(2), int(7, 8), "[5,6] => int{2}<<[plus,2]>>[plus,3][path].2"),
    testing(5, int.as(str).plus("1").lift(plus("a").plus("b")), "51ab", "5 => int[as,str][plus,'1']<<+'a'+'b'>>"),
  ))