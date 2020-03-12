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

import org.mmadt.language.mmlang.mmlangParser
import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.{ObjValue, RecValue, StrValue, Value}
import org.mmadt.language.obj.{Inst, Obj, Rec, Str}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.StorageProvider
import org.mmadt.storage.obj.value.VInst

import scala.io.Source

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmkvStorageProvider extends StorageProvider {
  private val emmkv = "=mmkv"
  private val kv    = rec[Str,Obj].named("kv")
  private val mmkv  = kv.q(*).named("mmkv")
  override def name:String = "mmkv"
  override val model:Model = Model(
    mmkv.put(str("k"),obj) -> mmkv.error("keys are immutable"),
    mmkv.put(str("v"),obj) -> mmkv.error("values are immutable"))

  override def resolveInstruction(opcode:String,args:util.List[Obj]):Optional[Inst] ={
    if (!opcode.equals(emmkv)) return Optional.empty()
    Optional.of(new mmkvInst(args.get(0).asInstanceOf[StrValue]))
  }

  class mmkvInst(fileStr:StrValue) extends VInst((emmkv,List(fileStr))) {
    val file:String = fileStr.value()
    def peekType(file:String):Map[StrValue,Obj] ={
      val source = Source.fromFile(file)
      try source.getLines().take(1).map(line => mmlangParser.parseAll(mmlangParser.recType,line).get).next().value().asInstanceOf[Map[StrValue,Obj]]
      finally source.close();
    }
    override def apply(trav:Traverser[Obj]):Traverser[Obj] ={
      trav.split(trav.obj() match {
        case _:Value[Obj] =>
          val source = Source.fromFile(file)
          try vrec(source.getLines().drop(1).flatMap(k => mmlangParser.parse(k).asInstanceOf[RecValue[StrValue,ObjValue]].toStrm.value())).asInstanceOf[Rec[StrValue,Obj]]
          finally source.close()
        case atype:Type[Obj] => atype.compose(trec(name = "mmkv",value = peekType(file)),new mmkvInst(file)).q(*)
      })
    }
  }

}
