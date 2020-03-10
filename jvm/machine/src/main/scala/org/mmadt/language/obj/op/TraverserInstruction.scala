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

package org.mmadt.language.obj.op

import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.processor.Traverser
import org.mmadt.processor.obj.`type`.util.InstUtil

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait TraverserInstruction extends Inst {

  def doFrom[S <: Obj,E <: Obj](traverser:Traverser[S]):Traverser[E] =
    traverser.split(composeInstruction((if (args().length > 1) traverser.state.getOrElse(arg0[StrValue]().value(),arg1[E]()) else traverser.state(arg0[StrValue]().value())).asInstanceOf[E]))

  def doTo[S <: Obj,E <: Obj](traverser:Traverser[S]):Traverser[E] =
    traverser.split[E](composeInstruction(traverser.obj().asInstanceOf[E]),traverser.state + (this.arg0[StrValue]().value() -> traverser.obj()))

  def doFold[S <: Obj,E <: Obj](traverser:Traverser[S]):Traverser[E] ={
    val t:Traverser[S] = traverser.obj() match {
      case _:Type[Obj] => Traverser.stateSplit[S](this.arg0[StrValue]().value(),this.arg1[Obj]())(traverser)
      case _ => traverser
    }
    t.split(InstUtil.instEval(t,this))
  }

  private def composeInstruction[E <: Obj](obj:E):E ={
    obj match {
      case atype:Type[Obj] => atype.compose(this).asInstanceOf[E]
      case _ => obj
    }
  }
}
