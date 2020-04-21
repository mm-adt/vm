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
import org.mmadt.language.obj.branch.{Brch, Coprod, Prod}
import org.mmadt.language.obj.op.initial.{IntOp, StrOp}
import org.mmadt.language.obj.value._
import org.mmadt.language.obj.value.strm._
import org.mmadt.language.obj.{ViaTuple, branch, _}
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.`type`._
import org.mmadt.storage.obj.branch.{OCoprod, OProd}
import org.mmadt.storage.obj.value._
import org.mmadt.storage.obj.value.strm._


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait StorageFactory {
  /////////TYPES/////////
  lazy val obj: ObjType = tobj()
  lazy val bool: BoolType = tbool()
  lazy val int: IntType = tint()
  lazy val real: RealType = treal()
  lazy val str: StrType = tstr()
  def rec[A <: Obj, B <: Obj]: RecType[A, B] = trec(value = Map.empty[A, B])
  def lst[A <: Obj]: LstType[A] = tlst()
  def prod[A <: Obj](values: A*): branch.Prod[A] = new OProd[A](value = values.toList)
  def coprod[A <: Obj](values: A*): branch.Coprod[A] = new OCoprod[A](value = values.toList)
  //
  def tobj(name: String = Tokens.obj, q: IntQ = qOne, via: ViaTuple = base()): ObjType
  def tbool(name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = base()): BoolType
  def tint(name: String = Tokens.int, q: IntQ = qOne, via: ViaTuple = base(IntOp())): IntType
  def treal(name: String = Tokens.real, q: IntQ = qOne, via: ViaTuple = base()): RealType
  def tstr(name: String = Tokens.str, q: IntQ = qOne, via: ViaTuple = base(StrOp())): StrType
  def trec[A <: Obj, B <: Obj](name: String = Tokens.rec, value: collection.Map[A, B], q: IntQ = qOne, via: ViaTuple = base()): RecType[A, B]
  def trec[A <: Obj, B <: Obj](value: (A, B), values: (A, B)*): RecType[A, B] = trec[A, B](value = (value +: values).toMap)
  def tlst[A <: Obj](name: String = Tokens.lst, value: List[A] = List.empty[A], q: IntQ = qOne, via: ViaTuple = base()): LstType[A]
  def tlst[A <: Obj](value: A, values: A*): LstType[A] = tlst[A](value = (value +: values).toList)
  /////////VALUES/////////
  def obj(value: Any): ObjValue
  def bool(value: Boolean): BoolValue = vbool(value = value)
  def int(value: Long): IntValue = vint(value = value)
  def real(value: Double): RealValue = vreal(value = value)
  def str(value: String): StrValue = vstr(value = value)
  def vrec[A <: Value[Obj], B <: Value[Obj]](_value: collection.Map[A, B]): RecValue[A, B] = vrec(value = _value)
  def vrec[A <: Value[Obj], B <: Value[Obj]](value: (A, B), values: (A, B)*): RecValue[A, B] = vrec(value = (value +: values).toMap)
  def vrec[A <: Value[Obj], B <: Value[Obj]](value1: RecValue[A, B], value2: RecValue[A, B], valuesN: RecValue[A, B]*): RecStrm[A, B] = vrec((List(value1, value2) ++ valuesN).iterator)
  def vlst[A <: Value[Obj]](): LstValue[A] = vlst(value = List.empty[A])
  def vlst[A <: Value[Obj]](value: A, values: A*): LstValue[A] = vlst(value = (value +: values).toList)
  //
  def bool(value1: BoolValue, value2: BoolValue, valuesN: BoolValue*): BoolStrm
  def int(value1: IntValue, value2: IntValue, valuesN: IntValue*): IntStrm
  def real(value1: RealValue, value2: RealValue, valuesN: RealValue*): RealStrm
  def str(value1: StrValue, value2: StrValue, valuesN: StrValue*): StrStrm
  def vrec[A <: Value[Obj], B <: Value[Obj]](value: Iterator[RecValue[A, B]]): RecStrm[A, B]
  def vbool(name: String = Tokens.bool, value: Boolean, q: IntQ = qOne, via: ViaTuple = base()): BoolValue
  def vint(name: String = Tokens.int, value: Long, q: IntQ = qOne, via: ViaTuple = base()): IntValue
  def vreal(name: String = Tokens.real, value: Double, q: IntQ = qOne, via: ViaTuple = base()): RealValue
  def vstr(name: String = Tokens.str, value: String, q: IntQ = qOne, via: ViaTuple = base()): StrValue
  def vlst[A <: Value[Obj]](name: String = Tokens.lst, value: List[A] = List.empty[A], q: IntQ = qOne, via: ViaTuple = base()): LstValue[A]
  def vrec[A <: Value[Obj], B <: Value[Obj]](name: String = Tokens.rec, value: collection.Map[A, B], q: IntQ = qOne, via: ViaTuple = base()): RecValue[A, B]
  //
  def strm[O <: Obj](values:O*): OStrm[O] = strm[O](values.toList.iterator)
  def strm[O <: Obj](itty: Iterator[O]): OStrm[O]
  def strm[O <: Obj]: OStrm[O]
}

object StorageFactory {
  ///////PROVIDERS///////
  val providers: ServiceLoader[StorageProvider] = ServiceLoader.load(classOf[StorageProvider])
  /////////TYPES/////////
  lazy val obj: ObjType = tobj()
  lazy val bool: BoolType = tbool()
  lazy val int: IntType = tint()
  lazy val real: RealType = treal()
  lazy val str: StrType = tstr()
  def rec[A <: Obj, B <: Obj]: RecType[A, B] = trec(value = Map.empty[A, B])
  def lst[A <: Obj]: LstType[A] = tlst(value = List.empty[A])
  def prod[A <: Obj](values: A*): Prod[A] = new OProd[A](value = values.toList)
  def coprod[A <: Obj](values: A*): Coprod[A] = new OCoprod[A](value = values.toList)
  //
  def tobj(name: String = Tokens.obj, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): ObjType = f.tobj(name, q, via)
  def tbool(name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): BoolType = f.tbool(name, q, via)
  def tint(name: String = Tokens.int, q: IntQ = qOne, via: ViaTuple = (null, IntOp()))(implicit f: StorageFactory): IntType = f.tint(name, q, via)
  def treal(name: String = Tokens.real, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): RealType = f.treal(name, q, via)
  def tstr(name: String = Tokens.str, q: IntQ = qOne, via: ViaTuple = (null, StrOp()))(implicit f: StorageFactory): StrType = f.tstr(name, q, via)
  def trec[A <: Obj, B <: Obj](name: String = Tokens.rec, value: collection.Map[A, B], q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): RecType[A, B] = f.trec(name, value, q, via)
  def trec[A <: Obj, B <: Obj](value: (A, B), values: (A, B)*)(implicit f: StorageFactory): RecType[A, B] = f.trec(value, values: _*)
  def tlst[A <: Obj](name: String = Tokens.lst, value: List[A] = List.empty[A], q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): LstType[A] = f.tlst(name, value, q, via)
  def tlst[A <: Obj](value: A, values: A*)(implicit f: StorageFactory): LstType[A] = f.tlst(value, values: _*)
  /////////VALUES/////////
  def obj(value: Any)(implicit f: StorageFactory): ObjValue = f.obj(value)
  def bool(value: Boolean)(implicit f: StorageFactory): BoolValue = f.bool(value)
  def bool(value1: BoolValue, value2: BoolValue, valuesN: BoolValue*)(implicit f: StorageFactory): BoolStrm = f.bool(value1, value2, valuesN: _*)
  def int(value: Long)(implicit f: StorageFactory): IntValue = f.int(value)
  def int(value1: IntValue, value2: IntValue, valuesN: IntValue*)(implicit f: StorageFactory): IntStrm = f.int(value1, value2, valuesN: _*)
  def real(value: Double)(implicit f: StorageFactory): RealValue = f.vreal(Tokens.real, value, qOne)
  def real(value: Float)(implicit f: StorageFactory): RealValue = f.vreal(Tokens.real, value.doubleValue(), qOne)
  def real(value1: RealValue, value2: RealValue, valuesN: RealValue*)(implicit f: StorageFactory): RealStrm = f.real(value1, value2, valuesN: _*)
  def str(value: String)(implicit f: StorageFactory): StrValue = f.vstr(Tokens.str, value, qOne)
  def str(value1: StrValue, value2: StrValue, valuesN: StrValue*)(implicit f: StorageFactory): StrStrm = f.str(value1, value2, valuesN: _*)
  def vrec[A <: Value[Obj], B <: Value[Obj]](value: collection.Map[A, B])(implicit f: StorageFactory): RecValue[A, B] = f.vrec(Tokens.rec, value, qOne)
  def vrec[A <: Value[Obj], B <: Value[Obj]](value: (A, B), values: (A, B)*)(implicit f: StorageFactory): RecValue[A, B] = f.vrec(value, values: _*)
  def vrec[A <: Value[Obj], B <: Value[Obj]](value1: RecValue[A, B], value2: RecValue[A, B], valuesN: RecValue[A, B]*)(implicit f: StorageFactory): RecStrm[A, B] = f.vrec(value1, value2, valuesN: _*)
  def vrec[A <: Value[Obj], B <: Value[Obj]](value: Iterator[RecValue[A, B]])(implicit f: StorageFactory): RecStrm[A, B] = f.vrec(value)
  def vlst[A <: Value[Obj]]()(implicit f: StorageFactory): LstValue[A] = f.vlst(value = List.empty[A])
  def vlst[A <: Value[Obj]](name: String = Tokens.lst, value: List[A], q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): LstValue[A] = f.vlst(name, value, q, via)
  def vlst[A <: Value[Obj]](value: A, values: A*)(implicit f: StorageFactory): LstValue[A] = f.vlst(value = (value +: values).toList)
  //
  def vbool(name: String = Tokens.bool, value: Boolean, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): BoolValue = f.vbool(name, value, q, via)
  def vint(name: String = Tokens.int, value: Long, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): IntValue = f.vint(name, value, q, via)
  def vreal(name: String = Tokens.real, value: Double, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): RealValue = f.vreal(name, value, q, via)
  def vstr(name: String = Tokens.str, value: String, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): StrValue = f.vstr(name, value, q, via)
  def vrec[A <: Value[Obj], B <: Value[Obj]](name: String = Tokens.rec, value: collection.Map[A, B], q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): RecValue[A, B] = f.vrec(name, value, q, via)
  def strm[O <: Obj](values:O*)(implicit f: StorageFactory): OStrm[O] = f.strm[O](values.toList.iterator)
  def strm[O <: Obj](itty: Iterator[O])(implicit f: StorageFactory): OStrm[O] = f.strm[O](itty)
  def strm[O <: Obj](implicit f: StorageFactory): OStrm[O] = f.strm[O]
  /////////CONSTANTS//////
  lazy val btrue: BoolValue = bool(value = true)
  lazy val bfalse: BoolValue = bool(value = false)
  lazy val qZero: (IntValue, IntValue) = (int(0), int(0))
  lazy val qOne: (IntValue, IntValue) = (int(1), int(1))
  lazy val qMark: (IntValue, IntValue) = (int(0), int(1))
  lazy val qPlus: (IntValue, IntValue) = (int(1), int(Long.MaxValue))
  lazy val qStar: (IntValue, IntValue) = (int(0), int(Long.MaxValue))
  lazy val * : (IntValue, IntValue) = qStar
  lazy val ? : (IntValue, IntValue) = qMark
  lazy val + : (IntValue, IntValue) = qPlus
  def asType[O <: Obj](obj: O): OType[O] = (obj match {
    case branching: Brch[_] if branching.isValue => branching.clone(value = branching.value.map(x => asType[Obj](x)))
    case atype: Type[_] => atype
    case _: IntValue | _: IntStrm => tint(name = obj.name, q = obj.q)
    case _: RealValue | _: RealStrm => treal(name = obj.name, q = obj.q)
    case _: StrValue | _: StrStrm => tstr(name = obj.name, q = obj.q)
    case _: BoolValue | _: BoolStrm => tbool(name = obj.name, q = obj.q)
    case _: RecStrm[_, _] => trec(name = obj.name, value = Map.empty, q = obj.q)
    case recval: RecValue[_, _] => trec(name = recval.name, value = recval.value, q = recval.q)
    case lstval: LstValue[_] => tlst(name = lstval.name, value = lstval.value, q = lstval.q)

  }).asInstanceOf[OType[O]]
  def isSymbol[O <: Obj](obj: O): Boolean = obj match {
    case _: Value[_] => false
    case atype: Type[_] => atype.root && atype.getClass.equals(tobj().getClass) && !atype.name.equals(Tokens.obj)
  }
  implicit val mmstoreFactory: StorageFactory = new StorageFactory {
    /////////TYPES/////////
    override def tobj(name: String = Tokens.obj, q: IntQ = qOne, via: ViaTuple = base()): ObjType = new TObj(name, q, via)
    override def tbool(name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = base()): BoolType = new TBool(name, q, via)
    override def tint(name: String = Tokens.int, q: IntQ = qOne, via: ViaTuple = base(IntOp())): IntType = new TInt(name, q, via)
    override def treal(name: String = Tokens.real, q: IntQ = qOne, via: ViaTuple = base()): RealType = new TReal(name, q, via)
    override def tstr(name: String = Tokens.str, q: IntQ = qOne, via: ViaTuple = base(StrOp())): StrType = new TStr(name, q, via)
    override def trec[A <: Obj, B <: Obj](name: String = Tokens.rec, value: collection.Map[A, B], q: IntQ = qOne, via: ViaTuple = base()): RecType[A, B] = new TRec[A, B](name, value, q, via)
    override def tlst[A <: Obj](name: String = Tokens.lst, value: List[A], q: IntQ = qOne, via: ViaTuple = base()): LstType[A] = new TLst[A](name, value, q, via)
    /////////VALUES/////////
    override def int(value: Long): IntValue = new VInt(value = value) // NECESSARY FOR Q PRELOAD
    override def obj(value: Any): ObjValue = new VObj(value = value)
    override def vbool(name: String, value: Boolean, q: IntQ, via: ViaTuple): BoolValue = new VBool(name, value, q, via)
    override def bool(value1: BoolValue, value2: BoolValue, valuesN: BoolValue*): BoolStrm = new VBoolStrm(value1 +: (value2 +: valuesN))
    override def vint(name: String, value: Long, q: IntQ, via: ViaTuple): IntValue = new VInt(name, value, q, via)
    override def int(value1: IntValue, value2: IntValue, valuesN: IntValue*): IntStrm = new VIntStrm(value1 +: (value2 +: valuesN))
    override def vreal(name: String, value: Double, q: IntQ, via: ViaTuple): RealValue = new VReal(name, value, q, base())
    override def real(value1: RealValue, value2: RealValue, valuesN: RealValue*): RealStrm = new VRealStrm(value1 +: (value2 +: valuesN))
    override def vstr(name: String, value: String, q: IntQ, via: ViaTuple): StrValue = new VStr(name, value, q, base())
    override def str(value1: StrValue, value2: StrValue, valuesN: StrValue*): StrStrm = new VStrStrm(value1 +: (value2 +: valuesN))
    override def vrec[A <: Value[Obj], B <: Value[Obj]](name: String, value: collection.Map[A, B], q: IntQ, via: ViaTuple): RecValue[A, B] = new VRec[A, B](name, value, q, via)
    override def vrec[A <: Value[Obj], B <: Value[Obj]](value: Iterator[RecValue[A, B]]): RecStrm[A, B] = new VRecStrm(value.toSeq)
    override def vlst[A <: Value[Obj]](name: String, value: List[A], q: IntQ, via: ViaTuple): LstValue[A] = new VLst[A](name, value, q, via)
    //
    override def strm[O <: Obj]: OStrm[O] = VEmptyStrm.empty[O]
    override def strm[O <: Obj](itty: Iterator[O]): OStrm[O] = {
      if (itty.hasNext) {
        val first: O = itty.next()
        if (itty.hasNext) {
          val second: O = itty.next()
          (first match {
            case boolValue: BoolValue => bool(value1 = boolValue, value2 = second.asInstanceOf[BoolValue], valuesN = itty.asInstanceOf[Iterator[BoolValue]].toSeq: _*)
            case intValue: IntValue => int(value1 = intValue, value2 = second.asInstanceOf[IntValue], valuesN = itty.asInstanceOf[Iterator[IntValue]].toSeq: _*)
            case realValue: RealValue => real(value1 = realValue, value2 = second.asInstanceOf[RealValue], valuesN = itty.asInstanceOf[Iterator[RealValue]].toSeq: _*)
            case strValue: StrValue => str(value1 = strValue, value2 = second.asInstanceOf[StrValue], valuesN = itty.asInstanceOf[Iterator[StrValue]].toList: _*)
            case recValue: RecValue[_, _] => vrec(value1 = recValue.asInstanceOf[ORecValue], value2 = second.asInstanceOf[ORecValue], valuesN = itty.asInstanceOf[Iterator[ORecValue]].toList: _*)
            case brchValue: Brch[_] => new VBrchStrm[Obj]((List(first, second) ++ itty.toList).asInstanceOf[List[Brch[Obj]]])
          }).asInstanceOf[OStrm[O]]
        } else VSingletonStrm.single(first)
      } else {
        strm
      }
    }
  }
}
