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

import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.branch.{BranchOp, ChooseOp}
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.initial.StartOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.model.{AsOp, ModelOp}
import org.mmadt.language.obj.op.reduce.{CountOp, FoldOp}
import org.mmadt.language.obj.op.sideeffect.ErrorOp
import org.mmadt.language.obj.op.traverser.{FromOp, ToOp}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{strm => _, _}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.processor.Processor
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Obj
  extends AOp
    with AsOp
    with BranchOp
    with CountOp
    with ChooseOp
    with IdOp
    with IsOp
    with FoldOp
    with MapOp
    with ModelOp
    with PathOp
    with FromOp
    with QOp
    with ErrorOp
    with EvalOp
    with EqsOp
    with ToOp
    with StartOp {

  //////////////////////////////////////////////////////////////
  // data associated with every obj
  val name: String // the obj type name TODO: should be ref to type?
  val q: IntQ // the obj quantifier
  val via: ViaTuple // the obj's incoming edge in the obj-graph
  //////////////////////////////////////////////////////////////

  // type methods
  def named(_name: String): this.type = this.clone(name = _name)
  def test(other: Obj): Boolean

  // quantifier methods
  def q(single: IntValue): this.type = this.q(single.q(qOne), single.q(qOne))
  def q(q: IntQ): this.type = this.clone(
    q = if (this.root) q else multQ(this.via._1, q),
    via = if (this.root) base() else (this.via._1, this.via._2.q(q)))
  // quantifier functions
  def hardQ(q: IntQ): this.type = this.clone(q = q)
  def hardQ(single: IntValue): this.type = this.hardQ(single.q(qOne), single.q(qOne))
  def alive(): Boolean = this.q != qZero

  // via methods
  def root: Boolean = null == this.via || null == this.via._1
  def isolate: this.type = this.clone(via = base())
  def via(obj: Obj, inst: Inst[_ <: Obj, _ <: Obj]): this.type = if (inst.q == qOne && via == (obj, inst)) this else this.clone(q = multQ(obj.q, inst.q), via = (obj, inst))
  def lineage: List[(Obj, Inst[Obj, Obj])] = if (this.root) Nil else this.via._1.lineage :+ this.via.asInstanceOf[(Obj, Inst[Obj, Obj])]
  def linvert(): this.type = {
    if (this.root) throw LanguageException.typeError(this, "The obj can not be decomposed beyond root")
    this.lineage.tail match {
      case Nil => this.isolate
      case incidentRoot => incidentRoot.foldLeft[Obj](incidentRoot.head._1.isolate)((btype, inst) => inst._2.exec(btype)).asInstanceOf[this.type]
    }
  }
  def rinvert[R <: Obj](): R = if (this.root) throw LanguageException.typeError(this, "The obj can not be decomposed beyond root") else this.via._1.asInstanceOf[R]

  // utility methods
  def clone(name: String = this.name, value: Any = null, q: IntQ = this.q, via: ViaTuple = this.via): this.type
  def toStrm: Strm[this.type] = strm[this.type](Iterator[this.type](this))
  def toList: List[this.type] = toStrm.value.toList
  def toSet: Set[this.type] = toStrm.value.toSet

  def compute[E <: Obj](rangeType: Type[E]): E = rangeType.lineage
    .headOption
    .map(x => x._2.exec(this))
    .map(x => x.compute(rangeType.linvert()))
    .getOrElse(this.asInstanceOf[E])

  def ==>[E <: Obj](rangeType: Type[E], model: Model = Model.id): E = {
    LanguageException.testDomainRange(asType(this), rangeType.asInstanceOf[Type[E]].domain())
    this match {
      case _: Type[_] => Processor.compiler(model).apply(this, Type.resolve(this, rangeType))
      case _: Value[_] => Processor.iterator(model).apply(this, Type.resolve(this, rangeType))
    }
  }
  def ===>[E <: Obj](rangeType: E): E = {this ==> rangeType.asInstanceOf[Type[E]] }
}

object Obj {
  def fetch[A <: Obj](obj: Obj, label: String): A = {
    val result: Option[A] = Obj.fetchOption[A](obj, label)
    if (result.isEmpty) throw LanguageException.labelNotFound(obj, label)
    result.get
  }
  def fetchOption[A <: Obj](obj: Obj, label: String): Option[A] = {
    obj.lineage.reverse.find(x => x._2.op().equals(Tokens.to) && x._2.arg0[StrValue]().value.equals(label)).map(x => x._1).asInstanceOf[Option[A]]
  }

  @inline implicit def booleanToBool(java: Boolean): BoolValue = bool(java)
  @inline implicit def longToInt(java: Long): IntValue = int(java)
  @inline implicit def intToInt(java: scala.Int): IntValue = int(java.longValue())
  @inline implicit def doubleToReal(java: scala.Double): RealValue = real(java)
  @inline implicit def floatToReal(java: scala.Float): RealValue = real(java)
  @inline implicit def stringToStr(java: String): StrValue = str(java)
  @inline implicit def mapToRec[A <: Value[Obj], B <: Value[Obj]](java: Map[A, B]): RecValue[A, B] = vrec[A, B](java)
  @inline implicit def mapToRec[A <: Value[Obj], B <: Value[Obj]](value: (A, B), values: (A, B)*): RecValue[A, B] = vrec(value = value, values = values: _*)
}
