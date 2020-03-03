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
import org.mmadt.language.obj.{InstList, IntQ, Obj}
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.`type`._
import org.mmadt.storage.obj.value._
import org.mmadt.storage.obj.value.strm.{VIntStrm, VStrStrm}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait StorageFactory {
  /////////TYPES/////////
  def obj():ObjType
  def bool:BoolType = tbool()
  def int:IntType = tint()
  def str:StrType = tstr()
  def rec:RecType[Obj,Obj] = trec(value = Map.empty)
  //
  def tbool(name:String = Tokens.bool,q:IntQ = qOne,insts:InstList = Nil):BoolType
  def tint(name:String = Tokens.int,q:IntQ = qOne,insts:InstList = Nil):IntType
  def tstr(name:String = Tokens.str,q:IntQ = qOne,insts:InstList = Nil):StrType
  def trec[A <: Obj,B <: Obj](name:String = Tokens.rec,value:Map[A,B],q:IntQ = qOne,insts:InstList = Nil):RecType[A,B]
  /////////VALUES/////////
  def obj(value:Any):ObjValue
  def bool(value:Boolean):BoolValue = vbool(Tokens.bool,value,qOne)
  def int(value:Long):IntValue
  def int(value1:IntValue,value2:IntValue,valuesN:IntValue*):IntStrm
  def str(value:String):StrValue = vstr(Tokens.str,value,qOne)
  def str(value1:StrValue,value2:StrValue,valuesN:StrValue*):StrStrm
  def vrec[A <: Value[Obj],B <: Value[Obj]](value:Map[A,B]):RecValue[A,B] = vrec(Tokens.rec,value,qOne)
  //
  def vbool(name:String,value:Boolean,q:IntQ):BoolValue
  def vint(name:String,value:Long,q:IntQ):IntValue
  def vstr(name:String,value:String,q:IntQ):StrValue
  def vrec[A <: Value[Obj],B <: Value[Obj]](name:String,value:Map[A,B],q:IntQ = qOne):RecValue[A,B]
}

object StorageFactory {
  /////////TYPES/////////
  def obj()(implicit f:StorageFactory):ObjType = f.obj()
  def bool:BoolType = tbool()
  def int:IntType = tint()
  def str:StrType = tstr()
  def rec:RecType[Obj,Obj] = trec(value = Map.empty)
  //
  def tbool(name:String = Tokens.bool,q:IntQ = qOne,insts:InstList = Nil)(implicit f:StorageFactory):BoolType = f.tbool(name,q,insts)
  def tint(name:String = Tokens.int,q:IntQ = qOne,insts:InstList = Nil)(implicit f:StorageFactory):IntType = f.tint(name,q,insts)
  def tstr(name:String = Tokens.str,q:IntQ = qOne,insts:InstList = Nil)(implicit f:StorageFactory):StrType = f.tstr(name,q,insts)
  def trec[A <: Obj,B <: Obj](name:String = Tokens.rec,value:Map[A,B],q:IntQ = qOne,insts:InstList = Nil)(implicit f:StorageFactory):RecType[A,B] = f.trec(name,value,q,insts)
  /////////VALUES/////////
  def obj(value:Any)(implicit f:StorageFactory):ObjValue = f.obj(value)
  def bool(value:Boolean)(implicit f:StorageFactory):BoolValue = f.vbool(Tokens.bool,value,qOne)
  def int(value:Long)(implicit f:StorageFactory):IntValue = f.int(value)
  def int(value1:IntValue,value2:IntValue,valuesN:IntValue*)(implicit f:StorageFactory):IntStrm = f.int(value1,value2,valuesN:_*)
  def str(value:String)(implicit f:StorageFactory):StrValue = f.vstr(Tokens.str,value,qOne)
  def str(value1:StrValue,value2:StrValue,valuesN:StrValue*)(implicit f:StorageFactory):StrStrm = f.str(value1,value2,valuesN:_*)
  def vrec[A <: Value[Obj],B <: Value[Obj]](value:Map[A,B])(implicit f:StorageFactory):RecValue[A,B] = f.vrec(Tokens.rec,value,qOne)
  //
  def vbool(name:String = Tokens.bool,value:Boolean,q:IntQ)(implicit f:StorageFactory):BoolValue = f.vbool(name,value,q)
  def vint(name:String = Tokens.int,q:IntQ,value:Long)(implicit f:StorageFactory):IntValue = f.vint(name,value,q)
  def vstr(name:String = Tokens.str,q:IntQ,value:String)(implicit f:StorageFactory):StrValue = f.vstr(name,value,q)
  def vrec[A <: Value[Obj],B <: Value[Obj]](name:String = Tokens.rec,q:IntQ,value:Map[A,B])(implicit f:StorageFactory):RecValue[A,B] = f.vrec(name,value,q)

  /////////CONSTANTS//////
  lazy val btrue :BoolValue           = bool(value=true)
  lazy val bfalse:BoolValue           = bool(value=false)
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
    override def tbool(name:String = Tokens.bool,q:IntQ = qOne,insts:InstList = Nil):BoolType = new TBool(name,insts,q)
    override def tint(name:String = Tokens.bool,q:IntQ = qOne,insts:InstList = Nil):IntType = new TInt(name,insts,q)
    override def tstr(name:String = Tokens.bool,q:IntQ = qOne,insts:InstList = Nil):StrType = new TStr(name,insts,q)
    override def trec[A <: Obj,B <: Obj](name:String = Tokens.bool,value:Map[A,B],q:IntQ = qOne,insts:InstList = Nil):RecType[A,B] = new TRec[A,B](name,value,insts,q)
    /////////VALUES/////////
    override def obj(value:Any):ObjValue = new VObj(value)
    override def int(value:Long):IntValue = new VInt(value)
    override def vbool(name:String,value:Boolean,q:(IntValue,IntValue)):BoolValue = new VBool(name,value,q)
    override def vint(name:String,value:Long,q:(IntValue,IntValue)):IntValue = new VInt(name,value,q)
    override def int(value1:IntValue,value2:IntValue,valuesN:IntValue*):IntStrm = new VIntStrm(value1 +: (value2 +: valuesN))
    override def vstr(name:String,value:String,q:(IntValue,IntValue)):StrValue = new VStr(name,value,q)
    override def str(value1:StrValue,value2:StrValue,valuesN:StrValue*):StrStrm = new VStrStrm(value1 +: (value2 +: valuesN))
    override def vrec[A <: Value[Obj],B <: Value[Obj]](name:String,value:Map[A,B],q:(IntValue,IntValue)):RecValue[A,B] = new VRec[A,B](name,value,q)
  }
}
