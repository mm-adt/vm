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

package org.mmadt.language.obj

import org.mmadt.language.LanguageFactory
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Inst[S <: Obj, +E <: Obj] extends Lst[Obj] {
  final def op: String = this.g._1
  final def args: List[Obj] = this.glist
  final def arg0[O <: Obj]: O = this.glist.head.asInstanceOf[O]
  final def arg1[O <: Obj]: O = this.glist.tail.head.asInstanceOf[O]
  final def arg2[O <: Obj]: O = this.glist.tail.tail.head.asInstanceOf[O]
  final def arg3[O <: Obj]: O = this.glist.tail.tail.tail.head.asInstanceOf[O]
  def exec(start: S): E;

  // standard Java implementations
  override def toString: String = LanguageFactory.printInst(this)
  override lazy val hashCode: scala.Int = this.g.hashCode()
  override def equals(other: Any): Boolean = other match {
    case inst: Inst[_, _] => inst.op == this.op && inst.args == this.args && this.q == inst.q
    case _ => false
  }
}

object Inst {
  def resolveArg[S <: Obj, E <: Obj](obj: S, arg: E): E =
    arg match {
      case valueArg: OValue[E] => valueArg
      case typeArg: OType[E] => obj match {
        case _: Strm[_] => arg
        case _: Value[_] => if (Type.ctypeCheck(obj, typeArg)) obj.compute(typeArg) else typeArg.q(qZero).asInstanceOf[E]
        case atype: Type[_] => if (Type.ctypeCheck(obj, typeArg)) atype.range.compute(typeArg) else typeArg.q(qZero).asInstanceOf[E]
      }
    }

  trait Func {
    def apply[S <: Obj, E <: Obj](start: S, inst: Inst[S, E]): E
  }
}
