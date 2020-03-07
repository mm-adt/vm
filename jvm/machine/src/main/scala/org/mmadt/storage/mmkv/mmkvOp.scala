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

package org.mmadt.storage.mmkv

import org.mmadt.language.mmlang.mmlangParser
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, Obj, Rec}
import org.mmadt.storage.StorageFactory.{qOne, _}
import org.mmadt.storage.obj.value.VInst

import scala.io.Source

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait mmkvOp {
  def mmkv(file:StrValue):Rec[StrValue,Obj]
}


object mmkvOp {

  lazy val KEY  :StrValue = str("k")
  lazy val VALUE:StrValue = str("v")
  lazy val EMMKV:String   = "=mmkv"
  lazy val MMKV :String   = "mmkv"

  def apply(file:StrValue):Inst = new VInst((EMMKV,List(file)),qOne,(a:Obj,b:List[Obj]) => a.mmkv(b.head.asInstanceOf[StrValue]))

  def peekType(file:String):Map[StrValue,Obj] ={
    val source = Source.fromFile(file)
    try {
      source.getLines().take(1).map(line => mmlangParser.parseAll(mmlangParser.recType,line).get).next().value().asInstanceOf[Map[StrValue,Obj]]
    } finally {
      source.close();
    }
  }
}