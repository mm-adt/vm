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

package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.{IntValue, Value}
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait RepeatOp {
  this:Obj =>
  def repeat(branch:Obj)(until:Obj):this.type = RepeatOp(branch, until).exec(this).asInstanceOf[this.type]
  //def until(until:Obj)(branch:A):A = RepeatOp(branch, until).exec(this)
}

object RepeatOp extends Func[Obj, Obj] {
  override val preArgs:Boolean = false
  def apply(branch:Obj, until:Obj):Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.repeat, List(branch, until)), func = this) with BranchInstruction
  override def apply(start:Obj, inst:Inst[Obj, Obj]):Obj = {
    val body:Obj = inst.arg0[Obj]
    val until:Obj = inst.arg1[Obj]
    //
    start match {
      case _:Value[Obj] => until match {
        case times:IntValue => 1L.to(times.g).foldLeft(start.q(q => q.mult(inst.q)).asInstanceOf[Obj])((aobj, _) => aobj `=>` body)
        case _:Obj =>
          def loop(incoming:Obj):Obj = {
            strm(incoming.toStrm.drain.map(x => {
              val outgoing:Obj = x `=>` body
              if (!(outgoing ->> until).alive) outgoing else loop(outgoing)
            }))
          }
          loop(start.q(q => q.mult(inst.q)))
      }
      case atype:Type[_] =>
        val compiledBody = atype ->> body
        compiledBody.via(start, inst.clone(_ => List(compiledBody, atype ->> until)))
    }
  }
}