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
import org.mmadt.language.mmlang.mmlangParser
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.value.VInst

import scala.collection.mutable
import scala.io.{BufferedSource, Source}

trait LoadOp {
  this: Obj =>
  def load(file: StrValue): this.type = LoadOp(file).exec(this).asInstanceOf[this.type]
}
object LoadOp extends Func[Obj, Obj] {
  def apply(file: Obj): Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.load, List(file)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    try {
      val file: String = Inst.oldInst(inst).arg0[Obj].toString
      val source: BufferedSource = Source.fromFile(file.dropRight(1).drop(1))
      val obj = mmlangParser.parse[Obj](source.getLines().foldLeft(new mutable.StringBuilder())((x, y) => x.append(y)).toString())
      source.close()
      start `=>` obj
    }
  }
}