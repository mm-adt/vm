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

import org.mmadt.language.obj.`type`.__.plus
import org.mmadt.language.obj.op.trace.ModelOp.{MM, NONE}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{testSet, testing}
import org.mmadt.storage.StorageFactory.str

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class SwapInstTest extends BaseInstTest(
  testSet("[swap] [plus]",List(NONE,MM),
    testing("rodriguez", str.swap(plus("marko")), "markorodriguez", "'rodriguez' => str[swap,[plus,'marko']]"),
    //testing("roro", str.swap(plus("maro")), "marororo", "'roro' => ^:[plus,'maro']:"),
    testing("roro", str.swap(plus("maro")), "marororo", "'roro' => /[plus,'maro']/"),
  ))