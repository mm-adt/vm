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

package org.mmadt.machine.obj.impl.value

import org.mmadt.machine.obj.impl.`type`.TBool
import org.mmadt.machine.obj.theory.obj.Bool
import org.mmadt.machine.obj.theory.obj.`type`.BoolType
import org.mmadt.machine.obj.theory.obj.value.BoolValue
import org.mmadt.machine.obj.{TQ, qOne}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VBool(jvm: Boolean, quantifier: TQ) extends VObj(jvm, quantifier) with BoolValue {

  def this(jvm: Boolean) = this(jvm, qOne)

  override def _jvm(): Boolean = jvm

  override def or(other: BoolValue): BoolValue = new VBool(this.jvm || otherBoolean(other))

  override def or(other: BoolType): BoolType = new TBool() // ??

  override def and(other: BoolValue): BoolValue = new VBool(this.jvm && otherBoolean(other))

  override def and(other: BoolType): BoolType = new TBool() // ??

  //override def zero(): Bool = boolT

  //override def one(): Bool = boolF

  private def otherBoolean(other: Bool): Boolean = other.asInstanceOf[VBool]._jvm()
}

object VBool {

  object boolT extends VBool(true)

  object boolF extends VBool(false)

}