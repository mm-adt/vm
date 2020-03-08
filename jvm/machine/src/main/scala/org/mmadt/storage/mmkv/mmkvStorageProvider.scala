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
import org.mmadt.language.obj.value.{ObjValue,RecValue,StrValue,Value}
import org.mmadt.language.obj.{Inst,Obj,Rec}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.StorageProvider
import org.mmadt.storage.obj.value.VInst

import scala.io.Source

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmkvStorageProvider extends StorageProvider {
  private val emmkv = "=mmkv"
  override def name():String = "mmkv"
  override def model():Model = Model.id
  override def resolveInstruction(opcode:String,args:util.List[Obj]):Optional[Inst] ={
    if (!opcode.equals(emmkv)) return Optional.empty()
    Optional.of(new mmkvInst(args.get(0).asInstanceOf[StrValue]))
  }

  class mmkvInst(fileStr:StrValue) extends VInst((emmkv,List(fileStr)),qOne,(a:Obj,b:List[Obj]) => {
    val file:String = b.head.asInstanceOf[StrValue].value()
    def peekType(file:String):Map[StrValue,Obj] ={
      val source = Source.fromFile(file)
      try source.getLines().take(1).map(line => mmlangParser.parseAll(mmlangParser.recType,line).get).next().value().asInstanceOf[Map[StrValue,Obj]]
      finally source.close();
    }
    ////
    a match {
      case _:Value[Obj] =>
        val source = Source.fromFile(file)
        try vrec(source.getLines().drop(1).flatMap(k => mmlangParser.parse(k).asInstanceOf[Iterator[RecValue[StrValue,ObjValue]]])).asInstanceOf[Rec[StrValue,Obj]]
        finally source.close()
      case atype:Type[Obj] => atype.compose(trec(name = name(),value = peekType(file)),new mmkvInst(file)).q(*)
    }
  })

}
