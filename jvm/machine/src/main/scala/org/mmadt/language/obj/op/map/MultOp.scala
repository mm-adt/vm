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
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, Int, Lst, Obj, Real}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait MultOp[O <: Obj] {
  this: O =>
  def mult(anon: __): this.type = MultOp(anon).exec(this)
  def mult(arg: O): this.type = MultOp(arg).exec(this)
  final def *(anon: __): this.type = this.mult(anon)
  final def *(arg: O): this.type = this.mult(arg)
}
object MultOp extends Func[Obj, Obj] {
  def apply[O <: Obj](obj: Obj): Inst[O, O] = new VInst[O, O](g = (Tokens.mult, List(obj)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    (start match {
      case _: Strm[_] => start
      case _: Value[_] => start match {
        case aint: Int => start.clone(g = aint.g * inst.arg0[Int].g)
        case areal: Real => start.clone(g = areal.g * inst.arg0[Real].g)
        case serialA: Lst[Obj] if serialA.isSerial => inst.arg0[Obj] match {
          case serialB: Lst[Obj] if serialB.isSerial => serialA.clone(serialA.glist ++ serialB.glist)
          case choiceB: Lst[Obj] if choiceB.isChoice => choiceB.clone(choiceB.glist.map(a => `;`[Obj].clone(serialA.glist :+ a)))
        }
        case choiceA: Lst[Obj] if choiceA.isChoice => inst.arg0[Obj] match {
          case serialB: Lst[Obj] if serialB.isSerial => choiceA.clone(choiceA.glist.map(a => `;`[Obj].clone(a +: serialB.glist)))
          case choiceB: Lst[Obj] if choiceB.isChoice => choiceA.clone(choiceA.glist.flatMap(a => choiceB.glist.map(b => a | b)))
        }
      }
      case _ => start
    }).via(start, inst)
  }
}

