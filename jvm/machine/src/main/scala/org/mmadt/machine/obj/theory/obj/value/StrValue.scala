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

package org.mmadt.machine.obj.theory.obj.value

import org.mmadt.machine.obj.theory.obj.`type`.StrType
import org.mmadt.machine.obj.theory.obj.{Bool, Str}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait StrValue extends Str
  with Value[StrValue] {

  override def value(): String //
  override def start(): StrType //

  override def to(label: StrValue): StrType = this.start().to(label) //

  override def plus(other: Str): Str = {
    try this.value() + other.value()
    catch {
      case _: IllegalAccessException => this.start().plus(other)
    }
  }

  override def gt(other: Str): Bool = {
    try this.value() > other.value()
    catch {
      case _: IllegalAccessException => this.start().gt(other)
    }
  }

  override def is(bool: Bool): Str = {
    try if (bool.value()) this else this.q(0)
    catch {
      case _: IllegalAccessException => this.start().is(bool)
    }
  }
}
