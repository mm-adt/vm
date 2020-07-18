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

package org.mmadt.language.obj.op.sideeffect

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.value.IntValue
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait PutOp[A <: Obj, B <: Obj] {
  this: Obj =>
  def put(key: A, value: B): this.type = PutOp(key, value).exec(this).asInstanceOf[this.type]
}
object PutOp extends Func[Obj, Obj] {
  def apply[A <: Obj, B <: Obj](key: A, value: B): Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.put, List(key, value)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = start match {
    case anon: __ => anon.via(start, inst)
    case arec: Rec[Obj, Obj] => arec.clone(g = (arec.gsep, arec.gmap + (inst.arg0[Obj] -> inst.arg1[Obj].hardQ(1))),via=(start, inst)) // TODO: {0} on int (probably cause of lazy Q loading)
    case alst: Lst[_] => inst.arg0[Obj] match {
      case avalue: IntValue =>
        val (front, back) = alst.glist.splitAt(avalue.g.toInt)
        alst.clone(g = (alst.gsep, (front :+ inst.arg1[Obj]) ++ back), via = (start, inst))
      case _ => alst.via(start, inst)
    }

  }
}
