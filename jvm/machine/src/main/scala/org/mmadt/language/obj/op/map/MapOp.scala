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
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, IntQ, Obj}
import org.mmadt.processor.{ProcessorException, Traverser}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait MapOp {
  this:Obj =>
  def map[O <: Obj](other:O):O = this match {
    case atype:Type[_] => atype.compose(asType(other).asInstanceOf[O],MapOp[O](other))
    case _ => other match {
      case _:Value[_] => other
      case atype:Type[O] => this ==> atype
    }
  }
}

object MapOp {
  def apply[O <: Obj](other:O):Inst[Obj,O] = new MapInst[O](other)

  class MapInst[O <: Obj](other:O,q:IntQ = qOne) extends VInst[Obj,O]((Tokens.map,List(other)),q) {
    override def q(quantifier:IntQ):this.type = new MapInst[O](other,quantifier).asInstanceOf[this.type]
    override def apply(trav:Traverser[Obj]):Traverser[O] = (trav.obj(),other) match {
      case (_:Value[_],atype:Type[O]) => trav.apply(atype)
      case (_:Obj,avalue:Value[_] with O) => trav.split[O](avalue)
      case (btype:Type[_],atype:Type[_] with O) =>
        val arg:O = Traverser.resolveArg(trav,atype)
        trav.split(btype.compose(arg,MapOp(arg)))
      case _ => throw new ProcessorException(s"unknown state ${trav.obj()}[map,${other}]")
    }
  }

}