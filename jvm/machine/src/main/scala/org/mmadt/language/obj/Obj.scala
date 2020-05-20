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
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.branch._
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.initial.StartOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.model.{AsOp, ModelOp}
import org.mmadt.language.obj.op.reduce.{CountOp, FoldOp}
import org.mmadt.language.obj.op.sideeffect.ErrorOp
import org.mmadt.language.obj.op.trace.{FromOp, ToOp, TraceOp}
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
    with CountOp
    with IdOp
    with IsOp
    with FoldOp
    with MapOp
    with ModelOp
    with GivenOp
    with PathOp
    with FromOp
    with QOp
    with ErrorOp
    with EvalOp
    with EqsOp
    with ToOp
    with TraceOp
    with StartOp
    with SplitOp
    with RepeatOp[Obj] {

  //////////////////////////////////////////////////////////////
  // data associated with every obj
  val name: String // the obj type name TODO: should be ref to type?
  val q: IntQ // the obj quantifier
  val via: ViaTuple // the obj's incoming edge in the obj-graph
  //////////////////////////////////////////////////////////////

  // type methods
  def named(name: String): this.type = this.clone(name = name)
  def test(other: Obj): Boolean
  def <=[D <: Obj](domainType: D): this.type = {
    LanguageException.testDomainRange(asType(this), asType(domainType))
    domainType.compute(asType(this)).hardQ(this.q).asInstanceOf[this.type]
  }
  def range: Type[Obj] = asType(this.isolate)
  def domain[D <: Obj]: Type[D] = if (this.root) asType(this).asInstanceOf[Type[D]] else asType(this.via._1).domain[D]

  // quantifier methods
  def q(single: IntValue): this.type = this.q(single.q(qOne), single.q(qOne))
  def q(q: IntQ): this.type = if (q.equals(qZero)) this.isolate.clone(q = qZero) else this.clone(
    q = if (this.root) q else multQ(this.via._1, q),
    via = if (this.root) base else (this.via._1, this.via._2.q(q)))
  def hardQ(q: IntQ): this.type = this.clone(q = q)
  def hardQ(single: IntValue): this.type = this.hardQ(single.q(qOne), single.q(qOne))
  def alive: Boolean = this.q != qZero

  // via methods
  def root: Boolean = null == this.via || null == this.via._1
  def isolate: this.type = this.clone(q = this.q, via = base) // TODO: rename to like start/end (the non-typed versions of domain/range)
  def domainObj[D <: Obj](): D = if (this.root) this.asInstanceOf[D] else this.via._1.domainObj[D]() // TODO: rename to like start/end (the non-typed versions of domain/range)
  def via(obj: Obj, inst: Inst[_ <: Obj, _ <: Obj]): this.type = this.clone(q = multQ(obj.q, inst.q), via = (obj, inst))
  def trace: List[(Obj, Inst[Obj, Obj])] = if (this.root) Nil else this.via._1.trace :+ this.via.asInstanceOf[(Obj, Inst[Obj, Obj])]
  def rinvert[R <: Obj](): R = if (this.root) throw LanguageException.zeroLengthPath(this) else this.via._1.asInstanceOf[R]
  def linvert(): this.type = {
    if (this.root) throw LanguageException.zeroLengthPath(this)
    this.trace.tail match {
      case Nil => this.isolate
      case incidentRoot => incidentRoot.foldLeft[Obj](incidentRoot.head._1.isolate)((btype, inst) => inst._2.exec(btype)).asInstanceOf[this.type]
    }
  }

  // poly fluent methods
  final def |[A <: Obj](obj: scala.Double): Lst[A] = this.|(real(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def |[A <: Obj](obj: scala.Long): Lst[A] = this.|(int(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def |[A <: Obj](obj: scala.Int): Lst[A] = this.|(int(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def |[A <: Obj](obj: String): Lst[A] = this.|(str(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def |[A <: Obj]: Lst[A] = lst(Tokens.|, this.asInstanceOf[A])
  final def |[A <: Obj](obj: A): Lst[A] = this.polyMaker(Tokens.|, obj)
  //final def |[A <: Obj](obj: Tuple2[String, A]): Poly[A] = this.polyMaker(Tokens.:|, obj)
  //
  final def `;`[A <: Obj](obj: scala.Double): Lst[A] = this.`;`(real(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def `;`[A <: Obj](obj: scala.Long): Lst[A] = this.`;`(int(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def `;`[A <: Obj](obj: scala.Int): Lst[A] = this.`;`(int(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def `;`[A <: Obj](obj: String): Lst[A] = this.`;`(str(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def `;`[A <: Obj]: Lst[A] = lst(Tokens.`;`, this.asInstanceOf[A])
  final def `;`[A <: Obj](obj: A): Lst[A] = this.polyMaker(Tokens.`;`, obj)
  //final def /[A <: Obj](obj: (String, A)): Poly[A] = this.polyMaker(Tokens.:/, obj)
  //
  final def `,`[A <: Obj](obj: scala.Double): Lst[A] = this.`,`(real(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def `,`[A <: Obj](obj: scala.Long): Lst[A] = this.`,`(int(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def `,`[A <: Obj](obj: scala.Int): Lst[A] = this.`,`(int(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def `,`[A <: Obj](obj: String): Lst[A] = this.`,`(str(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def `,`[A <: Obj]: Lst[A] = lst(Tokens.`,`, this.asInstanceOf[A])
  final def `,`[A <: Obj](obj: A): Lst[A] = this.polyMaker(Tokens.`,`, obj)
  /////////////////
  private final def polyMaker[A <: Obj](sep: String, obj: A): Lst[A] = {
    this match {
      case apoly: Lst[A] => obj match {
        case bpoly: Lst[A] => lst(sep, List(apoly.asInstanceOf[A], bpoly.asInstanceOf[A]): _*)
        case _ => apoly.clone(apoly.glist :+ obj)
      }
      case _ => lst(sep, this.asInstanceOf[A], obj)
    }
  }
  /*private def polyMaker[A <: Obj](sep: String, obj: (String, A)): Poly[A] = {
    this match {
      case apoly: Poly[A] => obj._2 match {
        case _: Poly[A] => poly(sep, List(this.asInstanceOf[A], obj._2): _*)
        case _ => apoly.clone(ground = (sep, apoly.groundList :+ obj._2, apoly.groundKeys :+ obj._1))
      }
      case _ => poly[A](sep).clone(ground = (sep, List(this.asInstanceOf[A], obj._2), List(null, obj._1)))
    }
  }*/

  // utility methods
  def clone(name: String = this.name, g: Any = null, q: IntQ = this.q, via: ViaTuple = this.via): this.type
  def toStrm: Strm[this.type] = strm[this.type](Seq[this.type](this)).asInstanceOf[Strm[this.type]]

  def compute[E <: Obj](rangeType: Type[E]): E = rangeType.trace
    .headOption
    .map(x => x._2.exec(this))
    .map(x => x.compute(rangeType.linvert()))
    .getOrElse(this.asInstanceOf[E])

  def ==>[E <: Obj](rangeType: Type[E], model: Model = Model.id): E = {
    LanguageException.testTypeCheck(range.range, rangeType.asInstanceOf[Type[E]].domain)
    this match {
      case _: Value[_] => Processor.iterator(model).apply(this, rangeType)
      case _: Type[_] => Processor.compiler(model).apply(this, rangeType)
    }
  }
  def ===>[E <: Obj](rangeType: E): E = {
    rangeType match {
      case _: Type[_] => this ==> rangeType.asInstanceOf[Type[E]]
      case _ => rangeType
    }
  }
}

object Obj {
  def fetch[A <: Obj](obj: Obj, label: String): A = {
    val result: Option[A] = Obj.fetchOption[A](obj, label)
    if (result.isEmpty) throw LanguageException.labelNotFound(obj.tracer(zeroObj `;` __), label)
    result.get
  }

  @scala.annotation.tailrec
  def fetchOption[A <: Obj](obj: Obj, label: String): Option[A] = {
    obj match {
      case x if x.root => None
      case x if x.via._2.op == Tokens.to && x.via._2.arg0[StrValue].g == label => Some(x.via._1.asInstanceOf[A])
      case x => fetchOption(x.via._1, label)
    }
  }

  //@inline implicit def tupleToPoly[A <: Obj](keyObj: (String, A)): Poly[A] = new OPoly[A](ground = (Tokens.:|, List(keyObj._2), List(keyObj._1)))
  @inline implicit def booleanToBool(ground: Boolean): BoolValue = bool(ground)
  @inline implicit def longToInt(ground: Long): IntValue = int(ground)
  @inline implicit def intToInt(ground: scala.Int): IntValue = int(ground.longValue())
  @inline implicit def doubleToReal(ground: scala.Double): RealValue = real(ground)
  @inline implicit def floatToReal(ground: scala.Float): RealValue = real(ground)
  @inline implicit def stringToStr(ground: String): StrValue = str(ground)

  @inline implicit class BooleanExtensions(b: Boolean)
  @inline implicit class StringExtensions(s: String)
  @inline implicit class IntegerExtensions(i: scala.Int)
  @inline implicit class LongExtensions(l: Long)
  @inline implicit class FloatExtensions(f: Float)
  @inline implicit class DoubleExtensions(d: Double)

}
