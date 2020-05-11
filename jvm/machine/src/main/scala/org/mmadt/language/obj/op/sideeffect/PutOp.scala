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

package org.mmadt.language.obj.op.sideeffect

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.value.IntValue
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait PutOp[A <: Obj, B <: Obj] {
  this: Obj =>
  def put(key: A, value: B): this.type = PutOp(key, value).exec(this).asInstanceOf[this.type]
}

object PutOp {
  def apply[A <: Obj, B <: Obj](key: A, value: B): Inst[Obj, Obj] = new PutInst[A, B](key, value)

  class PutInst[A <: Obj, B <: Obj](key: A, value: B, q: IntQ = qOne) extends VInst[Obj, Obj]((Tokens.put, List(key, value)), q) {
    override def q(q: IntQ): this.type = new PutInst[A, B](key, value, q).asInstanceOf[this.type]
    override def exec(start: Obj): Obj = {
      start match {
        case apoly: Lst[_] => key match {
          case avalue: IntValue =>
            val (front, back) = apoly.gvalues.splitAt(avalue.ground.toInt)
            apoly.clone(ground = (apoly.gsep, (front :+ value) ++ back), via = (start, this))
          case _ => apoly.via(start, this)
        }
        case rec: Rec[A, B] => rec.clone(ground = (Tokens./, rec.gmap + (key -> value)), via = (rec, this))
      }
    }
  }

}
