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
import org.mmadt.language.obj.op.map.IdOp
import org.mmadt.language.obj.value.strm.{RecStrm, Strm}
import org.mmadt.language.obj.value.{IntValue, RecValue, Value}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object obj {
  type IntQ = (IntValue,IntValue)
  type InstTuple = (String,List[Obj])
  type State = Map[String,Obj]
  type InstList = List[(Type[Obj],Inst[Obj,Obj])]
  type DomainInst[+T <: Obj] = (Type[Obj],Inst[Obj,T])
  def base[T <: Obj](inst:Inst[Obj,T]):DomainInst[T] = (null,inst)
  def base[T <: Obj]():DomainInst[T] = (null,IdOp[Obj]().asInstanceOf[Inst[Obj,T]])

  // less typing
  type OType[+O <: Obj] = O with Type[O]
  type OValue[+O <: Obj] = O with Value[O]
  type ORecType = RecType[Obj,Obj]
  type ORecValue = RecValue[Value[Obj],Value[Obj]]
  type ORecStrm = RecStrm[Value[Obj],Value[Obj]]
  type OStrm[+O <: Obj] = O with Strm[O]

  // quantifier utilities
  private lazy val zero:IntValue = int(0)
  def minZero(quantifier:IntQ):IntQ = (zero,quantifier._2)
  def maxZero(quantifier:IntQ):IntQ = (quantifier._2,quantifier._2)
  def multQ(objA:Obj,objB:Obj):IntQ = objB.q match {
    case _ if equals(qOne) => objA.q
    case quantifier:IntQ => (objA.q._1 * quantifier._1,objA.q._2 * quantifier._2)
  }
  def multQ(qA:IntQ,qB:IntQ):IntQ = qB match {
    case _ if equals(qOne) => qA
    case _:IntQ => (qA._1 * qB._1,qA._2 * qB._2)
  }

  def multQ(qA:Type[Obj],qB:IntQ):IntQ = if (null == qA) qB else this.multQ(qA.q,qB)

  def withinQ(objA:Obj,objB:Obj):Boolean ={
    objA.q._1.value >= objB.q._1.value &&
    objA.q._2.value <= objB.q._2.value
  }
  def eqQ(objA:Obj,objB:Obj):Boolean ={
    val aQ = objA.q
    val bQ = objB.q
    (aQ,bQ) match {
      case (null,null) => true
      case (null,y) if y._1.value == 1 && y._2.value == 1 => true
      case (x,null) if x._1.value == 1 && x._2.value == 1 => true
      case (x,y) if x._1.value == y._1.value && x._2.value == y._2.value => true
      case _ => false
    }
  }
}


