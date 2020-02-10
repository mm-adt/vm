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

package org.mmadt.processor.obj.value

import org.mmadt.language.Tokens
import org.mmadt.language.model.{Model, SimpleModel}
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.processor.Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class SimpleTraverser[S <: Obj](val obj: S, val state: Map[StrValue, Obj]) extends Traverser[S] {

  def this(obj: S) = this(obj, Map()) //

  override def split[E <: Obj](obj: E): Traverser[E] = new SimpleTraverser[E](obj, this.state) //
  override def apply[E <: Obj](endType: E with Type[_]): Traverser[E] = {
    if (endType.insts().isEmpty)
      this.asInstanceOf[Traverser[E]]
    else {
      (endType.insts().head._2 match {
        // traverser instructions
        case toInst: Inst if toInst.op().equals(Tokens.to) => new SimpleTraverser[S](this.obj, Map[StrValue, Obj](toInst.arg[StrValue]() -> this.obj) ++ this.state)
        case fromInst: Inst if fromInst.op().equals(Tokens.from) => new SimpleTraverser[E](this.state(fromInst.arg[StrValue]()).asInstanceOf[E], this.state) //
        // branch instructions
        // storage instructions
        case storeInst: Inst => this.split(storeInst.inst(storeInst.op(), storeInst.args().map {
          case typeArg: Type[_] => this.split(this.obj match {
            case tt: Type[_] => tt.pure()
            case _ => this.obj
          }).apply(typeArg).obj
          case valueArg: Value[_] => valueArg
        }).apply(this.obj))
      }).asInstanceOf[Traverser[E]]
    }
  }

  override val model: Model = new SimpleModel()
}
