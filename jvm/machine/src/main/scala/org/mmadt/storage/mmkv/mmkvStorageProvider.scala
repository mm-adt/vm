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

import java.util
import java.util.Optional

import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value._
import org.mmadt.language.obj.{Inst, Obj, Rec, Str}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.StorageProvider
import org.mmadt.storage.obj.value.VInst

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmkvStorageProvider extends StorageProvider {
  private val emmkv      = "=mmkv"
  private val K:StrValue = str("k")
  private val V:StrValue = str("v")
  private val mmkv       = rec[Str,Obj].q(*).named("mmkv")
  override def name:String = "mmkv"

  override val model:Model = Model(
    tobj(name) -> trec(K -> obj,V -> obj),
    mmkv.put(K,obj) -> mmkv.error("keys are immutable"),
    mmkv.put(V,obj) -> mmkv.error("values are immutable"))

  override def resolveInstruction(opcode:String,args:util.List[Obj]):Optional[Inst[Obj,Obj]] ={
    if (!opcode.equals(emmkv)) return Optional.empty()
    Optional.of(new mmkvInst(args.get(0).asInstanceOf[StrValue]))
  }

  class mmkvInst(fileStr:StrValue) extends VInst[Obj,Rec[StrValue,Obj]]((emmkv,List(fileStr))) {
    val store:mmkvStore[StrValue,ObjValue] = mmkvStorageProvider.open(fileStr.value)
    override def apply(trav:Traverser[Obj]):Traverser[Rec[StrValue,Obj]] ={
      trav.split((trav.obj() match {
        case atype:Type[_] => atype.compose(store.schema,this).q(*)
        case _:Value[_] => store.strm()
      }).asInstanceOf[Rec[StrValue,Obj]])
    }
  }

}

object mmkvStorageProvider {
  private val dbs:mutable.Map[String,mmkvStore[Obj,Obj]] = new mutable.LinkedHashMap

  def open[K <: Obj,V <: Obj](file:String):mmkvStore[K,V] = dbs.getOrElseUpdate(file,new mmkvStore(file)).asInstanceOf[mmkvStore[K,V]]
  def close():Unit = dbs.values.foreach(m => m.close())
}