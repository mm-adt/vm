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
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.qOne
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait MapOp {
  def map[O <: Obj](other:O):O = other // TODO NO IMPL -- INST
}

object MapOp {
  def apply[O <: Obj](other:O):Inst = new VInst((Tokens.map,List(other)),qOne,(a:Obj,b:List[Obj]) => a.map(b.head))
  def apply[O <: Obj](other:__):Inst = new VInst((Tokens.map,List(other)),qOne,(a:Obj,b:List[Obj]) => a.map(other(a.asInstanceOf[Type[_]].range()).asInstanceOf[O]))
}