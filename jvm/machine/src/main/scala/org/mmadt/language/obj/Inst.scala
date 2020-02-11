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

package org.mmadt.language.obj

import org.mmadt.language.Tokens


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Inst extends Obj {
  def value(): JInst

  override def name: String = Tokens.inst

  final def op(): String = this.value()._1 //
  final def args(): List[Obj] = this.value()._2 //
  final def arg[O <: Obj](): O = this.value()._2.head.asInstanceOf[O] //
  def apply(obj: Obj, args: List[Obj]): Obj

  override def equals(other: Any): Boolean = other.isInstanceOf[Inst] &&
    other.asInstanceOf[this.type].op().equals(this.op()) &&
    other.asInstanceOf[this.type].args().equals(this.args())

}
