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

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Obj, Poly}
import org.mmadt.storage.StorageFactory.zeroObj
import org.mmadt.storage.obj.value.VInst

trait HeadOp[+A <: Obj] {
  this: Obj =>
  def head: A = HeadOp().exec(this)
}
object HeadOp extends Func[Obj, Obj] {
  def apply[A <: Obj](): Inst[Obj, A] = new VInst[Obj, A](g = (Tokens.head, Nil), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = start match {
    case apoly: Poly[_] if apoly.ctype => __.q(apoly.q).via(start, inst)
    case apoly: Poly[_] => apoly.glist.find(_.alive).getOrElse(zeroObj) match {
      case atype: Type[_] => atype.via(start, inst)
      case avalue: Value[_] => avalue.clone(q = avalue.q.mult(inst.q).mult(apoly.q), via = (start, inst))
    } //.getOrElse(throw LanguageException.PolyException.noHead)
    case _ => start.via(start, inst)
  }
}