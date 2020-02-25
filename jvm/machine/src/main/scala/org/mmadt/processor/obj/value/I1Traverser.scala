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
import org.mmadt.language.obj.`type`.TypeChecker
import org.mmadt.language.obj.op.TraverserInstruction
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst,Obj,TType}
import org.mmadt.processor.Traverser
import org.mmadt.processor.obj.`type`.util.InstUtil

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class I1Traverser[S <: Obj](val obj:S,val state:Map[StrValue,Obj]) extends Traverser[S] {

  def this(obj:S) = this(obj,Map())

  override def split[E <: Obj](obj:E):Traverser[E] = new I1Traverser[E](obj,this.state)
  override def apply[E <: Obj](rangeType:TType[E]):Traverser[E] ={
    if (rangeType.insts().isEmpty) {
      TypeChecker.checkType(this.obj,rangeType)
      this.asInstanceOf[Traverser[E]]
    } else {
      (InstUtil.nextInst(rangeType).get match {
        case tinst:TraverserInstruction => tinst.op() match {
          case Tokens.to => new I1Traverser[S](this.obj,Map[StrValue,Obj](tinst.arg[StrValue]() -> this.obj) ++ this.state)
          case Tokens.from => new I1Traverser[E](this.state(tinst.arg[StrValue]()).asInstanceOf[E],this.state)
        }
        case storageInst:Inst => InstUtil.instEval(this,storageInst)
      }).apply(rangeType.linvert()).asInstanceOf[Traverser[E]]
    }
  }

  override val model:Model = Model.id
}
