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
import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.value._
import org.mmadt.language.obj.value.strm.RecStrm
import org.mmadt.language.obj.{Inst, Obj, Rec, Str}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.StorageProvider
import org.mmadt.storage.obj.value.VInst

import scala.io.{BufferedSource, Source}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmkvStorageProvider extends StorageProvider {
  private val emmkv = "=mmkv"
  private val kv    = rec[Str,Obj].named("kv")
  private val mmkv  = kv.q(*).named("mmkv")
  override def name:String = "mmkv"
  override val model:Model = Model(
    tobj(name) -> trec(str("k") -> obj,str("v") -> obj),
    mmkv.put(str("k"),obj) -> mmkv.error("keys are immutable"),
    mmkv.put(str("v"),obj) -> mmkv.error("values are immutable"))

  override def resolveInstruction(opcode:String,args:util.List[Obj]):Optional[Inst[Obj,Obj]] ={
    if (!opcode.equals(emmkv)) return Optional.empty()
    // Optional.of(inst.getOrElse(new mmkvInst(args.get(0).asInstanceOf[StrValue])))
    Optional.of(new mmkvInst(args.get(0).asInstanceOf[StrValue]))
  }

  var counter:Long                                = 0
  var inst   :Option[mmkvInst]                    = None
  var recType:RecType[StrValue,Obj]               = rec
  var records:List[RecValue[StrValue,Value[Obj]]] = Nil

  class mmkvInst(fileStr:StrValue) extends VInst[Obj,Rec[StrValue,Obj]]((emmkv,List(fileStr))) {
    val file:String = fileStr.value
    inst = Some(this)

    recType = {
      val source = Source.fromFile(fileStr.value)
      try trec[StrValue,Obj](name = "mmkv",value = source.getLines().take(1).map(line => mmlangParser.parseAll(mmlangParser.recType,line).get).next().value().asInstanceOf[Map[StrValue,Obj]])
      finally source.close();
    }
    records = {
      val source:BufferedSource = Source.fromFile(fileStr.value)
      try {
        val temp = vrec(source.getLines().drop(1).flatMap(k => mmlangParser.parse(k).asInstanceOf[RecValue[StrValue,Value[Obj]]].toStrm.value)).value.toList
        counter = temp.map(x => x.value(str("k"))).map(x => x.asInstanceOf[IntValue].value).max
        temp
      }
      finally source.close()

    }

    override def apply(trav:Traverser[Obj]):Traverser[Rec[StrValue,Obj]] ={
      trav.split((trav.obj() match {
        case atype:Type[_] => atype.compose(trec[StrValue,Obj](name = "mmkv",value = recType.value()),new mmkvInst(file)).q(*)
        case _:Value[_] => new mmkvStrm
      }).asInstanceOf[Rec[StrValue,Obj]])
    }
  }

  class mmkvStrm extends RecStrm[StrValue,Value[Obj]] {
    override def add[O <: Obj](obj:O):O ={
      counter = counter + 1
      println(records.length + "--" + counter.toString)
      records = obj.asInstanceOf[RecValue[StrValue,Value[Obj]]] +: records
      obj
    }
    override val value:Iterator[RecValue[StrValue,Value[Obj]]] = ((list:List[RecValue[StrValue,Value[Obj]]]) => list.iterator).apply(records)
    override def start():RecType[StrValue,Value[Obj]] = recType.asInstanceOf[RecType[StrValue,Value[Obj]]]
    override val q:(IntValue,IntValue) = (records.length,records.length)
    override def q(quantifier:(IntValue,IntValue)):mmkvStrm.this.type = this
    override val name:String = "mmkv"
  }

}
