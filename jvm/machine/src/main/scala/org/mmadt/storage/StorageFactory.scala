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

package org.mmadt.storage

import java.util.ServiceLoader

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Lst.LstTuple
import org.mmadt.language.obj.Obj.{IntQ, ViaTuple, rootVia}
import org.mmadt.language.obj.Rec.RecTuple
import org.mmadt.language.obj.`type`.{BoolType, _}
import org.mmadt.language.obj.value._
import org.mmadt.language.obj.value.strm._
import org.mmadt.language.obj.{Lst, _}
import org.mmadt.storage.StorageFactory.{qOne, qZero}
import org.mmadt.storage.obj.`type`._
import org.mmadt.storage.obj.value._
import org.mmadt.storage.obj.value.strm.util.MultiSet
import org.mmadt.storage.obj.value.strm.{VObjStrm, _}
import org.mmadt.storage.obj.{OLst, ORec}

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
  def rec[A <: Obj, B <: Obj]: Rec[A, B] = ORec.emptyType
  def rec[A <: Obj, B <: Obj](name: String = Tokens.rec, g: RecTuple[A, B] = (Tokens.`,`, List.empty), q: IntQ = qOne, via: ViaTuple = rootVia): Rec[A, B] = ORec.makeRec(name, g, q, via)
  def lst[A <: Obj]: Lst[A] = OLst.emptyType
  def lst[A <: Obj](single: A): Lst[A] = lst(g = (Tokens.`,`, List(single)))
  def lst[A <: Obj](name: String = Tokens.lst, g: LstTuple[A] = (Tokens.`,`, List.empty), q: IntQ = qOne, via: ViaTuple = rootVia): Lst[A] = OLst.makeLst(name, g, q, via)
  def lst[A <: Obj](sep: String, values: A*): Lst[A] = OLst.makeLst(g = (sep, values.toList))
  //
  def tobj(name: String = Tokens.obj, q: IntQ = qOne, via: ViaTuple = rootVia): ObjType
  def tbool(name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = rootVia): BoolType
  def tint(name: String = Tokens.int, q: IntQ = qOne, via: ViaTuple = rootVia): IntType
  def treal(name: String = Tokens.real, q: IntQ = qOne, via: ViaTuple = rootVia): RealType
  def tstr(name: String = Tokens.str, q: IntQ = qOne, via: ViaTuple = rootVia): StrType
  //
  def bool(value1: BoolValue, value2: BoolValue, valuesN: BoolValue*): BoolStrm
  def int(value1: IntValue, value2: IntValue, valuesN: IntValue*): IntStrm
  def real(value1: RealValue, value2: RealValue, valuesN: RealValue*): RealStrm
  def str(value1: StrValue, value2: StrValue, valuesN: StrValue*): StrStrm
  def bool(g: Boolean, name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = rootVia): BoolValue
  def int(g: Long, name: String = Tokens.int, q: IntQ = qOne, via: ViaTuple = rootVia): IntValue
  def real(g: Double, name: String = Tokens.real, q: IntQ = qOne, via: ViaTuple = rootVia): RealValue
  def str(g: String, name: String = Tokens.str, q: IntQ = qOne, via: ViaTuple = rootVia): StrValue
  //
  def strm[O <: Obj](objs: Seq[O]): OStrm[O]
  def strm[O <: Obj]: OStrm[O]
}

object StorageFactory {
  ///////PROVIDERS///////
  val providers: ServiceLoader[StorageProvider] = ServiceLoader.load(classOf[StorageProvider])
  /////////TYPES/////////
  def deadObj[O <: Obj]: O = zeroObj.asInstanceOf[O]
  lazy val zeroObj: ObjType = tobj().q(qZero)
  lazy val obj: ObjType = tobj()
  lazy val bool: BoolType = tbool()
  lazy val int: IntType = tint()
  lazy val real: RealType = treal()
  lazy val str: StrType = tstr()
  def rec[A <: Obj, B <: Obj](implicit f: StorageFactory): RecType[A, B] = f.rec[A, B].asInstanceOf[RecType[A, B]]
  def rec[A <: Obj, B <: Obj](value: (A, B), values: (A, B)*)(implicit f: StorageFactory): Rec[A, B] = f.rec(g = (Tokens.`,`, List(value) ++ values.toList))
  def rec[A <: Obj, B <: Obj](name: String = Tokens.rec, g: RecTuple[A, B] = (Tokens.`,`, List.empty), q: IntQ = qOne, via: ViaTuple = rootVia)(implicit f: StorageFactory): Rec[A, B] = ORec.makeRec(name, g, q, via)
  def lst[A <: Obj](single: A)(implicit f: StorageFactory): Lst[A] = f.lst[A](single)
  def lst[A <: Obj](implicit f: StorageFactory): LstType[A] = f.lst[A].asInstanceOf[LstType[A]]
  def lst[A <: Obj](sep: String, values: A*)(implicit f: StorageFactory): Lst[A] = f.lst[A](sep, values: _*)
  def lst[A <: Obj](name: String = Tokens.lst, g: LstTuple[A] = (Tokens.`,`, List.empty), q: IntQ = qOne, via: ViaTuple = rootVia)(implicit f: StorageFactory): Lst[A] = OLst.makeLst(name, g, q, via)
  //
  def tobj(name: String = Tokens.obj, q: IntQ = qOne, via: ViaTuple = rootVia)(implicit f: StorageFactory): ObjType = f.tobj(name, q, via)
  def tbool(name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = rootVia)(implicit f: StorageFactory): BoolType = f.tbool(name, q, via)
  def tint(name: String = Tokens.int, q: IntQ = qOne, via: ViaTuple = rootVia)(implicit f: StorageFactory): IntType = f.tint(name, q, via)
  def treal(name: String = Tokens.real, q: IntQ = qOne, via: ViaTuple = rootVia)(implicit f: StorageFactory): RealType = f.treal(name, q, via)
  def tstr(name: String = Tokens.str, q: IntQ = qOne, via: ViaTuple = rootVia)(implicit f: StorageFactory): StrType = f.tstr(name, q, via)
  /////////VALUES/////////
  def bool(value1: BoolValue, value2: BoolValue, valuesN: BoolValue*)(implicit f: StorageFactory): BoolStrm = f.bool(value1, value2, valuesN: _*)
  def int(value1: IntValue, value2: IntValue, valuesN: IntValue*)(implicit f: StorageFactory): IntStrm = f.int(value1, value2, valuesN: _*)
  def real(value1: RealValue, value2: RealValue, valuesN: RealValue*)(implicit f: StorageFactory): RealStrm = f.real(value1, value2, valuesN: _*)
  def str(value1: StrValue, value2: StrValue, valuesN: StrValue*)(implicit f: StorageFactory): StrStrm = f.str(value1, value2, valuesN: _*)
  //
  def bool(g: Boolean, name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = rootVia)(implicit f: StorageFactory): BoolValue = f.bool(g, name, q, via)
  def int(g: Long, name: String = Tokens.int, q: IntQ = qOne, via: ViaTuple = rootVia)(implicit f: StorageFactory): IntValue = f.int(g, name, q, via)
  def real(g: Double, name: String = Tokens.real, q: IntQ = qOne, via: ViaTuple = rootVia)(implicit f: StorageFactory): RealValue = f.real(g, name, q, via)
  def str(g: String, name: String = Tokens.str, q: IntQ = qOne, via: ViaTuple = rootVia)(implicit f: StorageFactory): StrValue = f.str(g, name, q, via)

  def obj(obj: Obj, objs: Obj*): Obj = strm[Obj](obj +: objs.toList)
  def strm[O <: Obj](objs: O*): OStrm[O] = strm[O](objs.toList)
  def strm[O <: Obj](seq: Seq[O])(implicit f: StorageFactory): OStrm[O] = f.strm[O](seq)
  def strm[O <: Obj](implicit f: StorageFactory): OStrm[O] = f.strm[O]
  /////////CONSTANTS//////
  lazy val btrue: BoolValue = bool(g = true)
  lazy val bfalse: BoolValue = bool(g = false)
  lazy val qZero: (IntValue, IntValue) = (int(0), int(0))
  lazy val qOne: (IntValue, IntValue) = (new VInt(g = 1), new VInt(g = 1))
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
    case _ => Tokens.obj
  }
  def toBaseName[A <: Obj](obj: A): A = obj.clone(name = baseName(obj))
  def asType[O <: Obj](obj: O): OType[O] = (obj match {
    case atype: Type[_] => atype
    case arec: RecStrm[Obj, Obj] => asType[O](arec.values.headOption.getOrElse(zeroObj).asInstanceOf[O])
    case alst: LstStrm[Obj] => asType[O](alst.values.headOption.getOrElse(zeroObj).asInstanceOf[O])
    case alst: LstValue[Obj] => if (alst.isEmpty) lst.q(obj.q) else lst(name = obj.name, g = (alst.gsep, if (alst.ctype) null else alst.glist.map(x => asType(x))), q = obj.q, via = alst.via)
    case arec: RecValue[Obj, Obj] => if (arec.isEmpty) rec.q(obj.q) else rec(name = obj.name, g = (arec.gsep, if (arec.ctype) null else arec.gmap.map(x => x._1 -> asType(x._2))), q = obj.q, via = arec.via)
    //
    case _: IntValue | _: IntStrm => tint(name = obj.name, q = obj.q)
    case _: RealValue | _: RealStrm => treal(name = obj.name, q = obj.q)
    case _: StrValue | _: StrStrm => tstr(name = obj.name, q = obj.q)
    case _: BoolValue | _: BoolStrm => tbool(name = obj.name, q = obj.q)
    case _: ObjStrm => tobj(name = obj.name, q = obj.q)

  }).asInstanceOf[OType[O]]
  implicit val mmstoreFactory: StorageFactory = new StorageFactory {
    /////////TYPES/////////
    override def tobj(name: String = Tokens.obj, q: IntQ = qOne, via: ViaTuple = rootVia): ObjType = new TObj(name, q, via)
    override def tbool(name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = rootVia): BoolType = new TBool(name, q, via)
    override def tint(name: String = Tokens.int, q: IntQ = qOne, via: ViaTuple = rootVia): IntType = new TInt(name, q, via)
    override def treal(name: String = Tokens.real, q: IntQ = qOne, via: ViaTuple = rootVia): RealType = new TReal(name, q, via)
    override def tstr(name: String = Tokens.str, q: IntQ = qOne, via: ViaTuple = rootVia): StrType = new TStr(name, q, via)
    /////////VALUES/////////
    override def bool(value1: BoolValue, value2: BoolValue, valuesN: BoolValue*): BoolStrm = new VBoolStrm(values = MultiSet(value1 +: (value2 +: valuesN)))
    override def bool(g: Boolean, name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = rootVia): BoolValue = new VBool(name, g, q, via)
    override def int(g: Long, name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = rootVia): IntValue = new VInt(name, g, q, via)
    override def int(value1: IntValue, value2: IntValue, valuesN: IntValue*): IntStrm = new VIntStrm(values = MultiSet(value1 +: (value2 +: valuesN)))
    override def real(g: Double, name: String = Tokens.real, q: IntQ, via: ViaTuple): RealValue = new VReal(name, g, q, rootVia)
    override def real(value1: RealValue, value2: RealValue, valuesN: RealValue*): RealStrm = new VRealStrm(values = MultiSet(value1 +: (value2 +: valuesN)))
    override def str(g: String, name: String = Tokens.str, q: IntQ, via: ViaTuple): StrValue = new VStr(name, g, q, rootVia)
    override def str(value1: StrValue, value2: StrValue, valuesN: StrValue*): StrStrm = new VStrStrm(values = MultiSet(value1 +: (value2 +: valuesN)))
    //
    override def strm[O <: Obj]: OStrm[O] = new VObjStrm(values = List.empty).asInstanceOf[OStrm[O]]
    override def strm[O <: Obj](values: Seq[O]): OStrm[O] = {
      if (values.map(x => baseName(x)).toSet.size > 1)
        return new VObjStrm(values = MultiSet[ObjValue](values.asInstanceOf[Seq[ObjValue]])).asInstanceOf[OStrm[O]]
      values.headOption.map(x => {
        val headName: String = x.name
        x match {
          case _: Bool => new VBoolStrm(name = headName, values = MultiSet[BoolValue](values.asInstanceOf[Seq[BoolValue]]))
          case _: Int => new VIntStrm(name = headName, values = MultiSet(values.asInstanceOf[Seq[IntValue]]))
          case _: Real => new VRealStrm(name = headName, values = MultiSet(values.asInstanceOf[Seq[RealValue]]))
          case _: Str => new VStrStrm(name = headName, values = MultiSet(values.asInstanceOf[Seq[StrValue]]))
          case _: Rec[Obj, Obj] => new VRecStrm[Obj, Obj](name = headName, values = MultiSet(values.asInstanceOf[Seq[RecValue[Obj, Obj]]]))
          case _: LstValue[Obj] => new VLstStrm[Obj](name = headName, values = MultiSet(values.asInstanceOf[Seq[LstValue[Obj]]]))
          // TODO: temporary below
          case y: TLst[_] => new VLstStrm[Obj](name = headName, values = MultiSet(values.map(x => new VLst(g = (y.gsep, if (!x.alive || !x.isInstanceOf[Lst[Obj]]) Nil else x.asInstanceOf[Lst[Obj]].glist))).asInstanceOf[Seq[LstValue[Obj]]]))
          case _ => new VObjStrm(values = List.empty)
        }
      }).getOrElse(new VObjStrm(values = List.empty)).asInstanceOf[O]
    }.asInstanceOf[OStrm[O]]
  }
}
