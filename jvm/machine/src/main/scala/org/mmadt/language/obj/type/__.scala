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

package org.mmadt.language.obj.`type`

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Obj.{IntQ, ViaTuple, rootVia}
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.branch.{CombineOp, MergeOp}
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class __(val name: String = Tokens.anon, val q: IntQ = qOne, val via: ViaTuple = rootVia) extends Type[__] {
  override def clone(name: String = this.name, g: Any = null, q: IntQ = this.q, via: ViaTuple = this.via): this.type = new __(name, q, via).asInstanceOf[this.type]
  def combine(other: Obj): this.type = this.via(this, CombineOp(other))
  def plus(other: Obj): this.type = this.via(this, PlusOp(other))
  def mult(other: Obj): this.type = this.via(this, MultOp(other))
  def neg: this.type = this.via(this, NegOp())
  def or(other: Obj): this.type = this.via(this, OrOp(other))
  def and(other: Obj): this.type = this.via(this, AndOp(other))
  def one: this.type = this.via(this, OneOp())
  def zero: this.type = this.via(this, ZeroOp())
  def gt(other: Obj): this.type = this.via(this, GtOp(other))
  def gte(other: Obj): this.type = this.via(this, GteOp(other))
  def lt(other: Obj): this.type = this.via(this, LtOp(other))
  def lte(other: Obj): this.type = this.via(this, LteOp(other))
  def head: this.type = this.via(this, HeadOp())
  def tail: this.type = this.via(this, TailOp())
  def last: this.type = this.via(this, LastOp())
  def merge: this.type = this.via(this, MergeOp())
  def empty: this.type = this.via(this, EmptyOp())
  def `>-`: this.type = this.merge
  override def not(other: Obj): Bool = bool.via(this, NotOp(other))
  ///
  def get(key: Obj): this.type = this.via(this, GetOp(key))
  def get[BB <: Obj](key: Obj, btype: BB): BB = btype.via(this, GetOp(key, btype))
  def put(key: Obj, value: Obj): this.type = this.via(this, PutOp(key, value))
  def inst: Inst[_ <: Obj, _ <: Obj] = this.via._2
}

object __ extends __(Tokens.anon, qOne, rootVia) {
  @inline implicit def symbolToToken(ground: Symbol): __ = __(ground.name)
  @inline implicit def symbolToRichToken(ground: Symbol): RichToken = new RichToken(ground)
  class RichToken(val ground: Symbol) {
    final def apply(obj: Obj): obj.type = obj.named(ground.name)
    final def unapply(arg: RichToken): __ = __(ground.name)
  }
  def apply(name: String): __ = __.named(name)
  def isAnon(aobj: Obj): Boolean = aobj.isInstanceOf[__] && aobj.name.equals(Tokens.anon)
  def isToken(aobj: Obj): Boolean = aobj.isInstanceOf[__] && !aobj.name.equals(Tokens.anon) && !aobj.name.equals(Tokens.obj)
  def isAnonToken(aobj: Obj): Boolean = __.isAnon(aobj) || __.isToken(aobj)
  def isAnonObj(aobj: Obj): Boolean = __.isAnon(aobj) || aobj.name.equals(Tokens.obj)
}

