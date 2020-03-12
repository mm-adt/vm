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
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait MapOp {
  this:Obj =>
  def map[O <: Obj](other:O):O = other // TODO NO IMPL -- INST
}

object MapOp {
  def apply[O <: Obj](other:O):Inst = new VInst((Tokens.map,List(other)),qOne,(trav:Traverser[Obj]) => trav.split(other match {
    case avalue:Value[O] => trav.obj().map(avalue)
    case atype:Type[O] => trav.obj().map(atype).asInstanceOf[O]
    case anon:__ => trav.obj().map(anon(trav.obj().asInstanceOf[Type[_]].range).asInstanceOf[O])
  }))
}