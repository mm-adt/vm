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

import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Obj, TType}
import org.mmadt.processor.Traverser
import org.mmadt.processor.obj.`type`.util.InstUtil

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class C2Traverser[S <: Obj](val obj:S,val state:Map[StrValue,Obj],val model:Model = Model.id) extends Traverser[S] {
  def this(obj:S) = this(obj,Map[StrValue,Obj]()) //

  override def split[E <: Obj](obj:E):Traverser[E] = new C2Traverser[E](obj,state,model) //
  override def apply[E <: Obj](rangeType:TType[E]):Traverser[E] ={
    val next:Traverser[E] = model.get(obj.asInstanceOf[Type[_]].domain()) match {
      case Some(atype) => this.split[E](atype.asInstanceOf[E].q(obj.q()))
      case None => this.asInstanceOf[Traverser[E]]
    }
    (InstUtil.nextInst(rangeType.insts()) match {
      case None => return next
      case Some(inst) => InstUtil.instEval(next,inst)
    }).apply(rangeType.linvert().asInstanceOf[E with Type[_]])
  }
}
