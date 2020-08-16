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

import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.__
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.testSet
import org.mmadt.storage.StorageFactory._
import org.mmadt.processor.inst.TestSetUtil._

class CombineInstTest extends BaseInstTest(
  testSet("[combine] value, type, strm",
    testing(int(1) `;` 2 `;` 3, lst.combine(int.plus(1) `;` int.plus(2) `;` int.plus(3)), int(2) `;` 4 `;` 6),
    testing(int(1) `;` 2 `;` 3, lst.combine(int.plus(1) `;` int.plus(2)), int(2) `;` 4 `;` 4),
    testing(int(1) `;` 2 `;` 3, lst.combine(int.plus(1) `;`), int(2) `;` 3 `;` 4),
    testing(int(1) `;` 2 `;` 3, lst.combine(lst), zeroObj `;`),
    testing(int(1) `;` (int(2) `,` 3) `;` 4, lst.combine(int.plus(1) `;` lst[Int].>-.count() `;` int.plus(10)), int(2) `;` 2 `;` 14),
    testing(int(2) | 4, lst.combine(int.plus(2) `;` int.mult(10)), int(4) `;` 40),
    testing(int(2) `;` 4, lst.combine(int.plus(2) `;` int.mult(10)), int(4) `;` 40),
    testing(int(2) `;` 4, lst.combine(int.plus(2) | int.mult(10)), int(4) | zeroObj),
    testing(int(4) | zeroObj, __.combine(int.plus(2) | int.mult(10)), int(6) | zeroObj))) {
}
