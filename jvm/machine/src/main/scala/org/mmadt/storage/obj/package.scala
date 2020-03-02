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

package org.mmadt.storage

import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.value._
import org.mmadt.language.obj.value.strm.{IntStrm, RecStrm, StrStrm}
import org.mmadt.storage.obj.`type`._
import org.mmadt.storage.obj.value.strm.{VIntStrm, VRecStrm, VStrStrm}
import org.mmadt.storage.obj.value.{VBool, VInt, VRec, VStr}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object obj {
  val qZero:(IntValue,IntValue) = (int(0),int(0))
  val qOne :(IntValue,IntValue) = (int(1),int(1))
  val qMark:(IntValue,IntValue) = (int(0),int(1))
  val qPlus:(IntValue,IntValue) = (int(1),int(Long.MaxValue))
  val qStar:(IntValue,IntValue) = (int(0),int(Long.MaxValue))
  val *    :(IntValue,IntValue) = qStar
  val ?    :(IntValue,IntValue) = qMark
  val +    :(IntValue,IntValue) = qPlus

  def obj:ObjType = new TObj()
  def int:IntType = new TInt()
  def int(name:String):IntType = new TInt(name)
  def bool:BoolType = new TBool()
  def bool(name:String):BoolType = new TBool(name)
  def str:StrType = new TStr()
  def tstr(name:String):StrType = new TStr(name)
  val btrue :BoolValue = bool(true)
  val bfalse:BoolValue = bool(false)

  def int(value:Long):IntValue = new VInt(value)
  def int(name:String,value:Long):IntValue = new VInt(name,value)
  def int(value:Long,values:Long*):IntStrm = new VIntStrm((value +: values).map(int))
  def int(values:IntValue*):IntStrm = new VIntStrm(values)
  def bool(value:Boolean):BoolValue = new VBool(value)
  def bool(name:String,value:Boolean):BoolValue = new VBool(name,value)
  def str(value:String):StrValue = new VStr(value)
  def str(name:String,value:String):StrValue = new VStr(name,value)
  def str(values:Seq[StrValue]):StrStrm = new VStrStrm(values)
  def rec[A <: Value[Obj],B <: Value[Obj]](name:String,value:Map[A,B],quantifier:IntQ):RecValue[A,B] = new VRec[A,B](name,value,quantifier)
  def rec[A <: Value[Obj],B <: Value[Obj]](name:String,value:Map[A,B]):RecValue[A,B] = new VRec[A,B](name,value)
  def rec[A <: Value[Obj],B <: Value[Obj]](value:Map[A,B]):RecValue[A,B] = new VRec[A,B](value)
  def rec[A <: Value[Obj],B <: Value[Obj]](name:String,value:RecValue[A,B],values:RecValue[A,B]*):RecStrm[A,B] = new VRecStrm[A,B](name,value +: values)
  def rec(values:Seq[ORecValue]):RecStrm[Value[Obj],Value[Obj]] = new VRecStrm[Value[Obj],Value[Obj]](values)
  def rec[A <: Value[Obj],B <: Value[Obj]](value:(A,B),values:(A,B)*):RecValue[A,B] = new VRec[A,B]((value +: values).toMap)
  def rec[A <: Value[Obj],B <: Value[Obj]](name:String)(values:(A,B)*):RecValue[A,B] = new VRec[A,B](name,values.toMap,qOne)

  def rec[A <: Obj,B <: Obj]:RecType[A,B] = new TRec[A,B]
  //def trec[A <: Obj,B <: Obj](name:String):RecType[A,B] = new TRec[A,B](name,Map.empty,Nil,qOne)
  def trec[A <: Obj,B <: Obj](name:String,value:Map[A,B],quantifier:IntQ):RecType[A,B] = new TRec[A,B](name,value,Nil,quantifier)
  def trec[A <: Obj,B <: Obj](value:Map[A,B]):RecType[A,B] = new TRec[A,B](value)
  def trec[A <: Obj,B <: Obj](value:(A,B),values:(A,B)*):RecType[A,B] = new TRec[A,B]((value +: values).toMap)
  def trec[A <: Obj,B <: Obj](name:String)(values:(A,B)*):RecType[A,B] = new TRec[A,B](name,values.toMap,Nil,qOne)


  def asType[O <: Obj](obj:O):Type[O] = (obj match {
    case strm:IntStrm => return int.q(int(0),strm.q()._2).asInstanceOf[Type[O]]
    case strm:StrStrm => return str.q(int(0),strm.q()._2).asInstanceOf[Type[O]]
    case atype:Type[_] => atype
    case _:IntValue => int
    case _:StrValue => str
    case _:BoolValue => bool
    case _:ORecValue => rec
  }).asInstanceOf[Type[O]].q(obj.q())
}
