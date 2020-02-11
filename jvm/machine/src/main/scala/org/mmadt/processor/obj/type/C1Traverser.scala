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

package org.mmadt.processor.obj.`type`

import org.mmadt.language.Tokens
import org.mmadt.language.model.{Model, SimpleModel}
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.{RecValue, StrValue}
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.processor.Traverser
import org.mmadt.processor.obj.`type`.util.InstUtil
import org.mmadt.storage.obj.rec

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class C1Traverser[S <: Obj](val obj: S, val state: Map[StrValue, Obj], val model: Model = new SimpleModel) extends Traverser[S] {
  def this(obj: S) = this(obj, Map[StrValue, Obj]()) //

  override def split[E <: Obj](obj: E): Traverser[E] = new C1Traverser[E](obj, state, model) //
  override def apply[E <: Obj](endType: E with Type[_]): Traverser[E] = {
    InstUtil.nextInst(endType.insts()) match {
      case None => this.asInstanceOf[Traverser[E]]
      case Some(inst) => inst match {
        case toInst: Inst if toInst.op().equals(Tokens.to) => new C1Traverser[E](obj.asInstanceOf[E], Map[StrValue, Obj](toInst.arg[StrValue]() -> obj) ++ this.state, model) //
        case fromInst: Inst if fromInst.op().equals(Tokens.from) => this.split(this.state(fromInst.arg[StrValue]()).asInstanceOf[E]) //
        case chooseInst: Inst if chooseInst.op().equals(Tokens.choose) => this.split(this.obj.asInstanceOf[E with Type[_]].pop().push(this.obj.inst(Tokens.choose,
          rec[E with Type[_], E with Type[_]](chooseInst.arg[RecValue[E with Type[_], E with Type[_]]]().value().
            map(a => this.apply(a._1).obj() -> this.apply(a._2).obj())))).asInstanceOf[E])
        case defaultInst: Inst => InstUtil.instEval(this, defaultInst)
      }
    }
  }
}
