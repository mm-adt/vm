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

package org.mmadt.language.obj.op.map

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait AOp {
  this: Obj =>
  def a(other: Obj): Bool = AOp(other).exec(this)
}
object AOp extends Func[Obj, Bool] {
  def apply(other: Obj): Inst[Obj, Bool] = new VInst[Obj, Bool](g = (Tokens.a, List(other)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Bool]): Bool = {
    val oldInst = inst.via._1.asInstanceOf[Inst[Obj, Bool]]
    start match {
      case astrm: Strm[_] => strm[Bool](astrm.values.map(x => oldInst.exec(x)))
      case _: Value[_] => bool(start.test(oldInst.arg0[Obj])).via(start, inst)
      case _: Type[_] => bool.via(start, oldInst)
    }
  }
}