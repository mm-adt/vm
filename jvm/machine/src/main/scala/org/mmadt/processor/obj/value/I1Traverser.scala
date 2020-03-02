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
import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`.{Type, TypeChecker}
import org.mmadt.language.obj.op.TraverserInstruction
import org.mmadt.language.obj.{Obj, State}
import org.mmadt.processor.Traverser
import org.mmadt.processor.obj.`type`.util.InstUtil

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class I1Traverser[S <: Obj](val obj:S,val state:State) extends Traverser[S] {

  def this(obj:S) = this(obj,Map.empty)
  override val model:Model = Model.id

  override def split[E <: Obj](obj:E,state:State = this.state):Traverser[E] = new I1Traverser[E](obj,state)
  override def apply[E <: Obj](rangeType:Type[E]):Traverser[E] ={
    (InstUtil.nextInst(rangeType) match {
      case None =>
        //TypeChecker.checkType(this.obj,rangeType)
        return this.asInstanceOf[Traverser[E]]
      case Some(inst) => inst match {
        case traverserInst:TraverserInstruction => traverserInst.op() match {
          case Tokens.to => traverserInst.doTo(this)
          case Tokens.from => traverserInst.doFrom(this)
        }
        case _ => this.split[E](InstUtil.instEval(this,inst))
      }
    }).apply(rangeType.linvert())
  }
}
