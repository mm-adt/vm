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

import org.mmadt.language.obj.`type`.{BoolType, IntType, RecType, StrType}
import org.mmadt.language.obj.value._
import org.mmadt.language.obj.{ORecType, Obj}
import org.mmadt.storage.obj.`type`.{TBool, TInt, TRec, TStr}
import org.mmadt.storage.obj.value.{VBool, VInt, VRec, VStr}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait StorageFactory {
  /////////TYPES/////////
  def tbool():BoolType
  def tint():IntType
  def tstr():StrType
  def trec[A <: Obj,B <: Obj]():RecType[A,B]
  /////////VALUES/////////
  def vbool(value:Boolean):BoolValue
  def vint(value:Long):IntValue
  def vstr(value:String):StrValue
  def vrec[A <: Value[Obj],B <: Value[Obj]](value:Map[A,B]):RecValue[A,B]
}

object StorageFactory {
  /////////TYPES/////////
  def tbool()(implicit f:StorageFactory):BoolType = f.tbool()
  def tint()(implicit f:StorageFactory):IntType = f.tint()
  def tstr()(implicit f:StorageFactory):StrType = f.tstr()
  def trec()(implicit f:StorageFactory):ORecType = f.trec()
  /////////VALUES/////////
  def vbool(value:Boolean)(implicit f:StorageFactory):BoolValue = f.vbool(value)
  def vint(value:Long)(implicit f:StorageFactory):IntValue = f.vint(value)
  def vstr(value:String)(implicit f:StorageFactory):StrValue = f.vstr(value)
  def vrec[A <: Value[Obj],B <: Value[Obj]](value:Map[A,B])(implicit f:StorageFactory):RecValue[A,B] = f.vrec(value)

  implicit val mmstoreFactory:StorageFactory = new StorageFactory {
    /////////TYPES/////////
    override def tbool():BoolType = new TBool()
    override def tint():IntType = new TInt()
    override def tstr():StrType = new TStr()
    override def trec[A <: Obj,B <: Obj]():RecType[A,B] = new TRec[A,B]()
    /////////VALUES/////////
    override def vbool(value:Boolean):BoolValue = new VBool(value)
    override def vint(value:Long):IntValue = new VInt(value)
    override def vstr(value:String):StrValue = new VStr(value)
    override def vrec[A <: Value[Obj],B <: Value[Obj]](value:Map[A,B]):RecValue[A,B] = new VRec[A,B](value)
  }
}
