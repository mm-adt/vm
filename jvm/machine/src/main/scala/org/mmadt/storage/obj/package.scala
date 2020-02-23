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

import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.value.strm.{IntStrm,RecStrm}
import org.mmadt.language.obj.value.{BoolValue,IntValue,RecValue,StrValue}
import org.mmadt.language.obj.{O,ORecValue,Obj,TQ}
import org.mmadt.storage.obj.`type`.{TBool,TInt,TRec,TStr}
import org.mmadt.storage.obj.value.strm.{VIntStrm,VRecStrm}
import org.mmadt.storage.obj.value.{VBool,VInt,VRec,VStr}

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

  def gt(obj:IntValue):BoolType = obj.gt() //
  def gt(obj:StrValue):BoolType = obj.gt() //

  def int:IntType = new TInt() //
  def int(name:String):IntType = new TInt(name,Nil,qOne) //
  def bool:BoolType = new TBool() //
  def str:StrType = new TStr() //
  def rec[A <: Obj,B <: Obj]:RecType[A,B] = new TRec[A,B]() //
  val btrue :BoolValue = bool(true)
  val bfalse:BoolValue = bool(false)

  def int(value:Long):IntValue = new VInt(value) //
  def int(values:Long*):IntStrm = new VIntStrm(values.map(i => int(i))) //
  def bool(value:Boolean):BoolValue = new VBool(value) //
  def str(value:String):StrValue = new VStr(value) //
  def rec[A <: Obj,B <: Obj](name:String,value:Map[A,B],quantifier:TQ):RecValue[A,B] = new VRec[A,B](name,value,quantifier) //
  def rec[A <: Obj,B <: Obj](value:Map[A,B]):RecValue[A,B] = new VRec[A,B](value) //
  def rec(name:String,value:ORecValue,values:ORecValue*):RecStrm[O,O] = new VRecStrm[O,O](name,value +: values:_*) //
  def rec(value:(O,O),values:(O,O)*):ORecValue = new VRec[O,O]((value +: values).toMap) //
  def rec[A <: Obj,B <: Obj](name:String)(values:(A,B)*):RecValue[A,B] = new VRec[A,B](name,values.toMap,qOne) //
  def trec[A <: Obj,B <: Obj](name:String):RecType[A,B] = new TRec[A,B](name,Map.empty,Nil,qOne) //

  def asType(obj:Obj):Type[_] = obj match {
    case atype:Type[_] => atype
    case _:IntValue => int
    case _:StrValue => str
    case _:BoolValue => bool
    case _:ORecValue => rec
  }
}
