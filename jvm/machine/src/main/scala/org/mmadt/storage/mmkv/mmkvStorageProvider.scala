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
import org.mmadt.language.obj.{Inst,Obj,Rec,Str}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory.{str,_}
import org.mmadt.storage.StorageProvider
import org.mmadt.storage.mmkv.mmkvStorageProvider._
import org.mmadt.storage.obj.value.VInst

import scala.collection.JavaConverters._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmkvStorageProvider extends StorageProvider {

  override def name:String = "mmkv"
  override lazy val model:Model = Model(
    // tobj(name) -> trec(K -> obj,V -> obj), // TODO: this needs to be dynamically determined by mmkvStore file access
    mmkv.put(K,obj) -> mmkv.error("keys are immutable"),
    mmkv.put(V,obj) -> mmkv.error("values are immutable"),
    (mmkv <= obj.q(0).compose(mmkv,mmkvOp.strm(str.to("x"))).is(mmkv.get(K,int).eqs(int.to("y")))) -> (mmkv.q(qMark) <= obj.q(0).compose(mmkvOp.isGetKeyEq(str.from("x"),int.from("y")))))

  val getByKeyEq:StrValue = str("getByKeyEq")
  override def resolveInstruction(op:String,args:util.List[Obj]):Optional[Inst[Obj,Obj]] ={
    if (op != opcode) Optional.empty()
    Optional.ofNullable(asScalaIterator(args.iterator()).toList match {
      case List(file:Str) => mmkvOp.strm(file)
      case List(file:Str,this.getByKeyEq,key:Obj) => mmkvOp.isGetKeyEq(file,key)
      case _ => null
    })
  }
}

object mmkvStorageProvider {

  private val opcode     = "=mmkv"
  private val K:StrValue = str("k")
  private val V:StrValue = str("v")
  private val mmkv       = rec[Str,Obj].q(*).named("mmkv")

  object mmkvOp {
    def isGetKeyEq(file:Str,key:Obj):Inst[Obj,Rec[StrValue,Obj]] = new mmkvIsGetKeyEqInst(file,key)
    def strm(file:Str):Inst[Obj,Rec[StrValue,Obj]] = new mmkvInst(file)

    class mmkvInst(fileStr:Str) extends VInst[Obj,Rec[StrValue,Obj]]((opcode,List(fileStr))) {
      override def apply(trav:Traverser[Obj]):Traverser[Rec[StrValue,Obj]] ={
        trav.split((trav.obj() match {
          case atype:Type[_] => atype.compose(connect(fileStr).schema,this).q(*)
          case _:Value[_] => connect(fileStr).strm()
        }).asInstanceOf[Rec[StrValue,Obj]])
      }
    }

    class mmkvIsGetKeyEqInst(fileStr:Str,key:Obj) extends VInst[Obj,Rec[StrValue,Obj]]((opcode,List(fileStr,str("getByKeyEq"),key))) {
      override def apply(trav:Traverser[Obj]):Traverser[Rec[StrValue,Obj]] ={
        trav.split((trav.obj() match {
          case atype:Type[_] => atype.compose(connect(fileStr).schema,this).q(*)
          case _ => vrec(K -> key.asInstanceOf[Value[Obj]],V -> connect(fileStr).get(key.asInstanceOf[Value[Obj]]))
        }).asInstanceOf[Rec[StrValue,Obj]])
      }
    }

    private def connect(file:Str):mmkvStore[Value[Obj],Value[Obj]] ={
      file match {
        case _:Type[_] => throw new UnsupportedOperationException
        case avalue:StrValue => mmkvStore.open(avalue.value)
      }
    }

  }

}

