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
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, IntQ, Obj}
import org.mmadt.language.obj._
import org.mmadt.storage.obj.{OObj, _}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
abstract class AbstractTObj(name:String,insts:List[(Type[Obj],Inst)],quantifier:IntQ) extends OObj(name,quantifier) with Type[Obj] {

  def this() = this(Tokens.obj,Nil,qOne)
  def insts():List[(Type[Obj],Inst)] = insts

  override def int(inst:Inst,quantifier:IntQ = quantifier):IntType = new TInt(typeName(inst.op(),(Tokens.int,inst.args())),this.insts() ::: List((this,inst)),quantifier) // TODO: propagating the type name
  override def bool(inst:Inst,quantifier:IntQ = quantifier):BoolType = new TBool(Tokens.bool,this.insts() ::: List((this,inst)),quantifier)
  override def str(inst:Inst,quantifier:IntQ = quantifier):StrType = new TStr(typeName(inst.op(),(Tokens.str,inst.args())),this.insts() ::: List((this,inst)),quantifier) // TODO: propagating the type name
  override def rec[A <: Obj,B <: Obj](atype:RecType[A,B],inst:Inst,quantifier:IntQ = quantifier):RecType[A,B] = new TRec[A,B](atype.name,atype.value(),this.insts() ::: List((this,inst)),multQ(this,atype))
  override def obj(inst:Inst,quantifier:IntQ = quantifier):ObjType = new TObj(Tokens.obj,this.insts() ::: List((this,inst)),quantifier)

  // utility method
  private def typeName(op:String,nextType:(String,List[Obj])):String =
    if (op.equals(Tokens.as))
      nextType._2.head.asInstanceOf[StrValue].value()
    else if (Tokens.named(name)) name else nextType._1

}