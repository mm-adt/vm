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

package org.mmadt.storage.obj

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Obj.{IntQ, ViaTuple, rootVia}
import org.mmadt.language.obj.Rec.RecTuple
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.`type`.TRec
import org.mmadt.storage.obj.value.VRec

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
abstract class ORec[A <: Obj, B <: Obj](val name: String = Tokens.rec, val g: RecTuple[A, B] = (Tokens.`,`, Map.empty[A, B]), val q: IntQ = qOne, val via: ViaTuple = rootVia) extends Rec[A, B] {
  override def clone(name: String = this.name,
                     g: Any = this.g,
                     q: IntQ = this.q,
                     via: ViaTuple = this.via): this.type = ORec.makeRec(name, g.asInstanceOf[RecTuple[A, B]], q, via).asInstanceOf[this.type]
}
object ORec {
  def makeRec[A <: Obj, B <: Obj](name: String = Tokens.rec, g: RecTuple[A, B] = (Tokens.`,`, Map.empty[A, B]), q: IntQ = qOne, via: ViaTuple = rootVia): Rec[A, B] = {
    if (null != g._2 && (g._2.isEmpty || !g._2.filter(x => x._1.alive && x._2.alive).exists(x => x._1.isInstanceOf[Type[_]] || x._2.isInstanceOf[Type[_]])))
      new VRec[A, B](name, g = (g._1, g._2.filter(x => x._1.alive && x._2.alive)), q, via)
    else new TRec[A, B](name, g, q, via)
  }
  def emptyType[A <: Obj, B <: Obj]: RecType[A, B] = new TRec[A, B](g = (Tokens.`,`, null))

}