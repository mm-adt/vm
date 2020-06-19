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

package org.mmadt.storage

import java.util.ServiceLoader

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.{BoolType, _}
import org.mmadt.language.obj.value._
import org.mmadt.language.obj.value.strm._
import org.mmadt.language.obj.{ViaTuple, _}
import org.mmadt.storage.StorageFactory.{qOne, qZero}
import org.mmadt.storage.obj.ORec
import org.mmadt.storage.obj.`type`._
import org.mmadt.storage.obj.value._
import org.mmadt.storage.obj.value.strm.util.MultiSet
import org.mmadt.storage.obj.value.strm.{VObjStrm, _}


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait StorageFactory {
  /////////TYPES/////////
  lazy val zeroObj: ObjType = tobj().q(qZero)
  lazy val obj: ObjType = tobj()
  lazy val bool: BoolType = tbool()
  lazy val int: IntType = tint()
  lazy val real: RealType = treal()
  lazy val str: StrType = tstr()
  def rec[A <: Obj, B <: Obj]: Rec[A, B] = new ORec()
  def rec[A <: Obj, B <: Obj](value: (A, B), values: (A, B)*): Rec[A, B] = new ORec(g = (Tokens.`,`, Map(value) ++ values.toMap[A, B]))
  def rec[A <: Obj, B <: Obj](sep: String = Tokens.`,`, map: Map[A, B]): Rec[A, B] = new ORec(g = (sep, map))
  def lst[A <: Obj](sep: String, values: A*): Lst[A] = new VLst[A](g = (sep, values.toList))
  def |[A <: Obj]: Lst[A] = new VLst[A](g = (Tokens.|, List.empty))
  def `;`[A <: Obj]: Lst[A] = new VLst[A](g = (Tokens.`;`, List.empty))
  def `,`[A <: Obj]: Lst[A] = new VLst[A](g = (Tokens.`,`, List.empty))
  //
  def tobj(name: String = Tokens.obj, q: IntQ = qOne, via: ViaTuple = base): ObjType
  def tbool(name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = base): BoolType
  def tint(name: String = Tokens.int, q: IntQ = qOne, via: ViaTuple = base): IntType
  def treal(name: String = Tokens.real, q: IntQ = qOne, via: ViaTuple = base): RealType
  def tstr(name: String = Tokens.str, q: IntQ = qOne, via: ViaTuple = base): StrType
  /////////VALUES/////////
  def obj(g: Any): ObjValue
  def bool(g: Boolean): BoolValue = vbool(g = g)
  def int(g: Long): IntValue = vint(g = g)
  def real(g: Double): RealValue = vreal(g = g)
  def str(g: String): StrValue = vstr(g = g)
  def rec[A <: Value[Obj], B <: Value[Obj]](value1: Rec[A, B], value2: Rec[A, B], valuesN: Rec[A, B]*): RecStrm[A, B] = vrec((List(value1, value2) ++ valuesN).iterator)
  //
  def bool(value1: BoolValue, value2: BoolValue, valuesN: BoolValue*): BoolStrm
  def int(value1: IntValue, value2: IntValue, valuesN: IntValue*): IntStrm
  def real(value1: RealValue, value2: RealValue, valuesN: RealValue*): RealStrm
  def str(value1: StrValue, value2: StrValue, valuesN: StrValue*): StrStrm
  def vrec[A <: Value[Obj], B <: Value[Obj]](value: Iterator[Rec[A, B]]): RecStrm[A, B]
  def vbool(name: String = Tokens.bool, g: Boolean, q: IntQ = qOne, via: ViaTuple = base): BoolValue
  def vint(name: String = Tokens.int, g: Long, q: IntQ = qOne, via: ViaTuple = base): IntValue
  def vreal(name: String = Tokens.real, g: Double, q: IntQ = qOne, via: ViaTuple = base): RealValue
  def vstr(name: String = Tokens.str, g: String, q: IntQ = qOne, via: ViaTuple = base): StrValue
  //
  def strm[O <: Obj](itty: Seq[O]): O
  def strm[O <: Obj]: OStrm[O]
}

object StorageFactory {
  ///////PROVIDERS///////
  val providers: ServiceLoader[StorageProvider] = ServiceLoader.load(classOf[StorageProvider])
  /////////TYPES/////////
  lazy val zeroObj: ObjType = tobj().q(qZero)
  lazy val obj: ObjType = tobj()
  lazy val bool: BoolType = tbool()
  lazy val int: IntType = tint()
  lazy val real: RealType = treal()
  lazy val str: StrType = tstr()
  def rec[A <: Obj, B <: Obj]: Rec[A, B] = new ORec()
  def rec[A <: Obj, B <: Obj](value: (A, B), values: (A, B)*)(implicit f: StorageFactory): Rec[A, B] = new ORec(g = (Tokens.`,`, Map(value) ++ values.toMap[A, B]))
  def rec[A <: Obj, B <: Obj](sep: String, map: Map[A, B])(implicit f: StorageFactory): Rec[A, B] = new ORec(g = (sep, map))
  def lst[A <: Obj](sep: String, values: A*)(implicit f: StorageFactory): Lst[A] = f.lst[A](sep, values: _*)
  def |[A <: Obj](implicit f: StorageFactory): Lst[A] = f.|
  def `;`[A <: Obj](implicit f: StorageFactory): Lst[A] = f.`;`
  def `,`[A <: Obj](implicit f: StorageFactory): Lst[A] = f.`,`

  //
  def tobj(name: String = Tokens.obj, q: IntQ = qOne, via: ViaTuple = base)(implicit f: StorageFactory): ObjType = f.tobj(name, q, via)
  def tbool(name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = base)(implicit f: StorageFactory): BoolType = f.tbool(name, q, via)
  def tint(name: String = Tokens.int, q: IntQ = qOne, via: ViaTuple = base)(implicit f: StorageFactory): IntType = f.tint(name, q, via)
  def treal(name: String = Tokens.real, q: IntQ = qOne, via: ViaTuple = base)(implicit f: StorageFactory): RealType = f.treal(name, q, via)
  def tstr(name: String = Tokens.str, q: IntQ = qOne, via: ViaTuple = base)(implicit f: StorageFactory): StrType = f.tstr(name, q, via)
  /////////VALUES/////////
  def obj(g: Any)(implicit f: StorageFactory): ObjValue = f.obj(g)
  def bool(g: Boolean)(implicit f: StorageFactory): BoolValue = f.bool(g)
  def bool(value1: BoolValue, value2: BoolValue, valuesN: BoolValue*)(implicit f: StorageFactory): BoolStrm = f.bool(value1, value2, valuesN: _*)
  def int(g: Long)(implicit f: StorageFactory): IntValue = f.int(g)
  def int(value1: IntValue, value2: IntValue, valuesN: IntValue*)(implicit f: StorageFactory): IntStrm = f.int(value1, value2, valuesN: _*)
  def real(g: Double)(implicit f: StorageFactory): RealValue = f.vreal(Tokens.real, g, qOne)
  def real(g: Float)(implicit f: StorageFactory): RealValue = f.vreal(Tokens.real, g.doubleValue(), qOne)
  def real(value1: RealValue, value2: RealValue, valuesN: RealValue*)(implicit f: StorageFactory): RealStrm = f.real(value1, value2, valuesN: _*)
  def str(g: String)(implicit f: StorageFactory): StrValue = f.vstr(Tokens.str, g, qOne)
  def str(value1: StrValue, value2: StrValue, valuesN: StrValue*)(implicit f: StorageFactory): StrStrm = f.str(value1, value2, valuesN: _*)
  def rec[A <: Value[Obj], B <: Value[Obj]](value1: Rec[A, B], value2: Rec[A, B], valuesN: Rec[A, B]*)(implicit f: StorageFactory): RecStrm[A, B] = f.rec(value1, value2, valuesN: _*)
  def vrec[A <: Value[Obj], B <: Value[Obj]](values: Iterator[Rec[A, B]])(implicit f: StorageFactory): RecStrm[A, B] = f.vrec(values)
  //
  def vbool(name: String = Tokens.bool, g: Boolean, q: IntQ = qOne, via: ViaTuple = base)(implicit f: StorageFactory): BoolValue = f.vbool(name, g, q, via)
  def vint(name: String = Tokens.int, g: Long, q: IntQ = qOne, via: ViaTuple = base)(implicit f: StorageFactory): IntValue = f.vint(name, g, q, via)
  def vreal(name: String = Tokens.real, g: Double, q: IntQ = qOne, via: ViaTuple = base)(implicit f: StorageFactory): RealValue = f.vreal(name, g, q, via)
  def vstr(name: String = Tokens.str, g: String, q: IntQ = qOne, via: ViaTuple = base)(implicit f: StorageFactory): StrValue = f.vstr(name, g, q, via)
  def strm[O <: Obj](seq: Seq[O])(implicit f: StorageFactory): O = f.strm[O](seq)
  def strm[O <: Obj](implicit f: StorageFactory): OStrm[O] = f.strm[O]
  /////////CONSTANTS//////
  lazy val btrue: BoolValue = bool(g = true)
  lazy val bfalse: BoolValue = bool(g = false)
  lazy val qZero: (IntValue, IntValue) = (int(0), int(0))
  lazy val qOne: (IntValue, IntValue) = (int(1), int(1))
  lazy val qMark: (IntValue, IntValue) = (int(0), int(1))
  lazy val qPlus: (IntValue, IntValue) = (int(1), int(Long.MaxValue))
  lazy val qStar: (IntValue, IntValue) = (int(0), int(Long.MaxValue))
  lazy val * : (IntValue, IntValue) = qStar
  lazy val ? : (IntValue, IntValue) = qMark
  lazy val + : (IntValue, IntValue) = qPlus
  def baseName(obj: Obj): String = obj match {
    case _: Bool => Tokens.bool
    case _: Int => Tokens.int
    case _: Real => Tokens.real
    case _: Str => Tokens.str
    case _: Lst[_] => Tokens.lst
    case _: Rec[_, _] => Tokens.rec
    case _: __ => Tokens.anon
  }
  def asType[O <: Obj](obj: O): OType[O] = (obj match {
    case arec: RecStrm[Obj, Obj] if arec.isValue => rec.q(arec.q) // TODO:
    case atype: Type[_] => atype
    case alst: LstStrm[Obj] if alst.isValue => new TLst[Obj](g = (Tokens.`,`, List.empty[Obj])).q(alst.q) // TODO:
    case alst: Lst[Obj] => new TLst[Obj](g = alst.g).hardQ(alst.q)

    //case arec: Rec[Obj, Obj] if arec.isValue => arec.clone(g = (arec.gsep, arec.gmap.map(x => (asType[Obj](x._1), asType[Obj](x._2)))))

    case _: IntValue | _: IntStrm => tint(name = obj.name, q = obj.q)
    case _: RealValue | _: RealStrm => treal(name = obj.name, q = obj.q)
    case _: StrValue | _: StrStrm => tstr(name = obj.name, q = obj.q)
    case _: BoolValue | _: BoolStrm => tbool(name = obj.name, q = obj.q)
    case _: ObjStrm => tobj(name = obj.name, q = obj.q)

  }).asInstanceOf[OType[O]]
  def isSymbol[O <: Obj](obj: O): Boolean = obj match {
    case _: Value[_] => false
    case atype: Type[_] => atype.root && atype.getClass.equals(tobj().getClass) && !atype.name.equals(Tokens.obj) && !atype.name.equals(Tokens.empty)
  }
  implicit val mmstoreFactory: StorageFactory = new StorageFactory {
    /////////TYPES/////////
    override def tobj(name: String = Tokens.obj, q: IntQ = qOne, via: ViaTuple = base): ObjType = new TObj(name, q, via)
    override def tbool(name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = base): BoolType = new TBool(name, q, via)
    override def tint(name: String = Tokens.int, q: IntQ = qOne, via: ViaTuple = base): IntType = new TInt(name, q, via)
    override def treal(name: String = Tokens.real, q: IntQ = qOne, via: ViaTuple = base): RealType = new TReal(name, q, via)
    override def tstr(name: String = Tokens.str, q: IntQ = qOne, via: ViaTuple = base): StrType = new TStr(name, q, via)
    /////////VALUES/////////
    override def int(g: Long): IntValue = new VInt(g = g) // NECESSARY FOR Q PRELOAD
    override def obj(g: Any): ObjValue = new VObj(g = g)
    override def vbool(name: String, g: Boolean, q: IntQ, via: ViaTuple): BoolValue = new VBool(name, g, q, via)
    override def bool(value1: BoolValue, value2: BoolValue, valuesN: BoolValue*): BoolStrm = new VBoolStrm(values = MultiSet(value1 +: (value2 +: valuesN)))
    override def vint(name: String, g: Long, q: IntQ, via: ViaTuple): IntValue = new VInt(name, g, q, via)
    override def int(value1: IntValue, value2: IntValue, valuesN: IntValue*): IntStrm = new VIntStrm(values = MultiSet(value1 +: (value2 +: valuesN)))
    override def vreal(name: String, g: Double, q: IntQ, via: ViaTuple): RealValue = new VReal(name, g, q, base)
    override def real(value1: RealValue, value2: RealValue, valuesN: RealValue*): RealStrm = new VRealStrm(values = MultiSet(value1 +: (value2 +: valuesN)))
    override def vstr(name: String, g: String, q: IntQ, via: ViaTuple): StrValue = new VStr(name, g, q, base)
    override def str(value1: StrValue, value2: StrValue, valuesN: StrValue*): StrStrm = new VStrStrm(values = MultiSet(value1 +: (value2 +: valuesN)))
    override def vrec[A <: Value[Obj], B <: Value[Obj]](values: Iterator[Rec[A, B]]): RecStrm[A, B] = new VRecStrm(values = MultiSet(values.toSeq))
    //
    override def strm[O <: Obj]: OStrm[O] = new VObjStrm(values = List.empty).asInstanceOf[OStrm[O]]
    override def strm[O <: Obj](values: Seq[O]): O = values.headOption.map {
      case _: Bool => new VBoolStrm(values = MultiSet[BoolValue](values.asInstanceOf[Seq[BoolValue]]))
      case _: Int => new VIntStrm(values = MultiSet(values.asInstanceOf[Seq[IntValue]]))
      case _: Real => new VRealStrm(values = MultiSet(values.asInstanceOf[Seq[RealValue]]))
      case _: Str => new VStrStrm(values = MultiSet(values.asInstanceOf[Seq[StrValue]]))
      case _: Rec[_, _] => new VRecStrm[Value[Obj], Value[Obj]](values = MultiSet(values.asInstanceOf[Seq[Rec[Value[Obj], Value[Obj]]]]))
      case _: VLst[_] => new VLstStrm[Obj](values = MultiSet(values.asInstanceOf[Seq[Lst[Obj]]]))
      case y: TLst[_] => new VLstStrm[Obj](values = MultiSet(values.map(x => new VLst(g = (y.gsep, x.asInstanceOf[TLst[Obj]].glist))).asInstanceOf[Seq[Lst[Obj]]]))
      case _ => new VObjStrm(values = List.empty)
    }.getOrElse(new VObjStrm(values = List.empty)).asInstanceOf[O]
  }
}
