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

import java.util.NoSuchElementException
import org.mmadt.language.obj.op.model.{ModelOp, NoOp}
import org.mmadt.language.obj.op.sideeffect.AddOp
import org.mmadt.language.obj.op.traverser.ExplainOp
import org.mmadt.language.obj.value.{IntValue, Value}
import org.mmadt.language.obj.{eqQ, _}
import org.mmadt.language.{LanguageException, LanguageFactory, Tokens}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Type[+T <: Obj] extends Obj
  with AddOp
  with ModelOp
  with ExplainOp {
  this: T =>
  def isDerived: Boolean = !this.root
  def hardQ(quantifier: IntQ): this.type = this.clone(_name = this.name, _quantifier = quantifier, _via = this.via)
  def hardQ(single: IntValue): this.type = this.hardQ(single.q(qOne), single.q(qOne))
  // type properties
  lazy val range: this.type = this.clone(_name = this.name, _quantifier = this.q, _via = base())
  def domain[D <: Obj](): Type[D] = if (this.root) this.asInstanceOf[Type[D]] else this.via._1.asInstanceOf[Type[D]].domain[D]()
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
  def rinvert[R <: Type[Obj]](): R = if (this.root) throw new NoSuchElementException else this.via._1.asInstanceOf[R] // TODO: ctypes just return themselves?

  // type specification and compilation
  final def <=[D <: Obj](domainType: Type[D]): this.type = {
    LanguageException.testDomainRange(this, domainType)

    Some(domainType).filter(x => x.root).map(_.id()).getOrElse(domainType).compose(this).hardQ(this.q).asInstanceOf[this.type]
  }
  // type constructors via stream ring theory // TODO: figure out how to get this into [mult][plus] compositions
  def compose[R <: Type[Obj]](btype: R): R = btype match {
    case anon: __ => anon(this)
    case atype: Type[Obj] => atype.lineage.seq.foldLeft[Obj](this)((b, a) => a._2.exec(b)).asInstanceOf[R].compose(btype.range, NoOp())
  }
  def compose(inst: Inst[_ <: Obj, _ <: Obj]): this.type = this.compose(this, inst)
  def compose[R <: Obj](nextObj: R, inst: Inst[_ <: Obj, _ <: Obj]): R = (nextObj match {
    case _: __ => new __(multQ(this, inst), if (inst.op().equals(Tokens.noop)) this.lineage else this.lineage ::: List((this, inst)))
    case _ => asType[Obj](nextObj).clone(_name = nextObj.name, _quantifier = multQ(this, inst), _via = if (inst.op().equals(Tokens.noop)) this.via else (this, inst))
  }).asInstanceOf[R]
  // obj-level operations
  override def add[O <: Obj](obj: O): O = this.compose(asType(obj).asInstanceOf[O], AddOp(obj))
  // pattern matching methods
  override def test(other: Obj): Boolean = other match {
    case argValue: Value[_] => TypeChecker.matchesTV(this, argValue)
    case argType: Type[_] => TypeChecker.matchesTT(this, argType)
  }
  // standard Java implementations
  override def toString: String = LanguageFactory.printType(this)
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.q.hashCode() ^ this.lineage.hashCode()
  override def equals(other: Any): Boolean = other match {
    case atype: Type[_] =>
      if (this.root)
        atype.root && atype.name.equals(this.name) && eqQ(atype, this)
      else if (this.isInstanceOf[__] && atype.isInstanceOf[__]) // TODO: have it work generically with types (and make it recurssive)
      atype.isDerived && atype.name.equals(this.name) && eqQ(atype, this) && this.lineage.map(x => x._2) == atype.lineage.map(x => x._2)
        else
        atype.isDerived && atype.name.equals(this.name) && eqQ(atype, this) && (this.via._2 == atype.via._2 && this.via._1 == atype.via._1)
    case _ => false
  }
}

object Type {
  @scala.annotation.tailrec
  def createInstList(list: List[(Type[Obj], Inst[Obj, Type[Obj]])], atype: Type[Obj]): List[(Type[Obj], Inst[Obj, Type[Obj]])] = {
    if (atype.root) list else createInstList(List((atype.range, atype.lineage.last._2.asInstanceOf[Inst[Obj, Type[Obj]]])) ::: list, atype.lineage.last._1.asInstanceOf[Type[Obj]])
  }
  // domain/range specifies anonymous types
  def resolve[R <: Obj](objA: Obj, objB: R): R = objB match {
    case x: __ => x(objA)
    case x: RecType[Obj, Obj] => trec(name = x.name, value = x.value().map(a => resolve(objA, a._1) -> resolve(objA, a._2)), q = x.q, via = x.via).asInstanceOf[R]
    case _ => objB
  }
}
