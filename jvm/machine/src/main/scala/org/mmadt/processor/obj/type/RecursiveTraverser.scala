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
import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`.{Type, TypeChecker}
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.processor.Traverser
import org.mmadt.processor.obj.`type`.util.InstUtil

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class RecursiveTraverser[S <: Obj](val obj: S, val state: Map[StrValue, Obj], val model: Model) extends Traverser[S] {

  def this(obj: S) = this(obj, Map[StrValue, Obj](), Model.id)

  def this(obj: S, state: Map[StrValue, Obj]) = this(obj, state, Model.id)

  override def split[E <: Obj](obj: E): Traverser[E] = new RecursiveTraverser(obj, this.state, model) //

  override def apply[E <: Obj](endType: E with Type[_]): Traverser[E] = {
    if (endType.insts().isEmpty) {
      TypeChecker.checkType(this.obj, endType)
      this.asInstanceOf[Traverser[E]]
    } else {
      this.obj match {
        case tobj: Type[_] if model.get(tobj.range(), endType).nonEmpty => this.apply(model.get(tobj.range(), endType).get.asInstanceOf[E with Type[_]])
        case _ =>
          (endType.insts().head._2 match {
            case toInst: Inst if toInst.op().equals(Tokens.to) => new RecursiveTraverser[S](obj, Map[StrValue, Obj](toInst.arg[StrValue]() -> obj) ++ this.state, model) //
            case fromInst: Inst if fromInst.op().equals(Tokens.from) => new RecursiveTraverser[E](this.state(fromInst.arg[StrValue]()).asInstanceOf[E], this.state, model) //
            case defaultInst: Inst => InstUtil.instEval(this, defaultInst)
          }).apply(endType.linvert().asInstanceOf[E with Type[_]])
      }
    }
  }
}
