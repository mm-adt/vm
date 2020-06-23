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

package org.mmadt.language.obj

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.op.map.IdOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.{LanguageException, LanguageFactory}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Inst[S <: Obj, +E <: Obj] extends Poly[S] with Type[Poly[S]] {
  val func: Func[_ <: Obj, _ <: Obj] = null
  final def op: String = this.gsep
  final def args: List[Obj] = this.glist.toList
  final def arg0[O <: Obj]: O = this.glist.head.asInstanceOf[O]
  final def arg1[O <: Obj]: O = this.glist.tail.head.asInstanceOf[O]
  final def arg2[O <: Obj]: O = this.glist.tail.tail.head.asInstanceOf[O]
  final def arg3[O <: Obj]: O = this.glist.tail.tail.tail.head.asInstanceOf[O]
  def exec(start: S): E = {
    this match {
      case _: TraceInstruction => this.func.asInstanceOf[Func[S, E]](start, this)
      case _ => start match {
        case _: Strm[_] => start.via(start, this).asInstanceOf[E]
        case _ => this.func.asInstanceOf[Func[S, E]](start, this.clone(g = (this.op, this.args.map(arg => Inst.resolveArg(start, arg)))).via(this, IdOp())) // TODO: It's not an [id] that processes the inst. hmmm...
      }
    }
  }
  // standard Java implementations
  override def toString: String = LanguageFactory.printInst(this)
  override def equals(other: Any): Boolean = other match {
    case inst: Inst[_, _] => inst.op == this.op && inst.args == this.args && this.q == inst.q
    case _ => false
  }
}
object Inst {
  def oldInst[S <: Obj, E <: Obj](newInst: Inst[S, E]): Inst[S, E] = newInst.via._1.asInstanceOf[Inst[S, E]]
  def resolveToken[A <: Obj](obj: Obj, arg: A): A =
    if (__.isToken(arg))
      Obj.fetchOption[A](obj, arg.name).orElse[A](obj match {
        case _: Type[Obj] => Some(__.from(arg.name).asInstanceOf[A])
        case _ => throw LanguageException.labelNotFound(obj, arg.name)
      }).map(x => arg.trace.foldLeft(x)((a, b) => b._2.exec(a).asInstanceOf[A])).get else arg
  def resolveArg[S <: Obj, E <: Obj](obj: S, arg: E): E = {
    resolveToken(obj, arg) match {
      //case lstArg: LstType[_]  => (if (obj.test(lstArg)) lstArg else lstArg.q(qZero)).asInstanceOf[E] // TODO: compute lstArg?
      case valueArg: OValue[E] => valueArg
      case typeArg: OType[E] => obj match {
        case _: Strm[_] => arg
        case _: Value[_] => if (Type.ctypeCheck(obj, typeArg)) obj.compute(typeArg) else typeArg.q(qZero).asInstanceOf[E]
        case obj: Type[_] => if (Type.ctypeCheck(obj, typeArg)) Obj.copyDefinitions(obj, obj.range).compute(typeArg) else typeArg.q(qZero).asInstanceOf[E]
      }
    }
  }
  trait Func[S <: Obj, E <: Obj] {
    def apply(start: S, inst: Inst[S, E]): E
  }
}
