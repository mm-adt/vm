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

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.DefineOp
import org.mmadt.language.obj.value._
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.mmkv.mmkvStorageProvider._
import org.mmadt.storage.obj.ORec
import org.mmadt.storage.obj.value.VInst
import org.mmadt.storage.{StorageException, StorageFactory, StorageProvider}

import scala.collection.JavaConverters._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmkvStorageProvider extends StorageProvider {

  override def name: String = "mmkv"
  /*override lazy val model: Model = Model.from(
    // tobj(name) -> trec(K -> obj,V -> obj), // TODO: this needs to be dynamically determined by mmkvStore file access
    mmkv.put(K, obj) -> mmkv.error("keys are immutable"),
    mmkv.put(V, obj) -> mmkv.error("values are immutable"))
  //    (mmkv <= mmkv.via(obj.q(0), mmkvOp.strm(str.to("x"))).is(mmkv.get(K, int).eqs(int.to("y")))) -> (mmkv.q(qMark) <= obj.q(0).via(obj.q(0), mmkvOp.isGetKeyEq(str.from("x"), int.from("y")))),
  //    (trec(K -> int, V -> obj).q(*) <= mmkv.via(obj.q(0), mmkvOp.strm(str.to("x"))).add(trec(K -> int, V -> obj).to("y"))) -> mmkv.via(mmkv, mmkvOp.addKeyValue(str.from("x"), rec.from("y"))))
*/
  val getByKeyEq: StrValue = str("getByKeyEq")
  val addKeyValue: StrValue = str("addKeyValue")
  override def resolveInstruction(op: String, args: util.List[Obj]): Optional[Inst[Obj, Obj]] = {
    if (op != opcode) Optional.empty()
    Optional.ofNullable(asScalaIterator(args.iterator()).toList match {
      case List(file: Str) => mmkvOp.strm(file)
      case List(file: Str, this.getByKeyEq, key: Obj) => mmkvOp.isGetKeyEq(file, key)
      //case List(file: Str, this.addKeyValue, key: Obj) => mmkvOp.addKeyValue(file, key.asInstanceOf[Rec[StrValue, Obj]])
      case _ => null
    })
  }
  override def definitions(): util.List[Inst[Obj, Obj]] = seqAsJavaList(List(
    DefineOp((__.error("keys are immutable") `,`) <= (__.put("k", __) `,`)),
    DefineOp((__.error("values are immutable") `,`) <= (__.put("v", __) `,`))
  ))
}

object mmkvStorageProvider {
  private val opcode = "=mmkv"
  private val K: StrValue = str("k")
  private val V: StrValue = str("v")
  private val mmkv: Rec[Obj, Obj] = new ORec[Obj, Obj]().q(*).named("mmkv")

  object mmkvOp {
    //def addKeyValue(file: Str, kv: Rec[StrValue, Obj]): Inst[Obj, Rec[StrValue, Obj]] = new mmkvAddKeyValueInst(file, kv)
    def isGetKeyEq(file: Str, key: Obj): Inst[Obj, Rec[StrValue, Obj]] = mmkvIsGetKeyEqInst(file, key)
    def strm(file: Str): Inst[Obj, Rec[StrValue, Obj]] = mmkvInst(file)

    object mmkvInst extends Func[Obj, Rec[StrValue, Obj]] {
      def apply(fileStr: Str): Inst[Obj, Rec[StrValue, Obj]] = new VInst[Obj, Rec[StrValue, Obj]](g = (opcode, List(fileStr)), func = this)
      override def apply(start: Obj, inst: Inst[Obj, Rec[StrValue, Obj]]): Rec[StrValue, Obj] = {
        val fileStr: String = inst.arg0[StrValue].g
        (start match {
          case _: Type[_] => connect(fileStr).schema.clone(via = (start, inst), q = StorageFactory.*)
          case _ => connect(fileStr).strm()
        }).asInstanceOf[Rec[StrValue, Obj]]
      }
    }

    object mmkvIsGetKeyEqInst extends Func[Obj, Rec[StrValue, Obj]] {
      def apply(fileStr: Str, key: Obj): Inst[Obj, Rec[StrValue, Obj]] = new VInst[Obj, Rec[StrValue, Obj]](g = (opcode, List(fileStr, str("getByKeyEq"), key)), func = this)
      override def apply(start: Obj, inst: Inst[Obj, Rec[StrValue, Obj]]): Rec[StrValue, Obj] = {
        val fileStr: String = inst.arg0[StrValue].g
        val key: Value[Obj] = inst.arg2[Value[Obj]]
        (start match {
          case _: Type[_] => connect(fileStr).schema.clone(via = (start, inst), q = StorageFactory.*)
          case _ => rec(K -> key, V -> connect(fileStr).get(key))
        }).asInstanceOf[Rec[StrValue, Obj]]
      }
    }


    /*class mmkvInst(fileStr: Str, q: IntQ = qOne) extends VInst[Obj, Rec[StrValue, Obj]](g = (opcode, List(fileStr)), q = q) {
      override def q(quantifier: IntQ): this.type = new mmkvInst(fileStr, quantifier).asInstanceOf[this.type]
      override def exec(start: Obj): Rec[StrValue, Obj] = {
        (start match {
          case atype: Type[_] => connect(fileStr).schema.via(atype, this).hardQ(StorageFactory.*)
          case _: Value[_] => connect(fileStr).strm().via(start, this)
        }).asInstanceOf[Rec[StrValue, Obj]]
      }
    }

    class mmkvIsGetKeyEqInst(fileStr: Str, key: Obj, q: IntQ = qOne) extends VInst[Obj, Rec[StrValue, Obj]](g = (opcode, List(fileStr, str("getByKeyEq"), key)), q = q) {
      override def q(quantifier: IntQ): this.type = new mmkvIsGetKeyEqInst(fileStr, key, quantifier).asInstanceOf[this.type]
      override def exec(start: Obj): Rec[StrValue, Obj] = {
        (start match {
          case atype: Type[_] => connect(fileStr).schema.via(atype, this).hardQ(StorageFactory.*)
          case _ => rec(K -> key.asInstanceOf[Value[Obj]], V -> connect(fileStr).get(key.asInstanceOf[Value[Obj]]))
        }).asInstanceOf[Rec[StrValue, Obj]]
      }
    }

    class mmkvAddKeyValueInst(fileStr: Str, key: Rec[StrValue, Obj], q: IntQ = qOne) extends VInst[Obj, Rec[StrValue, Obj]](g = (opcode, List(fileStr, str("addKeyValue"), key)), q = q) {
      override def q(quantifier: IntQ): this.type = new mmkvAddKeyValueInst(fileStr, key, quantifier).asInstanceOf[this.type]
      override def exec(start: Obj): Rec[StrValue, Obj] = {
        (start match {
          case atype: Type[_] => connect(fileStr).schema.via(atype, this).hardQ(StorageFactory.*)
          case _ => rec(K -> Inst.resolveArg[Obj, Obj](start, key).asInstanceOf[Rec[StrValue, ObjValue]].get(str("k")).asInstanceOf[Value[Obj]], V -> connect(fileStr).put(
            Inst.resolveArg[Obj, Obj](start, key).asInstanceOf[Rec[StrValue, ObjValue]].get(str("k")),
            Inst.resolveArg[Obj, Obj](start, key).asInstanceOf[Rec[StrValue, ObjValue]].get(str("v"))))
        }).asInstanceOf[Rec[StrValue, Obj]]
      }
    }*/

    private def connect(file: Str): mmkvStore[Value[Obj], Value[Obj]] = {
      file match {
        case avalue: StrValue => mmkvStore.open(avalue.g)
        case _ => throw new StorageException("A str value is required to connect to mmkv: " + file)
      }
    }
  }

}

