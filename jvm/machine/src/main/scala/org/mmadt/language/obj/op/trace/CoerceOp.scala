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

package org.mmadt.language.obj.op.trace

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.Obj.ViaTuple
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.op.trace.ModelOp.NONE
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait CoerceOp {
  this:Obj =>
  def ~>[O <: Obj](aobj:O):O = CoerceOp(aobj).exec(this).asInstanceOf[O]
  def ~>(atype:Symbol):Obj = CoerceOp(__(atype.name)).exec(this)
}

object CoerceOp extends Func[Obj, Obj] {
  override val preArgs:Boolean = false
  def apply[O <: Obj](obj:Obj):Inst[O, O] = new VInst[O, O](g = (Tokens.coerce, List(obj.asInstanceOf[O])), func = this) with TraceInstruction
  override def apply(start:Obj, inst:Inst[Obj, Obj]):Obj = inst.arg0[Obj] match {
    case atype:Type[Obj] if start.model == NONE => atype.rangeObj.via(start, inst)
    case atype:Type[Obj] =>
      start match {
        case _:Type[_] => getc(start.coerce(atype.domainObj), atype.trace).foldLeft(start)((a, b) => b.rangeObj.via(a, CoerceOp(b)))
        case _:Value[_] => start.named(atype.domainObj.name).compute(atype, withAs = false).named(atype.rangeObj.name)
      }
    case _:Value[Obj] => start
  }

  private def getc(base:Obj, trace:List[ViaTuple], cs:List[Obj] = List.empty[Obj]):List[Obj] = {
    cs :+ trace.foldLeft(base)((a, b) => {
      if (b._2.op.equals(Tokens.coerce))
        return (cs :+ a) ++ getc(a.rangeObj.coerce(b._2.arg0[Obj].domainObj), b._2.arg0[Obj].trace)
      else b._2.asInstanceOf[Inst[Obj, Obj]].exec(a)
    })
  }
}