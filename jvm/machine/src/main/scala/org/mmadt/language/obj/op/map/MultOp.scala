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

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, Int, Lst, Obj, Real}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

import scala.util.Try

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait MultOp[O <: Obj] {
  this: O =>
  def mult(arg: O): this.type = MultOp(arg).exec(this)
  def mult(arg: __): this.type = MultOp(arg).exec(this)
  final def *(arg: O): this.type = this.mult(arg)
  final def *(arg: __): this.type = this.mult(arg)
  final def ⨂(arg: O): this.type = this.mult(arg)
  final def ⨂(arg: __): this.type = this.mult(arg)
}
object MultOp extends Func[Obj, Obj] {
  def apply[O <: Obj](obj: Obj): Inst[O, O] = new VInst[O, O](g = (Tokens.mult, List(obj.asInstanceOf[O])), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    Try[Obj] {
      start match {
        case _: Strm[_] => start
        case _: Value[_] => start match {
          case aint: Int => start.clone(g = aint.g * inst.arg0[Int].g)
          case areal: Real => start.clone(g = areal.g * inst.arg0[Real].g)
          // poly mult
          case multA: Lst[Obj] if multA.isSerial => inst.arg0[Obj] match {
            case multB: Lst[Obj] if multB.isSerial => multA.clone(multA.glist ++ multB.glist)
            case plusB: Lst[Obj] if plusB.isPlus => plusB.clone(plusB.glist.map(a => lst(Tokens.`;`, multA.glist :+ a: _*)))
          }
          case multA: Lst[Obj] if multA.isPlus => inst.arg0[Obj] match {
            case multB: Lst[Obj] if multB.isSerial => multA.clone(multA.glist.map(a => lst(Tokens.`;`, a +: multB.glist: _*)))
            case plusB: Lst[Obj] if plusB.isPlus => multA.clone(multA.glist.flatMap(a => plusB.glist.map(b => lst(plusB.gsep, a, b))))
          }
        }
        case _ => start
      }
    }.toEither match {
      case left: Left[Throwable, Obj] => throw new LanguageException(left.value.getMessage)
      case right: Right[Throwable, Obj] => right.value.via(start, inst)
    }
  }
}

