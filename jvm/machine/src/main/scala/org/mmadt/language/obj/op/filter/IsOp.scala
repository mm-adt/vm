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

package org.mmadt.language.obj.op.filter

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{BoolType, Type}
import org.mmadt.language.obj.op.FilterInstruction
import org.mmadt.language.obj.op.filter.IsOp.IsInst
import org.mmadt.language.obj.value.{BoolValue, Value}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IsOp {
  this:Obj =>

  def is(bool:BoolType):OType[this.type] = (this match {
    case atype:Type[_] => atype.compose(this,new IsInst[this.type](bool)).asInstanceOf[Type[Obj]].hardQ(minZero(this.q))
    case avalue:Value[_] => avalue.start().is(bool)
  }).asInstanceOf[OType[this.type]]

  def is(bool:BoolValue):this.type = this match {
    case atype:Type[_] => atype.compose(this.q(minZero(this.q)),IsOp(bool.start()))
    case _ => if (bool.value) this else this.q(0)
  }
  /////////////////////////////////////////////////////////////////
  private def is[O <: Obj](inst:IsInst[O]):O = (this match {
    case atype:Type[_] => atype.compose(this,inst).asInstanceOf[Type[Obj]].hardQ(minZero(multQ(this,inst)))
    case avalue:Value[_] => inst.arg0[Bool]() match {
      case boolValue:BoolValue => if (boolValue.value) this.q(multQ(avalue,inst)._2) else this.q(0)
      case boolType:BoolType => avalue.start().is(boolType)
    }
  }).asInstanceOf[O]
}

object IsOp {
  def apply[O <: Obj with IsOp](other:Obj):Inst[O,O] = new IsInst[O](other)

  class IsInst[O <: Obj with IsOp](other:Obj,q:IntQ = qOne) extends VInst[O,O]((Tokens.is,List(other)),q) with FilterInstruction {
    override def q(quantifier:IntQ):this.type = new IsInst[O](other,quantifier).asInstanceOf[this.type]
    override def exec(start:O):O = start.is(new IsInst[O](Inst.resolveArg(start,other),q))
  }

}

