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

import org.mmadt.language.obj.op.ModelOp
import org.mmadt.language.obj.value.{RecValue, StrValue}
import org.mmadt.language.obj.{Bool, Inst, Obj, Rec, Str, TQ}
import org.mmadt.language.{Stringer, Tokens, obj}
import org.mmadt.processor.obj.`type`.RecursiveTraverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Type[T <: Type[T]] extends Obj
  with ModelOp {

  this: Type[T] =>
  def pure(): this.type = this.insts() match {
    case List() => this
    case _ => this.pop().pure()
  }

  def insts(): List[(Type[_], Inst)] //
  def pop(): this.type //
  def push(inst: Inst): T //
  def push[TT <: Type[TT]](t2: Obj, inst: Inst): TT = (t2 match {
    case _: Bool => bool(inst)
    case _: obj.Int => int(inst)
    case _: Str => str(inst)
    case _: Rec[_, _] => rec[Obj, Obj](Map.empty[Obj, Obj], inst)
  }).asInstanceOf[TT]

  def int(): IntType = int(null) //
  def int(inst: Inst): IntType = int(inst, this.q()) //
  def int(inst: Inst, q: TQ): IntType //

  def bool(): BoolType = bool(null) //
  def bool(inst: Inst): BoolType = bool(inst, this.q()) //
  def bool(inst: Inst, q: TQ): BoolType //

  def str(): StrType = str(null) //
  def str(inst: Inst): StrType = str(inst, this.q()) //
  def str(inst: Inst, q: TQ): StrType //

  def rec(): RecType[_, _] = rec[Obj, Obj](null, null) //
  def rec[K <: Obj, V <: Obj](tvalue: Map[K, V], inst: Inst): RecType[K, V] = rec(tvalue, inst, this.q()) //
  def rec[K <: Obj, V <: Obj](tvalue: Map[K, V], inst: Inst, q: TQ): RecType[K, V] //

  override def toString: String = Stringer.typeString(this) //

  final def <=[TT <: Type[TT]](mapFrom: Type[TT]): TT = mapFrom.q(this.q()).asInstanceOf[TT]

  def ==>[TT <: Type[TT]](t: Type[TT]): TT = new RecursiveTraverser[this.type](this).apply(t).obj().asInstanceOf[TT] // TODO: USE COMPILATION TRAVERSER

  override def map[O <: Obj](other: O): O = this.push(other, inst(Tokens.map, other)) //
  override def from[O <: Obj](label: StrValue): O = this.push(inst(Tokens.from, label)).asInstanceOf[O] //

  override def equals(other: Any): Boolean = other match {
    case t: Type[T] => t.insts().map(_._2) == this.insts().map(_._2) && this.pure().toString == t.pure().toString
    case _ => false
  }

  def |[O <: Type[O]](other: O): O = { // TODO: ghetto union type construction
    if (this.insts().nonEmpty && this.insts().head._2.op().equals(Tokens.choose))
      this.pop().choose(Map(other -> other) ++ this.insts().head._2.arg[RecValue[O, O]]().value())
    else
      this.choose(other -> other, this.asInstanceOf[O] -> this.asInstanceOf[O])
  }

  override def hashCode(): scala.Int = this.pure().toString.hashCode // TODO: using toString()

  override def model(model: StrValue): this.type = this.push(inst(Tokens.model, model)).asInstanceOf[this.type] //
}
