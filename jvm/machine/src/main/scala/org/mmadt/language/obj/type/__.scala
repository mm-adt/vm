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
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.ObjValue
import org.mmadt.language.obj.{Inst, IntQ, OType, Obj, ViaTuple, _}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class __(val name: String = Tokens.empty, val q: IntQ = qOne, val via: ViaTuple = base()) extends Type[__]
  with PlusOp[__, ObjValue]
  with MultOp[__, ObjValue]
  with HeadOp[Obj]
  with TailOp
  with AndOp
  with OrOp
  with GetOp[Obj, Obj]
  with PutOp[Obj, Obj]
  with NegOp
  with GtOp[__, ObjValue]
  with GteOp[__, ObjValue]
  with LtOp[__, ObjValue]
  with LteOp[__, ObjValue]
  with ZeroOp
  with OneOp {
  override def clone(name: String = Tokens.empty, value: Any, quantifier: IntQ = qOne, via: ViaTuple = base()): this.type = new __(name, quantifier, via).asInstanceOf[this.type]
  def apply[T <: Obj](obj: Obj): OType[T] = asType(this.lineage.foldLeft[Obj](asType(obj))((a, i) => i._2.exec(a))).asInstanceOf[OType[T]]
  // type-agnostic monoid supporting all instructions
  //override def domain[D <: Obj](): Type[D] = obj.q(qStar).asInstanceOf[Type[D]]
  override def plus(other: __): this.type = this.via(this, PlusOp(other))
  override def plus(other: ObjValue): this.type = this.via(this, PlusOp(other))
  override def mult(other: __): this.type = this.via(this, MultOp(other))
  override def mult(other: ObjValue): this.type = this.via(this, MultOp(other))
  def is(other: Obj): BoolType = bool.via(this, IsOp(other))
  override def neg(): this.type = this.via(this, NegOp())
  override def get(key: Obj): this.type = this.via(this, GetOp(key))
  override def get[BB <: Obj](key: Obj, btype: BB): BB = btype.via(this, GetOp(key, btype))
  override def put(key: Obj, value: Obj): this.type = this.via(this, PutOp(key, value))
  override def gt(other: ObjValue): BoolType = bool.via(this, GtOp(other))
  override def lt(other: ObjValue): BoolType = bool.via(this, LtOp(other))
  override def lte(other: ObjValue): BoolType = bool.via(this, LteOp(other))
  override def gte(other: ObjValue): BoolType = bool.via(this, GteOp(other))
  override def zero(): this.type = this.via(this, ZeroOp())
  override def one(): this.type = this.via(this, OneOp())
  override def head(): this.type = this.via(this, HeadOp())
  override def tail(): this.type = this.via(this, TailOp())
}

object __ extends __(Tokens.empty, qOne, base()) {
  def apply(insts: List[Inst[_, _]]): __ = insts.foldLeft(new __())((a, b) => a.via(a, b.asInstanceOf[Inst[Obj, Obj]]))
}

