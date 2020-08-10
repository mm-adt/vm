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
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.value.{IntValue, RealValue, StrValue, Value}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory.bool
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait GteOp[O <: Obj] {
  this: O =>
  def gte(anon: __): Bool = GteOp(anon).exec(this)
  def gte(other: O): Bool = GteOp(other).exec(this)
  final def >=(other: O): Bool = this.gte(other)
  final def >=(anon: __): Bool = this.gte(anon)
}
object GteOp extends Func[Obj, Bool] {
  def apply(other: Obj): Inst[Obj, Bool] = new VInst[Obj, Bool](g = (Tokens.gte, List(other)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Bool]): Bool = {
    (start match {
      case aint: IntValue => bool(g = aint.g >= inst.arg0[Int].g)
      case areal: RealValue => bool(g = areal.g >= inst.arg0[Real].g)
      case astr: StrValue => bool(g = astr.g >= inst.arg0[Str].g)
      case _: Value[_] => throw LanguageException.unsupportedInstType(start, inst)
      case _ => bool
    }).via(start, inst)
  }
}
