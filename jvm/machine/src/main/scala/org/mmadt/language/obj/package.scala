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

package org.mmadt.language

import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.value.strm.{RecStrm, Strm}
import org.mmadt.language.obj.value.{IntValue, RecValue, Value}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object obj {
  type IntQ = (IntValue, IntValue)
  type ViaTuple = (Obj, Inst[_ <: Obj, _ <: Obj])
  type LstTuple[A <: Obj] = (String, List[A])
  type RecTuple[A <: Obj, B <: Obj] = (String, collection.Map[A, B])
  val base: ViaTuple = (null, null)
  // less typing
  type OType[+O <: Obj] = Type[O] with O
  type OValue[+O <: Obj] = Value[O] with O
  type ORecType = RecType[Obj, Obj]
  type ORecValue = RecValue[Value[Obj], Value[Obj]]
  type ORecStrm = RecStrm[Value[Obj], Value[Obj]]
  type OStrm[+O <: Obj] = O with Strm[O]
  // quantifier utilities
  private lazy val zero: IntValue = int(0)
  def minZero(quantifier: IntQ): IntQ = (zero, quantifier._2)
  def maxZero(quantifier: IntQ): IntQ = (quantifier._2, quantifier._2)
  def multQ(objA: Obj, objB: Obj): IntQ = objB.q match {
    case _ if equals(qOne) => objA.q
    case quantifier: IntQ => (objA.q._1 * quantifier._1, objA.q._2 * quantifier._2)
  }
  def multQ(qA: IntQ, qB: IntQ): IntQ = qB match {
    case x: IntQ if qOne.equals(x) => qA
    case _ => (qA._1 * qB._1, qA._2 * qB._2)
  }
  def multQ(qA: Obj, qB: IntQ): IntQ = this.multQ(qA.q, qB)
  def plusQ(objA: Obj, objB: Obj): IntQ = objB.q match {
    case _ if equals(qZero) => objA.q
    case quantifier: IntQ => (objA.q._1 + quantifier._1, objA.q._2 + quantifier._2)
  }
  def plusQ(qA: IntQ, qB: IntQ): IntQ = qB match {
    case _ if equals(qZero) => qA
    case _: IntQ => (qA._1 + qB._1, qA._2 + qB._2)
  }
  def withinQ(objA: Obj, objB: Obj): Boolean = {
    objA.q._1.g >= objB.q._1.g &&
      objA.q._2.g <= objB.q._2.g
  }
  def eqQ(objA: Obj, objB: Obj): Boolean = {
    (objA.q, objB.q) match {
      case (null, null) => true
      case (null, y) if y._1.g == 1 && y._2.g == 1 => true
      case (x, null) if x._1.g == 1 && x._2.g == 1 => true
      case (x, y) if x._1.g == y._1.g && x._2.g == y._2.g => true
      case _ => false
    }
  }
}


