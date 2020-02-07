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

package org.mmadt.machine.obj.impl

import org.mmadt.machine.obj.impl.obj.`type`.{TBool, TInt, TRec, TStr}
import org.mmadt.machine.obj.impl.obj.value.{VBool, VInt, VRec, VStr}
import org.mmadt.machine.obj.theory.obj.Obj
import org.mmadt.machine.obj.theory.obj.`type`.{BoolType, IntType, RecType, StrType}
import org.mmadt.machine.obj.theory.obj.value.{BoolValue, IntValue, RecValue, StrValue}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object obj {
  val qZero: (IntValue, IntValue) = (int(0), int(0))
  val qOne: (IntValue, IntValue) = (int(1), int(1))
  val qMark: (IntValue, IntValue) = (int(0), int(1))
  val qPlus: (IntValue, IntValue) = (int(1), int(Long.MaxValue))
  val qStar: (IntValue, IntValue) = (int(0), int(Long.MaxValue))

  val int: IntType = new TInt() //
  val bool: BoolType = new TBool() //
  val str: StrType = new TStr() //
  def rec[A <: Obj, B <: Obj]: RecType[A, B] = new TRec[A, B]() //
  val btrue: BoolValue = bool(true)
  val bfalse: BoolValue = bool(false)

  def int(value: Long): IntValue = new VInt(value) //
  def bool(value: Boolean): BoolValue = new VBool(value) //
  def str(value: String): StrValue = new VStr(value) //
  def rec[A <: Obj, B <: Obj](value: Map[A, B]): RecValue[A, B] = new VRec[A, B](value) //
  def rec[A <: Obj, B <: Obj](value: (A, B)*): RecValue[A, B] = new VRec[A, B](value.reverse.toMap) //
}
