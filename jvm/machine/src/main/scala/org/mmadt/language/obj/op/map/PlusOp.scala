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
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{RecValue, Value}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait PlusOp[O <: Obj] {
  this: O =>
  def plus(anon: __): this.type = PlusOp(anon).exec(this)
  def plus(arg: O): this.type = PlusOp(arg).exec(this)
  final def +(anon: __): this.type = this.plus(anon)
  final def +(arg: O): this.type = this.plus(arg)
}

object PlusOp {
  def apply[O <: Obj](obj: Obj): Inst[O, O] = new VInst[O, O](g = (Tokens.plus, List(obj)), func = PlusFunc)

  object PlusFunc extends Func {
    override def apply[S <: Obj, E <: Obj](start: S, inst: Inst[S, E]): E = {
      val rinst = inst.clone(g = (inst.gsep, List(Inst.resolveArg(start, inst.arg0[Obj])))).via(inst, IdOp())
      (start match {
        case _: Strm[_] => start
        case _: Value[_] => start match {
          case aint: Int => start.clone(g = aint.g + rinst.arg0[Int].g)
          case areal: Real => start.clone(g = areal.g + rinst.arg0[Real].g)
          case astr: Str => start.clone(g = astr.g + rinst.arg0[Str].g)
          case arec: RecValue[Value[Value[Obj]], Obj] => start.clone(g = (arec.g._1, arec.gmap ++ rinst.arg0[RecValue[Value[Obj], Value[Obj]]].gmap))
          case arec: ORecType => start.clone(g = arec.gmap ++ rinst.arg0[ORecType]().gmap)
          //////// EXPERIMENTAL
          case serialA: Poly[E] if serialA.isSerial => rinst.arg0[Poly[E]] match {
            case serialB: Poly[E] if serialB.isSerial => serialA | serialB
            case choiceB: Poly[E] if choiceB.isChoice => serialA | choiceB
          }
          case choiceA: Poly[E] if choiceA.isChoice => rinst.arg0[Poly[E]] match {
            case serialB: Poly[E] if serialB.isSerial => if (serialB.isEmpty) choiceA else choiceA | serialB
            case choice: Poly[E] if choice.isChoice => |[E].clone((choiceA.glist ++ choice.glist).toList)
          }
        }
        case _: Type[_] => start
      }).asInstanceOf[E].via(start, rinst)
    }
  }

}
