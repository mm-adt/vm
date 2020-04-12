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
import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.op.map.PathOp.PathInst
import org.mmadt.language.obj.value.{IntValue, RecValue, Value}
import org.mmadt.language.obj.{Inst, IntQ, Obj, Rec}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

import scala.collection.mutable

trait PathOp {
  this: Obj =>
  private lazy val inst: Inst[Obj, Rec[IntValue, Obj]] = new PathInst()

  def path(): Rec[IntValue, Obj] = this match {
    case avalue: Value[_] => {
      var counter: scala.Long = 1
      var path: RecValue[IntValue, Value[Obj]] = vrec[IntValue, Value[Obj]](mutable.LinkedHashMap[IntValue, Value[Obj]]())
      this.lineage.foreach(x => {
        path = path.put(int(counter), x._1.asInstanceOf[Value[Obj]])
        counter = counter + 1
      })
      path.put(int(counter), avalue).via(this, inst).asInstanceOf[Rec[IntValue, Obj]]
    }
    case atype: Type[_] => {
      var counter: scala.Long = 1
      var path: RecType[IntValue, Obj] = trec(value = mutable.LinkedHashMap.empty[IntValue, Obj])
      this.lineage.foreach(x => {
        path = path.put(int(counter), x._1)
        counter = counter + 1
      })
      atype.compose(path.put(int(counter), atype), inst)
    }
  }
}

object PathOp {
  def apply(): PathInst = new PathInst
  class PathInst(q: IntQ = qOne) extends VInst[Obj, Rec[IntValue, Obj]]((Tokens.path, Nil), q) {
    override def q(quantifier: IntQ): this.type = new PathInst(quantifier).asInstanceOf[this.type]
    override def exec(start: Obj): Rec[IntValue, Obj] = start.path().via(start, this)
  }

}