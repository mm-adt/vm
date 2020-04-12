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

import org.mmadt.language.LanguageException
import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.branch.{BranchOp, ChooseOp}
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.model.{AsOp, ModelOp}
import org.mmadt.language.obj.op.reduce.{CountOp, FoldOp}
import org.mmadt.language.obj.op.sideeffect.ErrorOp
import org.mmadt.language.obj.op.traverser.FromOp
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{strm => _, _}
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
    with EqsOp {
  // quantifier methods
  val q: IntQ
  def q(q: IntQ): this.type = this.clone(
    q = if (this.root) q else multQ(this.via._1, q),
    via = if (this.root) base() else (this.via._1, this.via._2.q(q)).asInstanceOf[ViaTuple])
  def q(single: IntValue): this.type = this.q(single.q(qOne), single.q(qOne))
  def alive(): Boolean = this.q != qZero
  // historic mutations
  def root: Boolean = null == this.via || null == this.via._1
  val via: ViaTuple
  def lineage: List[(Obj, Inst[Obj, Obj])] = if (this.root) Nil else this.via._1.lineage :+ this.via.asInstanceOf[(Obj, Inst[Obj, Obj])]
  def via(obj: Obj, inst: Inst[_ <: Obj, _ <: Obj]): this.type = if (inst.q == qOne && via == (obj, inst)) this else this.clone(q = multQ(obj.q, inst.q), via = (obj, inst))
  // utility methods
  def toStrm: Strm[this.type] = strm[this.type](Iterator[this.type](this))
  def toList: List[this.type] = toStrm.value.toList
  def toSet: Set[this.type] = toStrm.value.toSet
  def ==>[E <: Obj](rangeType: Type[E], model: Model = Model.id): E = this match {
    case atype: Type[_] => Processor.compiler(model).apply(atype, Type.resolve(atype, rangeType))
    case avalue: Value[_] => Processor.iterator(model).apply(avalue, Type.resolve(avalue, rangeType))
  }
  def ===>[E <: Obj](rangeType: E): E = {
    LanguageException.testDomainRange(asType(this), rangeType.asInstanceOf[Type[E]].domain())

    Processor.iterator().apply(this, Type.resolve(this, rangeType.asInstanceOf[Type[E]]))
  } // TODO: necessary for __ typecasting -- weird) (get rid of these methods)

  // pattern matching methods
  def named(_name: String): this.type = this.clone(name = _name)
  val name: String
  def test(other: Obj): Boolean
  def clone(name: String = this.name, value: Any = null, q: IntQ = this.q, via: ViaTuple = this.via): this.type
  def compute[E <: Obj](rangeType: Type[E]): E = rangeType.lineage
    .headOption
    .map(x => x._2.exec(this))
    .map(x => x.compute(rangeType.linvert()))
    .getOrElse(this.asInstanceOf[E])
}

object Obj {
  @inline implicit def booleanToBool(java: Boolean): BoolValue = bool(java)
  @inline implicit def longToInt(java: Long): IntValue = int(java)
  @inline implicit def intToInt(java: scala.Int): IntValue = int(java.longValue())
  @inline implicit def doubleToReal(java: scala.Double): RealValue = real(java)
  @inline implicit def floatToReal(java: scala.Float): RealValue = real(java)
  @inline implicit def stringToStr(java: String): StrValue = str(java)
  @inline implicit def mapToRec[A <: Value[Obj], B <: Value[Obj]](java: Map[A, B]): RecValue[A, B] = vrec[A, B](java)
  @inline implicit def mapToRec[A <: Value[Obj], B <: Value[Obj]](value: (A, B), values: (A, B)*): RecValue[A, B] = vrec(value = value, values = values: _*)
}
