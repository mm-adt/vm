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
import org.mmadt.language.obj.Rec._
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{LstType, RecType, __}
import org.mmadt.language.obj.value.{IntValue, LstValue, RecValue}
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

  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val key: Obj = inst.arg0[Obj]
    val value: Obj = inst.arg1[Obj]
    val oldKey: Obj = Inst.oldInst(inst).arg0[Obj]
    val oldValue: Obj = Inst.oldInst(inst).arg1[Obj]
    start match {
      case _: __ => start.via(start, inst)
      // REC
      case arec: RecValue[Obj, Obj] => arec.clone(g = (arec.gsep, arec.gmap.replace(key -> value))).via(start, inst)
      case arec: RecType[Obj, Obj] => (oldKey.alive, key.alive, oldValue.alive, value.alive) match {
        //case (true, false, true, true) => arec.clone(g = (arec.gsep, arec.gmap + (oldKey -> value))).via(start, Inst.oldInst(inst))
        //case (true, false, true, false) => arec.clone(g = (arec.gsep, arec.gmap + (oldKey -> oldValue))).via(start, Inst.oldInst(inst))
        case (true, true, true, false) => arec.clone(g = (arec.gsep, arec.gmap.replace(key -> oldValue))).via(start, Inst.oldInst(inst))
        case _ => arec.clone(g = (arec.gsep, arec.gmap.replace(key -> value))).via(start, inst)
      }
      // LST
      case alst: LstValue[Obj] =>
        val (front, back) = alst.glist.splitAt(key.asInstanceOf[IntValue].g.toInt)
        alst.clone(g = (alst.gsep, (front :+ value) ++ back)).via(start, inst)
      case alst: LstType[Obj] => key match {
        case aint: IntValue =>
          val (front, back) = alst.glist.splitAt(aint.g.toInt)
          (oldValue.alive, value.alive) match {
            case (true, false) => alst.clone(g = (alst.gsep, (front :+ oldValue) ++ back)).via(start, Inst.oldInst(inst))
            case _ => alst.clone(g = (alst.gsep, (front :+ value) ++ back)).via(start, inst)
          }
        case _ => alst.via(start, inst)
      }
    }
  }
}
