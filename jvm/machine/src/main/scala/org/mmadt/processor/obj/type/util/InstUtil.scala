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

package org.mmadt.processor.obj.`type`.util

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type,__}
import org.mmadt.language.obj.value.Value
import org.mmadt.processor.Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object InstUtil {
  def valueArgs[S <: Obj,E <: Obj](traverser:Traverser[S],inst:Inst):List[Obj] ={
    if (inst.op() == Tokens.choose) return inst.args()
    inst.args().map{
      case valueArg:Value[_] => valueArg
      case typeArg:Type[_] => traverser.split(traverser.obj() match {
        case atype:Type[_] => atype.range
        case avalue:Value[_] => avalue
      }).apply(typeArg).obj()
    }
  }

  /**
   * Before an instruction is applied, its arguments are computing by a split of the incoming traverser
   */
  def instEval[S <: Obj,E <: Obj](traverser:Traverser[S],inst:Inst):E =
    inst.apply(traverser).asInstanceOf[E]


  @scala.annotation.tailrec
  def createInstList(list:List[(Type[Obj],Inst)],atype:Type[Obj]):List[(Type[Obj],Inst)] ={
    if (atype.insts.isEmpty) list else createInstList(List((atype.range,atype.insts.last._2)) ::: list,atype.insts.last._1)
  }

  def nextInst(atype:Type[_]):Option[Inst] = atype.insts match {
    case Nil => None
    case x => Some(x.head._2)
  }

  def resolveAnonymous[R <: Obj](obj:Obj,rangeType:Type[R]):Type[R] = rangeType match {
    case x:__ => x(obj)
    case x:R => x
  }
}
