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

package org.mmadt.machine.obj.impl.traverser

import org.mmadt.machine.obj.theory.obj.Obj
import org.mmadt.machine.obj.theory.obj.`type`.Type
import org.mmadt.machine.obj.theory.obj.value.StrValue
import org.mmadt.machine.obj.theory.traverser.Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class RecursiveTraverser(state: Map[StrValue, Obj], obj: Obj) extends Traverser {
  def this(obj: Obj) = this(Map[StrValue, Obj](), obj)

  override def obj[S <: Obj](): S = obj.asInstanceOf[S] //
  override def split[E <: Obj](obj: E): Traverser = new RecursiveTraverser(state, obj) //
  override def apply[P <: Type[P]](t: Type[P]): Traverser = if (t.insts().isEmpty) this else t.insts().head._2.apply(this).apply(t.pop())

  override def to(label: StrValue, obj: Obj): Traverser = new RecursiveTraverser(this.state ++ Map[StrValue, Obj](label -> obj), obj)
  override def from(label: StrValue): Traverser = new RecursiveTraverser(this.state, this.state.get(label).get)

  override def state(): Map[StrValue, Obj] = state
}
