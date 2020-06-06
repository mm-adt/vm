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

package org.mmadt.language.obj.`type`

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.branch.MergeOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.{Inst, IntQ, OType, Obj, ViaTuple, _}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class __(val name: String = Tokens.anon, val q: IntQ = qOne, val via: ViaTuple = base) extends Type[__] {
  override def clone(name: String = Tokens.anon, g: Any, q: IntQ = qOne, via: ViaTuple = base): this.type = new __(name, q, via).asInstanceOf[this.type]
  override def via(obj: Obj, inst: Inst[_ <: Obj, _ <: Obj]): this.type = this.clone(via = (obj, inst))
  def apply[T <: Obj](obj: Obj): OType[T] = this.trace.foldLeft[Obj](asType(obj))((a, i) => i._2.exec(a)).asInstanceOf[OType[T]]
  def plus(other: Obj): this.type = this.via(this, PlusOp(other))
  def mult(other: Obj): this.type = this.via(this, MultOp(other))
  def neg(): this.type = this.via(this, NegOp())
  def or(other: Obj): Bool = bool.via(this, OrOp(other))
  def and(other: Obj): Bool = bool.via(this, AndOp(other))
  def one(): this.type = this.via(this, OneOp())
  def zero(): this.type = this.via(this, ZeroOp())
  def gt(other: Obj): BoolType = bool.via(this, GtOp(other))
  def gte(other: Obj): BoolType = bool.via(this, GteOp(other))
  def lt(other: Obj): BoolType = bool.via(this, LtOp(other))
  def lte(other: Obj): BoolType = bool.via(this, LteOp(other))
  def head(): this.type = this.via(this, HeadOp())
  def tail(): this.type = this.via(this, TailOp())
  def merge(): this.type = this.via(this, MergeOp())
  ///
  def get(key: Obj): this.type = this.via(this, GetOp(key))
  def get[BB <: Obj](key: Obj, btype: BB): BB = btype.via(this, GetOp(key, btype))
  def put(key: Obj, value: Obj): this.type = this.via(this, PutOp(key, value))
  //
  override def equals(other: Any): Boolean = other match {
    case obj: Obj if !this.alive => !obj.alive
    case anon: __ => ((this.root && anon.root) || (this.via == anon.via))
    case _ => false
  }
}

object __ extends __(Tokens.anon, qOne, base) {
  def apply(insts: List[Inst[_, _]]): __ = insts.foldLeft(new __())((a, b) => a.via(a, b.asInstanceOf[Inst[Obj, Obj]]))
}

