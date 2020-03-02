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

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.value._
import org.mmadt.language.obj.value.strm.{IntStrm, StrStrm}
import org.mmadt.language.obj.{ORecType, Obj}
import org.mmadt.storage.obj.`type`._
import org.mmadt.storage.obj.value._
import org.mmadt.storage.obj.value.strm.{VIntStrm, VStrStrm}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait StorageFactory {
  /////////TYPES/////////
  def obj():ObjType
  def bool():BoolType
  def int():IntType
  def str():StrType
  def trec[A <: Obj,B <: Obj]():RecType[A,B]
  /////////VALUES/////////
  def obj(value:Any):ObjValue
  def bool(value:Boolean):BoolValue
  def int(value:Long):IntValue
  def int(value:IntValue,values:IntValue*):IntStrm
  def str(value:String):StrValue
  def str(value:StrValue,values:StrValue*):StrStrm
  def vrec[A <: Value[Obj],B <: Value[Obj]](value:Map[A,B]):RecValue[A,B]
}

object StorageFactory {
  /////////TYPES/////////
  def obj()(implicit f:StorageFactory):ObjType = f.obj()
  def bool()(implicit f:StorageFactory):BoolType = f.bool()
  def int()(implicit f:StorageFactory):IntType = f.int()
  def str()(implicit f:StorageFactory):StrType = f.str()
  def trec()(implicit f:StorageFactory):ORecType = f.trec()
  /////////VALUES/////////
  def obj(value:Any)(implicit f:StorageFactory):ObjValue = f.obj(value)
  def bool(value:Boolean)(implicit f:StorageFactory):BoolValue = f.bool(value)
  def int(value:Long)(implicit f:StorageFactory):IntValue = f.int(value)
  def int(value:IntValue,values:IntValue*)(implicit f:StorageFactory):IntStrm = f.int(value,values:_*)
  def str(value:String)(implicit f:StorageFactory):StrValue = f.str(value)
  def str(value:StrValue,values:StrValue*)(implicit f:StorageFactory):StrStrm = f.str(value,values:_*)
  def vrec[A <: Value[Obj],B <: Value[Obj]](value:Map[A,B])(implicit f:StorageFactory):RecValue[A,B] = f.vrec(value)

  /////////CONSTANTS//////
  lazy val btrue :BoolValue           = bool(value = true)
  lazy val bfalse:BoolValue           = bool(value = false)
  lazy val qZero :(IntValue,IntValue) = (int(0),int(0))
  lazy val qOne  :(IntValue,IntValue) = (int(1),int(1))
  lazy val qMark :(IntValue,IntValue) = (int(0),int(1))
  lazy val qPlus :(IntValue,IntValue) = (int(1),int(Long.MaxValue))
  lazy val qStar :(IntValue,IntValue) = (int(0),int(Long.MaxValue))
  lazy val *     :(IntValue,IntValue) = qStar
  lazy val ?     :(IntValue,IntValue) = qMark
  lazy val +     :(IntValue,IntValue) = qPlus

  implicit val mmstoreFactory:StorageFactory = new StorageFactory {
    /////////TYPES/////////
    override def obj():ObjType = new TObj()
    override def bool():BoolType = new TBool()
    override def int():IntType = new TInt()
    override def str():StrType = new TStr()
    override def trec[A <: Obj,B <: Obj]():RecType[A,B] = new TRec[A,B]()
    /////////VALUES/////////
    override def obj(value:Any):ObjValue = new VObj(value)
    override def bool(value:Boolean):BoolValue = new VBool(value)
    override def int(value:Long):IntValue = new VInt(value)
    override def int(value:IntValue,values:IntValue*):IntStrm = new VIntStrm(value +: values)
    override def str(value:String):StrValue = new VStr(value)
    override def str(value:StrValue,values:StrValue*):StrStrm = new VStrStrm(value +: values)
    override def vrec[A <: Value[Obj],B <: Value[Obj]](value:Map[A,B]):RecValue[A,B] = new VRec[A,B](value)
  }
}
