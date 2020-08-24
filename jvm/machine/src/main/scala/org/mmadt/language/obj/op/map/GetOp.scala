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
import org.mmadt.language.obj.value.{IntValue, LstValue, Value}
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
      case arec: Rec[Obj, Obj] => strm(arec.gmap.filter(a => key.test(a._1)).flatMap(a => a._2 match {
        case astrm: Strm[_] => astrm.values
        case atype: Type[_] => (__ `=>` atype).toStrm.values
        case _ => List(a._2)
      }))
      case alst: Lst[_] if key.isInstanceOf[Int] => key match {
        case aint: IntValue => alst match {
          case _: LstValue[_] => LanguageException.Poly.testIndex(alst, aint.g.toInt); alst.glist(aint.g.toInt)
          case _: LstType[_] if LanguageException.testIndex(alst, aint.g.toInt) => alst.glist(aint.g.toInt) // TODO: multi-get with int types like rec
          case _ => typeHint
        }
        case _ => typeHint
      }
      case _: Value[_] => zeroObj
      case anon: __ if anon.name.equals("x") => anon // TODO: so ghetto -- this is because defs and variables fighting for namespace
      case _ => typeHint
    }
    value match {
      case astrm: Strm[_] =>
        if (astrm.values.isEmpty)
          if (start.isInstanceOf[Type[_]]) typeHint.via(start, newInst)
          else zeroObj.via(start, newInst)
        else if (1 == astrm.values.size) astrm.values.head match {
          case atype: Type[_] => atype.via(start, newInst)
          case avalue: Value[_] => avalue.clone(q = avalue.q.mult(inst.q), via = (start, newInst))
        } else
          astrm(x => x.clone(q = x.q.mult(inst.q), via = (start, newInst)))
      case avalue: Value[_] => avalue.clone(q = avalue.q.mult(inst.q), via = (start, newInst))
      case _ => value.via(start, newInst)
    }
  }
}