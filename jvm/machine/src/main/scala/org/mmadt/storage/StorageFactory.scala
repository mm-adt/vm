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

import java.util.ServiceLoader

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{BoolType, _}
import org.mmadt.language.obj.value._
import org.mmadt.language.obj.value.strm._
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.`type`._
import org.mmadt.storage.obj.value._
import org.mmadt.storage.obj.value.strm._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait StorageFactory {
  /////////TYPES/////////
  lazy val obj :ObjType  = tobj()
  lazy val bool:BoolType = tbool()
  lazy val int :IntType  = tint()
  lazy val str :StrType  = tstr()
  def rec[A <: Obj,B <: Obj]:RecType[A,B] = trec(value = Map.empty[A,B])
  //
  def tobj(name:String = Tokens.obj,q:IntQ = qOne,insts:InstList = Nil):ObjType
  def tbool(name:String = Tokens.bool,q:IntQ = qOne,insts:InstList = Nil):BoolType
  def tint(name:String = Tokens.int,q:IntQ = qOne,insts:InstList = Nil):IntType
  def tstr(name:String = Tokens.str,q:IntQ = qOne,insts:InstList = Nil):StrType
  def trec[A <: Obj,B <: Obj](name:String = Tokens.rec,value:Map[A,B],q:IntQ = qOne,insts:InstList = Nil):RecType[A,B]
  def trec[A <: Obj,B <: Obj](value:(A,B),values:(A,B)*):RecType[A,B]
  /////////VALUES/////////
  def obj(value:Any):ObjValue
  def bool(value:Boolean):BoolValue = vbool(Tokens.bool,value,qOne)
  def bool(value1:BoolValue,value2:BoolValue,valuesN:BoolValue*):BoolStrm
  def int(value:Long):IntValue
  def int(value1:IntValue,value2:IntValue,valuesN:IntValue*):IntStrm
  def str(value:String):StrValue = vstr(Tokens.str,value,qOne)
  def str(value1:StrValue,value2:StrValue,valuesN:StrValue*):StrStrm
  def vrec[A <: Value[Obj],B <: Value[Obj]](value:Map[A,B]):RecValue[A,B] = vrec(Tokens.rec,value,qOne)
  def vrec[A <: Value[Obj],B <: Value[Obj]](value:(A,B),values:(A,B)*):RecValue[A,B]
  def vrec[A <: Value[Obj],B <: Value[Obj]](value1:RecValue[A,B],value2:RecValue[A,B],valuesN:RecValue[A,B]*):RecStrm[A,B]
  def vrec[A <: Value[Obj],B <: Value[Obj]](value:Iterator[RecValue[A,B]]):RecStrm[A,B]
  //
  def vbool(name:String,value:Boolean,q:IntQ):BoolValue
  def vint(name:String,value:Long,q:IntQ):IntValue
  def vstr(name:String,value:String,q:IntQ):StrValue
  def vrec[A <: Value[Obj],B <: Value[Obj]](name:String,value:Map[A,B],q:IntQ = qOne):RecValue[A,B]
  //
  def strm[O <: Obj](itty:Iterator[O]):OStrm[O]
  def strm[O <: Obj]:OStrm[O]
}

object StorageFactory {
  ///////PROVIDERS///////
  val providers:ServiceLoader[StorageProvider] = ServiceLoader.load(classOf[StorageProvider])
  /////////TYPES/////////
  lazy val obj :ObjType  = tobj()
  lazy val bool:BoolType = tbool()
  lazy val int :IntType  = tint()
  lazy val str :StrType  = tstr()
  def rec[A <: Obj,B <: Obj]:RecType[A,B] = trec(value = Map.empty[A,B])
  //
  def tobj(name:String = Tokens.obj,q:IntQ = qOne,insts:InstList = Nil)(implicit f:StorageFactory):ObjType = f.tobj(name,q,insts)
  def tbool(name:String = Tokens.bool,q:IntQ = qOne,insts:InstList = Nil)(implicit f:StorageFactory):BoolType = f.tbool(name,q,insts)
  def tint(name:String = Tokens.int,q:IntQ = qOne,insts:InstList = Nil)(implicit f:StorageFactory):IntType = f.tint(name,q,insts)
  def tstr(name:String = Tokens.str,q:IntQ = qOne,insts:InstList = Nil)(implicit f:StorageFactory):StrType = f.tstr(name,q,insts)
  def trec[A <: Obj,B <: Obj](name:String = Tokens.rec,value:Map[A,B],q:IntQ = qOne,insts:InstList = Nil)(implicit f:StorageFactory):RecType[A,B] = f.trec(name,value,q,insts)
  def trec[A <: Obj,B <: Obj](value:(A,B),values:(A,B)*)(implicit f:StorageFactory):RecType[A,B] = f.trec(value,values:_*)
  /////////VALUES/////////
  def obj(value:Any)(implicit f:StorageFactory):ObjValue = f.obj(value)
  def bool(value:Boolean)(implicit f:StorageFactory):BoolValue = f.vbool(Tokens.bool,value,qOne)
  def bool(value1:BoolValue,value2:BoolValue,valuesN:BoolValue*)(implicit f:StorageFactory):BoolStrm = f.bool(value1,value2,valuesN:_*)
  def int(value:Long)(implicit f:StorageFactory):IntValue = f.int(value)
  def int(value1:IntValue,value2:IntValue,valuesN:IntValue*)(implicit f:StorageFactory):IntStrm = f.int(value1,value2,valuesN:_*)
  def str(value:String)(implicit f:StorageFactory):StrValue = f.vstr(Tokens.str,value,qOne)
  def str(value1:StrValue,value2:StrValue,valuesN:StrValue*)(implicit f:StorageFactory):StrStrm = f.str(value1,value2,valuesN:_*)
  def vrec[A <: Value[Obj],B <: Value[Obj]](value:Map[A,B])(implicit f:StorageFactory):RecValue[A,B] = f.vrec(Tokens.rec,value,qOne)
  def vrec[A <: Value[Obj],B <: Value[Obj]](value:(A,B),values:(A,B)*)(implicit f:StorageFactory):RecValue[A,B] = f.vrec(value,values:_*)
  def vrec[A <: Value[Obj],B <: Value[Obj]](value1:RecValue[A,B],value2:RecValue[A,B],valuesN:RecValue[A,B]*)(implicit f:StorageFactory):RecStrm[A,B] = f.vrec(value1,value2,valuesN:_*)
  def vrec[A <: Value[Obj],B <: Value[Obj]](value:Iterator[RecValue[A,B]])(implicit f:StorageFactory):RecStrm[A,B] = f.vrec(value)
  //
  def vbool(name:String = Tokens.bool,value:Boolean,q:IntQ = qOne)(implicit f:StorageFactory):BoolValue = f.vbool(name,value,q)
  def vint(name:String = Tokens.int,value:Long,q:IntQ = qOne)(implicit f:StorageFactory):IntValue = f.vint(name,value,q)
  def vstr(name:String = Tokens.str,value:String,q:IntQ = qOne)(implicit f:StorageFactory):StrValue = f.vstr(name,value,q)
  def vrec[A <: Value[Obj],B <: Value[Obj]](name:String = Tokens.rec,value:Map[A,B],q:IntQ = qOne)(implicit f:StorageFactory):RecValue[A,B] = f.vrec(name,value,q)
  def strm[O <: Obj](itty:Iterator[O])(implicit f:StorageFactory):OStrm[O] = f.strm[O](itty)
  def strm[O <: Obj](implicit f:StorageFactory):OStrm[O] = f.strm[O]

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

  def asType[O <: Obj](obj:O):OType[O] = (obj match {
    case atype:Type[O] with O => return atype
    case _:IntValue | _:IntStrm => tint(name = obj.name,q = obj.q)
    case _:StrValue | _:StrStrm => tstr(name = obj.name,q = obj.q)
    case _:BoolValue | _:BoolStrm => tbool(name = obj.name,q = obj.q)
    case _:ORecStrm => trec(name = obj.name,value = Map.empty,q = obj.q)
    case recval:ORecValue => trec(name = recval.name,value = recval.value,q = obj.q)
  }).asInstanceOf[OType[O]]

  implicit val mmstoreFactory:StorageFactory = new StorageFactory {
    /////////TYPES/////////
    override def tobj(name:String = Tokens.obj,q:IntQ = qOne,insts:InstList = Nil):ObjType = new TObj(name,q,insts)
    override def tbool(name:String = Tokens.bool,q:IntQ = qOne,insts:InstList = Nil):BoolType = new TBool(name,q,insts)
    override def tint(name:String = Tokens.int,q:IntQ = qOne,insts:InstList = Nil):IntType = new TInt(name,q,insts)
    override def tstr(name:String = Tokens.str,q:IntQ = qOne,insts:InstList = Nil):StrType = new TStr(name,q,insts)
    override def trec[A <: Obj,B <: Obj](name:String = Tokens.rec,value:Map[A,B],q:IntQ = qOne,insts:InstList = Nil):RecType[A,B] = new TRec[A,B](name,value,q,insts)
    override def trec[A <: Obj,B <: Obj](value:(A,B),values:(A,B)*):RecType[A,B] = new TRec[A,B]((value +: values).toMap)
    /////////VALUES/////////
    override def obj(value:Any):ObjValue = new VObj(value)
    override def int(value:Long):IntValue = new VInt(value)
    override def vbool(name:String,value:Boolean,q:(IntValue,IntValue)):BoolValue = new VBool(name,value,q)
    override def bool(value1:BoolValue,value2:BoolValue,valuesN:BoolValue*):BoolStrm = new VBoolStrm(value1 +: (value2 +: valuesN))
    override def vint(name:String,value:Long,q:(IntValue,IntValue)):IntValue = new VInt(name,value,q)
    override def int(value1:IntValue,value2:IntValue,valuesN:IntValue*):IntStrm = new VIntStrm(value1 +: (value2 +: valuesN))
    override def vstr(name:String,value:String,q:(IntValue,IntValue)):StrValue = new VStr(name,value,q)
    override def str(value1:StrValue,value2:StrValue,valuesN:StrValue*):StrStrm = new VStrStrm(value1 +: (value2 +: valuesN))
    override def vrec[A <: Value[Obj],B <: Value[Obj]](name:String,value:Map[A,B],q:(IntValue,IntValue)):RecValue[A,B] = new VRec[A,B](name,value,q)
    override def vrec[A <: Value[Obj],B <: Value[Obj]](value:(A,B),values:(A,B)*):RecValue[A,B] = new VRec[A,B]((value +: values).toMap)
    override def vrec[A <: Value[Obj],B <: Value[Obj]](value1:RecValue[A,B],value2:RecValue[A,B],valuesN:RecValue[A,B]*):RecStrm[A,B] = new VRecStrm(value1 +: (value2 +: valuesN))
    override def vrec[A <: Value[Obj],B <: Value[Obj]](value:Iterator[RecValue[A,B]]):RecStrm[A,B] = new VRecStrm(value.toSeq)
    //
    override def strm[O <: Obj]:OStrm[O] = VEmptyStrm.empty[O]
    override def strm[O <: Obj](itty:Iterator[O]):OStrm[O] ={
      if (itty.hasNext) {
        val first:O = itty.next()
        if (itty.hasNext) {
          val second:O = itty.next()
          (first match {
            case boolValue:BoolValue => bool(value1 = boolValue,value2 = second.asInstanceOf[BoolValue],valuesN = itty.asInstanceOf[Iterator[BoolValue]].toSeq:_*)
            case intValue:IntValue => int(value1 = intValue,value2 = second.asInstanceOf[IntValue],valuesN = itty.asInstanceOf[Iterator[IntValue]].toSeq:_*)
            case strValue:StrValue => str(value1 = strValue,value2 = second.asInstanceOf[StrValue],valuesN = itty.asInstanceOf[Iterator[StrValue]].toList:_*)
            case recValue:ORecValue => vrec[Value[Obj],Value[Obj]](value1 = recValue,value2 = second.asInstanceOf[ORecValue],valuesN = itty.asInstanceOf[Iterator[ORecValue]].toList:_*)
            // case recValue:...
          }).asInstanceOf[OStrm[O]]
        } else VSingletonStrm.single(first)
      } else {
        strm[O]
      }
    }
  }
}
