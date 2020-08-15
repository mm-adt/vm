/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.storage.mmkv

import java.util
import java.util.Optional

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.initial.StartOp
import org.mmadt.language.obj.op.trace.RewriteOp
import org.mmadt.language.obj.value._
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.mmkv.mmkvStorageProvider._
import org.mmadt.storage.obj.value.VInst
import org.mmadt.storage.{StorageException, StorageProvider}

import scala.collection.JavaConverters._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmkvStorageProvider extends StorageProvider {

  override def name: String = "mmkv"
  val getByKeyEq: StrValue = str("getByKeyEq")
  val addKeyValue: StrValue = str("addKeyValue")

  override def resolveInstruction(op: String, args: util.List[Obj]): Optional[Inst[Obj, Obj]] = {
    if (op != opcode) Optional.empty()
    Optional.ofNullable(asScalaIterator(args.iterator()).toList match {
      case List(file: Obj) => mmkvOp.mmkvGetRecords(file)
      case List(file: Obj, this.getByKeyEq, key: Obj) => mmkvOp.mmkvGetRecordsByKey(file, key)
      case _ => null
    })
  }
  override def rewrites(): util.List[Inst[Obj, Obj]] = seqAsJavaList(List(
    RewriteOp((__.error("keys are immutable") `,`) <= lst(",", mmkv.put(K, __.via(__, StartOp(__))))),
    RewriteOp((__.error("values are immutable") `,`) <= lst(",", mmkv.put(V, __.via(__, StartOp(__))))),
    /*RewriteOp(
      (List(mmkvGetRecordsByKey(str, 1)).foldLeft(__.asInstanceOf[Obj])((x, y) => y.exec(x)) `,`)
        <=
        (List(mmkvGetRecords(str), IsOp[Obj](mmkv.q(*).get(K).eqs(1))).foldLeft(__.asInstanceOf[Obj])((x, y) => x.via(x, y)) `,`)))*/))
}

object mmkvStorageProvider {
  private val opcode = "=mmkv"
  private val K: StrValue = str("k")
  private val V: StrValue = str("v")
  private val mmkv: Rec[StrValue, Type[Obj]] = rec(g = (",", List(K -> int, V -> __))).named("mmkv")

  object mmkvOp {
    object mmkvGetRecords extends Func[Obj, Rec[StrValue, Obj]] {
      def apply(fileStr: Obj): Inst[Obj, Rec[StrValue, Obj]] = new VInst[Obj, Rec[StrValue, Obj]](g = (opcode, List(fileStr)), func = this)
      override def apply(start: Obj, inst: Inst[Obj, Rec[StrValue, Obj]]): Rec[StrValue, Obj] = {
        if (inst.arg0[Obj].isInstanceOf[Type[_]])
          return rec[StrValue, Obj].via(start, inst)
        val fileStr: String = inst.arg0[StrValue].g
        (start match {
          case _: Type[_] => mmkvStore.open(fileStr).schema.via(start, inst).hardQ(*)
          case _ => mmkvStore.open(fileStr).stream((start, inst))
        }).asInstanceOf[Rec[StrValue, Obj]]
      }
    }

    object mmkvGetRecordsByKey extends Func[Obj, Rec[StrValue, Obj]] {
      def apply(fileStr: Obj, key: Obj): Inst[Obj, Rec[StrValue, Obj]] = new VInst[Obj, Rec[StrValue, Obj]](g = (opcode, List(fileStr, str("getByKeyEq"), key)), func = this)
      override def apply(start: Obj, inst: Inst[Obj, Rec[StrValue, Obj]]): Rec[StrValue, Obj] = {
        if (inst.arg0[Obj].isInstanceOf[Type[_]])
          return rec[StrValue, Obj].via(start, inst)
        val fileStr: String = inst.arg0[StrValue].g
        val key: Obj = inst.arg2[Obj]
        (start match {
          case _: Type[_] => mmkvStore.open(fileStr).schema.via(start, inst).hardQ(*)
          case _ => rec(K -> key, V -> mmkvStore.open(fileStr).get(key)).via(start, inst)
        })
      }
    }

    private def connect(file: Str): mmkvStore[Value[Obj], Value[Obj]] = {
      file match {
        case avalue: StrValue => mmkvStore.open(avalue.g)
        case _ => throw new StorageException("A str value is required to connect to mmkv: " + file)
      }
    }
  }

}

