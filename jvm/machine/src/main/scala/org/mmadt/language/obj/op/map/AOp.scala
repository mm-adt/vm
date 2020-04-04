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
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait AOp {
  this:Obj =>
  def a(other:Type[Obj]):Bool = this match {
    case atype:Type[_] => atype.compose(bool,AOp(other))
    case _ => bool(this.test(other))
  }
}

object AOp {
  def apply(other:Type[Obj]):Inst[Obj,Bool] = new AInst(other)

  class AInst(other:Type[Obj],q:IntQ = qOne) extends VInst[Obj,Bool]((Tokens.a,List(other)),q) {
    override def q(quantifier:IntQ):this.type = new AInst(other,quantifier).asInstanceOf[this.type]
    override def apply(trav:Traverser[Obj]):Traverser[Bool] = trav.split(trav.obj() match {
      case atype:Type[_] => atype.compose(bool,this)
      case avalue:Value[_] => avalue.a(other).q(multQ(avalue,this))
    })
  }

}