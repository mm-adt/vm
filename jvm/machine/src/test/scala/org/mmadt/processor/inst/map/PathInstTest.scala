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

package org.mmadt.processor.inst.map

import org.mmadt.language.obj.Obj.{intToInt, stringToStr}
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp.{MM, MMX, NONE}
import org.mmadt.language.obj.op.trace.PathOp.VERTICES
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory.{int, str, strm}


class PathInstTest extends BaseInstTest(
  testSet("[path] table test", List(NONE, MM, MMX),
    comment("vertices and edges"),
    testing("a", str.path, "a" `;`, "'a' => str[path]"),
    testing("a".q(2), str.q(2).path, ("a".q(2) `;`).q(2), "'a'{2} => str{2}[path]"),
    testing("a".q(2), str.q(2).plus("b").q(3).path, ("a".q(2) `;` plus("b").inst.q(3) `;` "ab".q(6)).q(6) <= "a".q(2).plus("b").q(3).path, "'a'{2} => str{2}[plus,'b']{3}[path]"),
    testing("a", str.plus("b").plus("c").path, ("a" `;` plus("b").inst `;` "ab" `;` plus("c").inst `;` "abc") <= "a".plus("b").plus("c").path, "'a'[plus,'b'][plus,'c'][path]"),
    comment("vertices only"),
    testing("a", str.plus("b").plus("c").path(VERTICES), "a" `;` "ab" `;` "abc", "'a'[plus,'b'][plus,'c'][path,(_;{0})]"),
    testing("a", str.plus("b").plus(plus("c").plus("d")).plus("e").path(VERTICES), "a" `;` "ab" `;` "ababcd" `;` "ababcde", "'a'=>str[plus,'b'][plus,[plus,'c'][plus,'d']][plus,'e'][path,(_;{0})]"),
    testing("a", plus("b").plus(plus("c").plus("d")).plus("e").path(VERTICES).get(1).path(VERTICES), str("a") `;` "ab" `;` "ababcd" `;` "ababcde", "'a'+'b'[++'c'+'d']+'e'[path,(_;{0})]"),
    testing(0, int.plus(1).plus(2).plus(3).plus(4).path(VERTICES), (0 `;` 1 `;` 3 `;` 6 `;` 10), "0 => int+1+2+3+4[path,(_;{0})]"),
    testing(int(1, 2, 3), plus(1).path(VERTICES), strm(List(1 `;` 2, 2 `;` 3, 3 `;` 4)), "[1,2,3][plus,1][path,(_;{0})]"),
    testing(int(1, 2, 3), int.q(3).plus(1).plus(2).path(VERTICES), strm(1 `;` 2 `;` 4, 2 `;` 3 `;` 5, 3 `;` 4 `;` 6), "[1,2,3]=>int{3}+1+2[path,(_;{0})]"),
  ))

