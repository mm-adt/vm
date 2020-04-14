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

import org.mmadt.language.obj.op.model.ModelOp
import org.mmadt.language.obj.op.sideeffect.AddOp
import org.mmadt.language.obj.op.traverser.ExplainOp
import org.mmadt.language.obj.value.{IntValue, Value}
import org.mmadt.language.obj.{eqQ, _}
import org.mmadt.language.{LanguageException, LanguageFactory}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Type[+T <: Obj] extends Obj
  with AddOp
  with ModelOp
  with ExplainOp {
  this: T =>

  // quantifier functions
  def hardQ(quantifier: IntQ): this.type = this.clone(name = this.name, q = quantifier, via = this.via)
  def hardQ(single: IntValue): this.type = this.hardQ(single.q(qOne), single.q(qOne))

  // type signature properties and functions
  def range: this.type = this.clone(via = base())
  def domain[D <: Obj](): Type[D] = if (this.root) this.asInstanceOf[Type[D]] else this.via._1.asInstanceOf[Type[D]].domain[D]()
  def <=[D <: Obj](domainType: Type[D]): this.type = {
    LanguageException.testDomainRange(this, domainType)
    Some(domainType).filter(x => x.root).map(_.id()).getOrElse(domainType).compose(this).hardQ(this.q).asInstanceOf[this.type]
  }

  // type manipulation functions
  def linvert(): this.type = {
    ((this.lineage.tail match {
      case Nil => this.range
      case i => i.foldLeft[Obj](i.head._1.asInstanceOf[Type[Obj]].range)((btype, inst) => inst._2.exec(btype))
    }) match {
      case vv: Value[_] => vv.start()
      case x => x
    }).asInstanceOf[this.type]
  }
  def rinvert[R <: Type[Obj]](): R = if (this.root) throw LanguageException.typeError(this, "The type can not be decomposed beyond it's canonical form") else this.via._1.asInstanceOf[R]

  // type constructors via stream ring theory
  def compose[R <: Type[Obj]](btype: R): R = btype.lineage.seq.foldLeft[Obj](this)((b, a) => a._2.exec(b)).asInstanceOf[R]

  // pattern matching methods
  override def test(other: Obj): Boolean = other match {
    case argValue: Value[_] => TypeChecker.matchesTV(this, argValue)
    case argType: Type[_] => TypeChecker.matchesTT(this, argType)
  }
  // standard Java implementations
  override def toString: String = LanguageFactory.printType(this)
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.q.hashCode() ^ this.lineage.hashCode()
  override def equals(other: Any): Boolean = other match {
    case atype: Type[_] => atype.name.equals(this.name) && eqQ(atype, this) && ((this.root && atype.root) || (this.via == atype.via))
    case _ => false
  }

  // obj-level operations TODO: remove
  override def add[O <: Obj](obj: O): O = asType(obj).asInstanceOf[O].via(this, AddOp(obj))
}

object Type {
  // domain/range specifies anonymous types
  def resolve[R <: Obj](objA: Obj, objB: R): R = objB match {
    case x: __ => x(objA)
    case x: RecType[Obj, Obj] => trec(name = x.name, value = x.value().map(a => resolve(objA, a._1) -> resolve(objA, a._2)), q = x.q, via = x.via).asInstanceOf[R]
    case _ => objB
  }
}
