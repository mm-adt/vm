/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.processor

import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.map.ZeroOp
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

class NullaryInstTest extends FunSuite {

  private val types:List[Type[_] with ZeroOp] = List(int,str,real)
  private val values:List[Value[_] with ZeroOp] = List(int(1))//,str("a"),real(1.0))


  test("[zero] lineage w/ values") {
   values.foreach(x => {
     val line = x.zero().q(2).zero().q(10)
     println(line + ":" + line.lineage)
     assertResult((int(10),int(10)))(line.q)
     // assertResult(0)(line.value)
     assertResult(2)(line.lineage.length)
     assertResult((x,ZeroOp()))(line.lineage.head) // TODO: .q(2) ??
     assertResult((int(0).q(2),ZeroOp()))(line.lineage.last) // TODO: .q(10) ??
   })
  }

  test("[zero] lineage w/ int type") {
     types.foreach(x => {
     val line = x.zero().q(2).zero().q(10)
     println(line + ":" + line.lineage)
     assertResult((int(20),int(20)))(line.range.q)
     assertResult(2)(line.lineage.length)
     assertResult((x,ZeroOp().q(2)))(line.lineage.head)
     assertResult((x.zero().q(2),ZeroOp().q(10)))(line.lineage.last)
   })
  }

}
