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

package org.mmadt.storage.obj.`type`

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.value.{IntValue, StrValue}
import org.mmadt.language.obj.{Inst, OType, Obj, TQ, TypeObj}
import org.mmadt.storage.obj.{OObj, _}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
abstract class AbstractTObj[+O <: Obj](name:String,insts:List[(OType,Inst)],quantifier:TQ) extends OObj(name,quantifier) with Type[Obj]  {

  def this() = this(Tokens.obj,Nil,qOne)
  def insts():List[(OType,Inst)] = insts

  override def int(inst:Inst,q:TQ):IntType = new TInt(typeName(inst.op(),(Tokens.int,inst.args())),this.insts() ::: List((this,inst)),q) // TODO: propagating the type name
  override def bool(inst:Inst,q:TQ):BoolType = new TBool(Tokens.bool,this.insts() ::: List((this,inst)),q)
  override def str(inst:Inst,q:TQ):StrType = new TStr(typeName(inst.op(),(Tokens.str,inst.args())),this.insts() ::: List((this,inst)),q) // TODO: propagating the type name
  override def rec[A <: Obj,B <: Obj](atype:RecType[A,B],inst:Inst,q:TQ):RecType[A,B] = new TRec(atype.name,atype.value(),this.insts() ::: List((this,inst)),(atype.q()._1.mult(q._1),atype.q()._2.mult(q._2)))
  override def obj(inst:Inst,q:(IntValue,IntValue)):ObjType = new TObj(Tokens.obj,this.insts() ::: List((this,inst)),q)

  // utility method
  private def typeName(op:String,nextType:(String,List[Obj])):String =
    if (op.equals(Tokens.as))
      nextType._2.head.asInstanceOf[StrValue].value()
    else if (Tokens.named(name)) name else nextType._1

}