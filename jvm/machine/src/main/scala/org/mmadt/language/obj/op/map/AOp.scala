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

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait AOp {
  this: Obj =>
  def a(other: Obj): Bool = AOp(other).exec(this)
}

object AOp extends Func[Obj, Bool] {
  override val preArgs: Boolean = false
  def apply(other: Obj): Inst[Obj, Bool] = new VInst[Obj, Bool](g = (Tokens.a, List(other)), func = this) with TraceInstruction

  override def apply(start: Obj, inst: Inst[Obj, Bool]): Bool = {
    start match {
      case _ if !start.alive => bfalse.via(start, inst)
      case _: Value[_] => bool(start.test(inst.arg0[Obj])).via(start, inst)
      case _: Type[_] => bool.via(start, inst)
    }
  }
}

/////////////////////////////
/*
override def apply(start: Obj, inst: Inst[Obj, Bool]): Bool = {
    val oldInst = Inst.oldInst(inst)
    //val oldInst = Inst.oldInst(inst)
    start match {
      case astrm: Strm[_] => strm[Bool](astrm.values.map(x => oldInst.exec(x)))
      case astrm: Strm[_] => strm[Bool](astrm.values.map(x => inst.exec(x)))
      case _: Value[_] =>
        (oldInst.arg0[Obj] match {
          case atype: Type[Obj] if atype.trace.headOption.exists(_._2.op.equals(Tokens.from)) => bool(start.test(inst.arg0[Obj]))
          case x => bool(start.test(x))
        }).via(start, oldInst)
        (inst.arg0[Obj] match {
          //case atype: Type[Obj] if atype.trace.headOption.exists(_._2.op.equals(Tokens.from)) => bool(start.test(inst.arg0[Obj]))
          case x => bool(start.test(Inst.resolveToken(start, x)))
        }).via(start, inst)
      case _ =>
        if (!Inst.resolveArg(start, oldInst.arg0[Obj]).alive)
          bfalse.via(start, oldInst)
        else
          oldInst.arg0[Obj] match {
            case atype: Type[Obj] if atype.trace.headOption.exists(_._2.op.equals(Tokens.from)) => bool.via(start, inst)
            case _ => bool.via(start, oldInst)
          }
        if (!Inst.resolveArg(start, inst.arg0[Obj]).alive)
           bfalse.via(start, inst)
         else
        inst.arg0[Obj] match {
          //case atype: Type[Obj] if atype.trace.headOption.exists(_._2.op.equals(Tokens.from)) => bool.via(start, inst)
          case _ => bool.via(start, inst)
        }
    }
 */
/*
 bindLeftValuesToRightVariables(atype, int.is(int.gt(int.to("x"))))
          .map(y => y._1.compute(y._2))
          .map(x => Obj.fetchOption[Int](x, "x"))
          .filter(x => x.isDefined)
          .map(x => x.get.plus(1))
          .map(x => bool(x.test(oldInst.arg0[Type[_]])).clone(via = (start, oldInst)))
          .find(x => x.g)
          .getOrElse(
 */
/*private def bindLeftValuesToRightVariables(left: Type[Obj], right: Type[Obj]): List[(Obj, Type[Obj])] = {
  left.trace.map(_._2).zip(right.trace.map(_._2))
    .flatMap(x => x._1.args.zip(x._2.args))
    .filter(x => x._2.isInstanceOf[Type[Obj]])
    .flatMap(x => {
      x._1 match {
        case left1: Type[Obj] => bindLeftValuesToRightVariables(left1, x._2.asInstanceOf[Type[Obj]])
        case _ => List(x)
      }
    })
    .map(x => (x._1, x._2.asInstanceOf[Type[Obj]]))
}*/