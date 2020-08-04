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
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{IntValue, Value}
import org.mmadt.language.obj.{Bool, Inst, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait RepeatOp[A <: Obj] {
  this: A =>

  def repeat(branch: A)(until: Obj): A = RepeatOp(branch, until).exec(this)
  def until(until: Obj)(branch: A): A = RepeatOp(branch, until).exec(this)
}

object RepeatOp extends Func[Obj, Obj] {
  def apply[A <: Obj](branch: A, until: Obj): Inst[A, A] = new VInst[A, A](g = (Tokens.repeat, List(branch, until.asInstanceOf[A])), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val oldInst = Inst.oldInst(inst)
    val until: Obj = Inst.resolveArg(start, oldInst.arg1)
    //
    start match {
      case _: Value[_] if until.isInstanceOf[Bool] =>
        def loop(y: Obj): Obj = {
          strm(y.toStrm.values.filter(_.alive).flatMap(x => {
            val temp: Obj = x ==> oldInst.arg0[Obj]
            val doloop: Bool = temp ==> oldInst.arg1[Bool]
            if (doloop.toStrm.values.last.g) loop(temp).toStrm.values else temp.toStrm.values // TODO: note strm unrolling
          }))
        }
        loop(start)
      case _: Value[_] =>
        val times = until.asInstanceOf[IntValue].g
        var repeatStart = start;
        var i = 0
        while (repeatStart.alive && i < times) {
          i = i + 1
          repeatStart = repeatStart ==> oldInst.arg0[Obj]
        }
        repeatStart
      case _: Type[_] => start.via(start, oldInst)
    }
  }
}