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

package org.mmadt.language.obj.op.map

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{IntValue, Value}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

import scala.util.Try

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait GetOp[A <: Obj, B <: Obj] {
  this: Obj =>
  def get(key: A): B = GetOp(key).exec(this)
  def get[BB <: Obj](key: A, btype: BB): BB = GetOp[A, BB](key).exec(this)
}
object GetOp extends Func[Obj, Obj] {
  def apply[A <: Obj, B <: Obj](key: A, typeHint: B = obj.asInstanceOf[B]): Inst[Obj, B] = new VInst[Obj, B](g = (Tokens.get, List(key)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val key: Obj = inst.arg0[Obj]
    val value: Obj = start match {
      case arec: Rec[Obj, Obj] =>
        val results = arec.gmap.filter(a => key.test(a._1)).values.flatMap(a => a.toStrm.values).filter(a => a.alive)
        if (results.isEmpty) if (arec.isInstanceOf[Type[_]]) __ else zeroObj
        else if (results.size == 1) results.head
        else strm(results.toSeq)
      case alst: Lst[_] => key match {
        case aint: IntValue =>
          Try[Obj] {
            alst.glist(aint.g.toInt)
          }.getOrElse(start match {
            case _: Value[_] =>
              LanguageException.PolyException.testIndex(alst, aint.g.toInt)
              obj
            case _: Type[_] => __
          })
        case _ => obj
      }
      case _: Value[_] => zeroObj
      case _ => start
    }
    value match {
      case astrm: Strm[_] => strm(astrm.values.map(x => x.clone(via = (start, inst))))
      case _ => value.via(start, inst)
    }
  }
}