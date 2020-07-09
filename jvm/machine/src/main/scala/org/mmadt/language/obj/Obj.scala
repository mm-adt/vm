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

import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.branch._
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.initial.StartOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.reduce.{CountOp, FoldOp}
import org.mmadt.language.obj.op.sideeffect.{ErrorOp, LoadOp}
import org.mmadt.language.obj.op.trace._
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
    with AndOp
    with OrOp
    with CountOp
    with DefineOp
    with DefsOp
    with IdOp
    with IsOp
    with FoldOp
    with MapOp
    with NotOp
    with GivenOp
    with JuxtaOp
    with PathOp
    with FromOp
    with LoadOp
    with QOp
    with ErrorOp
    with EvalOp
    with EqsOp
    with ToOp
    with TraceOp
    with StartOp
    with SplitOp
    with RewriteOp
    with RepeatOp[Obj] {

  //////////////////////////////////////////////////////////////
  // data associated with every obj
  val name: String // the obj type name TODO: should be ref to type?
  val q: IntQ // the obj quantifier
  val via: ViaTuple // the obj's incoming edge in the obj-graph
  //////////////////////////////////////////////////////////////

  // type methods
  def named(name: String): this.type = {
    LanguageException.checkAnonymousTypeName(this, name)
    this.clone(name = name)
  }
  def test(other: Obj): Boolean
  def <=[D <: Obj](domainType: D): this.type = {
    if (domainType.root) this.clone(via = (domainType, NoOp()))
    else this.clone(via = (domainType.rinvert(), domainType.via._2))
  }
  def range: Type[Obj] = asType(this.isolate)
  def domain[D <: Obj]: Type[D] = if (this.root) asType(this).asInstanceOf[Type[D]] else asType(this.via._1).domain[D]

  // quantifier methods
  def q(single: IntValue): this.type = this.q(single.q(qOne), single.q(qOne))
  def q(q: IntQ): this.type = if (q.equals(qZero)) this.isolate.clone(q = qZero) else this.clone(
    q = if (this.root) q else multQ(this.q, q),
    via = if (this.root) base else (this.via._1, this.via._2.q(q)))
  def hardQ(q: IntQ): this.type = this.clone(q = q)
  def hardQ(single: IntValue): this.type = this.hardQ(single.q(qOne), single.q(qOne))
  def alive: Boolean = this.q != qZero

  // via methods
  def root: Boolean = null == this.via || null == this.via._1
  def isolate: this.type = this.clone(q = this.q, via = base) // TODO: rename to like start/end (the non-typed versions of domain/range)
  def domainObj[D <: Obj]: D = if (this.root) this.asInstanceOf[D] else this.via._1.domainObj[D] // TODO: rename to like start/end (the non-typed versions of domain/range)
  def via(obj: Obj, inst: Inst[_ <: Obj, _ <: Obj]): this.type = this.clone(q = if (this.alive) multQ(obj.q, inst.q) else qZero, via = (obj, inst))
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
  //
  final def `;`[A <: Obj](obj: scala.Double): Lst[A] = this.`;`(real(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def `;`[A <: Obj](obj: scala.Long): Lst[A] = this.`;`(int(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def `;`[A <: Obj](obj: scala.Int): Lst[A] = this.`;`(int(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def `;`[A <: Obj](obj: String): Lst[A] = this.`;`(str(obj).asInstanceOf[A]) // TODO: figure out how to do this implicitly
  final def `;`[A <: Obj]: Lst[A] = lst(Tokens.`;`, this.asInstanceOf[A])
  final def `;`[A <: Obj](obj: A): Lst[A] = this.polyMaker(Tokens.`;`, obj)
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
        case bpoly: Lst[A] => lst(g = (sep, List(apoly.asInstanceOf[A], bpoly.asInstanceOf[A])))
        case _ => lst(g = (apoly.gsep, apoly.glist :+ obj))
      }
      case _ => lst(g = (sep, List(this.asInstanceOf[A], obj)))
    }
  }

  // utility methods
  def clone(name: String = this.name, g: Any = null, q: IntQ = this.q, via: ViaTuple = this.via): this.type
  def toStrm: Strm[this.type] = strm[this.type](Seq[this.type](this)).asInstanceOf[Strm[this.type]]

  def compute[E <: Obj](rangeType: E): E = rangeType match {
    case _: Type[E] if __.isAnonRoot(this) && rangeType.root => rangeType.hardQ(multQ(this.q, rangeType.q))
    case _: Type[E] =>
      if (this.isInstanceOf[Type[_]] && this.root && rangeType.root)
        LanguageException.testTypeCheck(this, asType(rangeType).hardQ(this.q))
      Tokens.tryName[E](rangeType, rangeType.trace
        .headOption
        .map(x => x._2.exec(this))
        .map(x => x.compute(rangeType.linvert()))
        .getOrElse(this.asInstanceOf[E]))
    case _ => rangeType
  }

  def ==>[E <: Obj](rangeType: Type[E]): E = {
    if (!rangeType.alive) return zeroObj.asInstanceOf[E]
    LanguageException.testTypeCheck(range.range, rangeType.domain)
    this match {
      case _: Value[_] => Processor.iterator(this, rangeType)
      case _: Type[_] => Processor.compiler(this, rangeType)
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
  def copyDefinitions[A <: Obj](parent: Obj, child: A): A = parent.trace.filter(x => x._2.op.equals(Tokens.define)).foldLeft(child)((a, b) => b._2.exec(a).asInstanceOf[A])

  @scala.annotation.tailrec
  def fetch(start: Obj, search: Obj): Boolean = {
    start match {
      case x if x.root => false
      case x if x.via._2.op == Tokens.to && x.via._2.arg0[StrValue].g == search.name => true
      case x if x.via._2.op == Tokens.define && x.via._2.args.exists(y => y.trace == search.trace) => true
      case x if x.via._2.op == Tokens.rewrite && x.via._2.arg0[Obj].trace == search.trace && x.via._2.arg0[Obj].equals(search) => true // TODO: trace search because poly values (bad?)
      case x => fetch(x.via._1, search)
    }
  }

  @scala.annotation.tailrec
  def fetchOption[A <: Obj](source: Obj, obj: Obj, label: String): Option[A] = {
    obj match {
      case x if x.root => None
      case x if x.via._2.op == Tokens.to && x.via._2.arg0[StrValue].g == label => obj match {
        case _: Value[Obj] => Some(x.via._1.asInstanceOf[A])
        case _: Type[Obj] => Some(x.via._1.range.from(label).asInstanceOf[A])
      }
      case x if x.via._2.op == Tokens.define && x.via._2.args.exists(y => y.name == label && source.test(asType(y.domain))) =>
        Some(toBaseName(x.via._2.args.find(y => y.name == label && source.test(asType(y.domain))).get.asInstanceOf[A]))
      case x if x.via._2.op == Tokens.rewrite && x.via._2.arg0[Obj].name == label =>
        Some(Inst.resolveArg(obj, x.via._2.arg0[A]))
      case x =>
        fetchOption(source, x.via._1, label)
    }
  }


  @scala.annotation.tailrec
  def fetchWithInstOption[A <: Obj](obj: Obj, label: String): Option[(String, A)] = {
    obj match {
      case x if x.root => None
      case x if x.via._2.op == Tokens.to && x.via._2.arg0[StrValue].g == label =>
        Some((Tokens.to, x.via._1.asInstanceOf[A]))
      case x if x.via._2.op == Tokens.define && x.via._2.args.exists(y => y.name == label) =>
        Some((Tokens.define, x.via._2.args.find(y => y.name == label).get.asInstanceOf[A]))
      case x if x.via._2.op == Tokens.rewrite && x.via._2.arg0[Obj].name == label =>
        Some((Tokens.rewrite, x.via._2.arg0[A]))
      case x => fetchWithInstOption(x.via._1, label)
    }
  }

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
