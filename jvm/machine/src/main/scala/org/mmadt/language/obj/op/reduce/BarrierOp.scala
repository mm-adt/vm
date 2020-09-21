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

package org.mmadt.language.obj.op.reduce

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.ReduceInstruction
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.StorageFactory.zeroObj
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait BarrierOp {
  this:Obj =>
  def barrier(atype:Type[Obj]):atype.type = BarrierOp(atype).exec(this).asInstanceOf[atype.type]
  def =|(atype:Type[Obj]):atype.type = barrier(atype)
}
object BarrierOp extends Func[Obj, Obj] {
  override val preArgs:Boolean = false
  override val preStrm:Boolean = false

  def apply(atype:Type[Obj]):Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.barrier, List(atype)), func = this) with ReduceInstruction[Obj] {
    val seed:Obj = zeroObj
    val reducer:Obj = __.id
  }

  override def apply(start:Obj, inst:Inst[Obj, Obj]):Obj = {
    start match {
      case astrm:Strm[_] => astrm.named(inst.arg0[Obj].name)
      // case avalue:Value[_] => avalue // should throw exception
      case _:Type[_] => inst.arg0[Obj].via(start, inst)
    }
  }
}
