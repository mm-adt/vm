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

package org.mmadt.processor

import org.mmadt.language.Stringer
import org.mmadt.machine.obj.theory.obj.Obj
import org.mmadt.machine.obj.theory.obj.`type`.Type
import org.mmadt.machine.obj.theory.obj.value.StrValue

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Traverser[S <: Obj] {

  def obj(): S // the obj location of the traverser
  def state(): Map[StrValue, Obj] // the local variables of the traverser
  //
  protected def to(label: StrValue): Traverser[S] // store the obj to the state by label
  protected def from[E <: Obj](label: StrValue): Traverser[E] // load an obj from the state by label
  def split[E <: Obj](obj: E): Traverser[E] // clone the traverser with a new obj location
  def apply[E <: Obj](t: E with Type[_]): Traverser[E] // embed the traverser's obj into the provided type

  override def toString: String = Stringer.traverserString(this)

}
