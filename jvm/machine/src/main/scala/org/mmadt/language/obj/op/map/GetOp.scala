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
  def π(key:A):B = this.get(key)
}
object GetOp extends Func[Obj, Obj] {
  def apply[A <: Obj, B <: Obj](key: A, typeHint: B = __.asInstanceOf[B]): Inst[Obj, B] = new VInst[Obj, B](g = (Tokens.get, List(key, typeHint)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val key: Obj = inst.arg0[Obj].hardQ(qOne) // TODO: explore start() as the lifted form of a type argument
    val newInst: Inst[Obj, Obj] = Inst.oldInst(inst).clone(args => List(key, args(1)))
    val typeHint: Obj = newInst.arg1[Obj].hardQ(start.q)
    val value: Obj = start match {
      case arec: Rec[_, _] => strm(arec.gmap.filter(kv => kv._1.test(key)).map(kv => kv._2 match {
        case _: Value[_] => kv._2
        case _: Type[_] => __.compute(kv._2.hardQ(kv._2.pureQ))
      }))
      case alst: Lst[_] if key.isInstanceOf[Int] => key match {
        case aint: IntValue => alst match {
          case _: LstValue[_] => LanguageException.Poly.testIndex(alst, aint.g.toInt); alst.glist(aint.g.toInt)
          case _: LstType[_] if LanguageException.testIndex(alst, aint.g.toInt) => alst.glist(aint.g.toInt)
          case _ => typeHint
        }
        case atype: Type[_] => strm(alst.glist.view.zipWithIndex.filter(vi => int(vi._2).test(atype)).map(vi => vi._1 match {
          case _: Value[_] => vi._1
          case _: Type[_] => __.compute(vi._1.hardQ(vi._1.pureQ))
        }))
      }
      case anon: __ if anon.name.equals("x") => anon // TODO: so ghetto -- this is because defs and variables fighting for namespace
      case _ => typeHint
    }
    value match {
      case astrm: Strm[_] =>
        if (astrm.drain.isEmpty) if (start.isInstanceOf[Type[_]]) typeHint.via(start, newInst) else zeroObj
        else if (1 == astrm.drain.size) Poly.finalResult(astrm.drain.head, start, newInst)
        else astrm(x => Poly.finalResult(x, start, newInst))
      case _ => Poly.finalResult(value, start, newInst)
    }
  }
}