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
import org.mmadt.language.obj.`type`.{LstType, Type, __}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{IntValue, LstValue, RecValue, Value}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait GetOp[A <: Obj, +B <: Obj] {
  this: Obj =>
  def get(key: A): B = GetOp(key, __.asInstanceOf[B]).exec(this)
  def get[BB <: Obj](key: A, btype: BB): BB = GetOp[A, BB](key, btype).exec(this)
}
object GetOp extends Func[Obj, Obj] {
  def apply[A <: Obj, B <: Obj](key: A, typeHint: B = __.asInstanceOf[B]): Inst[Obj, B] = new VInst[Obj, B](g = (Tokens.get, List(key, typeHint)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val key: Obj = inst.arg0[Obj].hardQ(qOne) // TODO: explore start() as the lifted form of a type argument
    val newInst: Inst[Obj, Obj] = inst.clone(g = (Tokens.get, List(key, Inst.oldInst(inst).arg1[Obj])))
    val typeHint: Obj = Inst.oldInst(inst).arg1[Obj].hardQ(start.q)
    val value: Obj = start match {
      case arec: RecValue[Obj, Obj] =>
        val values = strm(arec.gmap.filter(a => a._1.test(key)).flatMap(a => a._2 match {
          case astrm: Strm[_] => astrm.values
          case _ => List(a._2)
        }).filter(_.alive).map(x => x.via(start, newInst)))
        if (values.toStrm.values.nonEmpty && values.toStrm.values.exists(x => x.q != qOne)) return values
        else values
      case arec: Rec[Obj, Obj] => strm(arec.gmap.filter(a => key.test(a._1)).map(a => a._2))
      case alst: Lst[_] if key.isInstanceOf[Int] => key match {
        case aint: IntValue => alst match {
          case _: LstValue[_] => LanguageException.PolyException.testIndex(alst, aint.g.toInt); alst.glist(aint.g.toInt)
          case _: LstType[_] if LanguageException.testIndex(alst, aint.g.toInt) => alst.glist(aint.g.toInt) // TODO: multi-get with int types like rec
          case _ => typeHint
        }
        case _ => typeHint
      }
      case _: Value[_] => zeroObj
      case anon: __ if anon.name.equals("x") => anon // TODO: so ghetto -- this is because defs and variables fighting for namespace
      case _ => typeHint
    }
    (value match {
      case astrm: Strm[_] =>
        if (astrm.values.isEmpty) if (start.isInstanceOf[Type[_]]) typeHint else zeroObj
        else if (1 == astrm.values.size) astrm.values(0)
        else return astrm
      case _ => value
    }).via(start, newInst)
  }
}