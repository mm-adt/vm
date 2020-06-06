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

package org.mmadt.language.obj.op.trace

import java.lang.{Double => JDouble}

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait AsOp {
  this: Obj =>
  def as[O <: Obj](obj: O): O = this match {
    case _: Type[_] => obj.via(this, AsOp(obj))
    case _ => AsOp(obj).exec(obj)
  }
}

object AsOp extends Func[Obj, Obj] {
  def apply[O <: Obj](obj: Obj): Inst[O, O] = new VInst[O, O](g = (Tokens.as, List(obj)), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val asObj: Obj = (Inst.resolveToken(start, inst.arg0[Obj]) match {
      case atype: Type[Obj] if atype.trace.headOption.exists(_._2.op.equals(Tokens.from)) => Inst.resolveArg(start, atype).named(atype.trace.head._2.arg0[StrValue].g)
      case atype: Type[Obj] if start.isInstanceOf[Value[_]] => atype match {
        case rectype: Rec[Obj, Obj] => rec(rectype.gsep, rectype.gmap.map(x => Inst.resolveArg(start, x._1) -> Inst.resolveArg(start, x._2)).toMap).named(rectype.name)
        case atype: StrType => vstr(name = atype.name, g = start.asInstanceOf[Value[Obj]].g.toString).compute(atype)
        case atype: IntType => vint(name = atype.name, g = Integer.valueOf(start.asInstanceOf[Value[Obj]].g.toString).longValue()).compute(atype)
        case atype: RealType => vreal(name = atype.name, g = JDouble.valueOf(start.asInstanceOf[Value[Obj]].g.toString).doubleValue()).compute(atype)
      }
      case x => x.named(inst.arg0.name)
    }).via(start, inst)
    assert(asObj.alive)
    asObj
  }
}
