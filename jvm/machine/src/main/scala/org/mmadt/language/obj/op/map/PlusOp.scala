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

package org.mmadt.language.obj.op.map

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.Rec._
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

import scala.util.{Failure, Success, Try}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait PlusOp[O <: Obj] {
  this:O =>
  def plus(arg:O):this.type = PlusOp(arg).exec(this)
  def plus(arg:__):this.type = PlusOp(arg).exec(this)
  final def +(arg:O):this.type = this.plus(arg)
  final def +(arg:__):this.type = this.plus(arg)
  final def ⨁(arg:O):this.type = this.plus(arg)
  final def ⨁(arg:__):this.type = this.plus(arg)
}

object PlusOp extends Func[Obj, Obj] {
  def apply[O <: Obj](obj:Obj):Inst[O, O] = new VInst[O, O](g = (Tokens.plus, List(obj.asInstanceOf[O])), func = this)
  override def apply(start:Obj, inst:Inst[Obj, Obj]):Obj = Try[Obj](start match {
    case _: Type[_] if !(start.isInstanceOf[Lst[_]] && inst.arg0.isInstanceOf[Lst[_]]) => start
    case abool:Bool => abool.clone(g = abool.g || inst.arg0[Bool].g)
    case aint:Int => aint.clone(g = aint.g + inst.arg0[Int].g)
    case areal:Real => areal.clone(g = areal.g + inst.arg0[Real].g)
    case astr:Str => astr.clone(g = astr.g + inst.arg0[Str].g)
    case arec:Rec[Obj, Obj] => arec.clone(_.replace(inst.arg0[Rec[Obj, Obj]].gmap))
    // poly plus
    case multA:Lst[Obj] if multA.isSerial => inst.arg0[Lst[Obj]] match {
      case multB:Lst[Obj] if multB.isSerial => multA `,` multB
      case plusB:Lst[Obj] if plusB.isPlus => lst(g=(plusB.gsep, List(multA, plusB)))
    }
    case plusA:Lst[Obj] if plusA.isPlus => inst.arg0[Lst[Obj]] match {
      case multB:Lst[Obj] if multB.isSerial => if (multB.isEmpty) plusA else lst(g=(plusA.gsep, List(plusA, multB)))
      case plusB:Lst[Obj] if plusB.isPlus => plusA.clone(g = (plusA.gsep, plusA.glist ++ plusB.glist))
    }
  }) match {
    case x:Failure[_] if x.exception.isInstanceOf[LanguageException] => throw x.exception
    case _:Failure[_] => throw LanguageException.typingError(start, asType(inst.arg0[Obj]))
    case x:Success[Obj] => x.value.via(start, inst)
  }
}
