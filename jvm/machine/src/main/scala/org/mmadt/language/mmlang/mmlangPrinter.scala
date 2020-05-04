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

package org.mmadt.language.mmlang

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{RecValue, StrValue, Value}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object mmlangPrinter {

  def qString(x: IntQ): String = x match {
    case `qOne` => Tokens.empty
    case `qZero` => QZERO
    case `qMark` => s"${LCURL}${Tokens.q_mark}${RCURL}"
    case `qPlus` => s"${LCURL}${Tokens.q_plus}${RCURL}"
    case `qStar` => s"${LCURL}${Tokens.q_star}${RCURL}"
    case (x, y) if x == y => s"${LCURL}${x}${RCURL}"
    case (x, y) if y == int(Long.MaxValue) => "{" + x + ",}"
    case (x, y) if x == int(Long.MinValue) => "{," + y + "}"
    case x if null == x => Tokens.empty
    case _ => "{" + x._1.ground + "," + x._2.ground + "}"
  }

  def strmString(strm: Strm[Obj]): String = strm.values.foldLeft(Tokens.empty)((a, b) => a + b + COMMA).dropRight(1)

  def polyString(poly: Poly[_]): String = {
    if (poly.root) polyList(poly)
    if (!poly.isValue)
      typeString(poly.asInstanceOf[Type[Obj]])
    else
      polyList(poly) + qString(poly.q)
  }

  private def mapString(map: collection.Map[_, _], sep: String = COMMA, empty: String = Tokens.empty): String = if (map.isEmpty) empty else map.foldLeft(LBRACKET)((string, kv) => string + (kv._1 + COLON + kv._2 + sep)).dropRight(1) + RBRACKET
  private def polyList(poly: Poly[_]): String = {
    if (poly.isInstanceOf[Strm[_]]) return strmString(poly.asInstanceOf[Strm[Obj]])
    poly.groundList.zip(if (poly.hasKeys) poly.groundKeys else List.fill(poly.groundList.length)(() => "")).foldLeft(LBRACKET)((a, b) => a + Option(b).filter(x => x._1.asInstanceOf[Obj].alive()).map(x => (if (poly.hasKeys) (x._2 + Tokens.:->) else Tokens.empty) + x._1).getOrElse(Tokens.empty) + poly.ground._1).dropRight(1) + RBRACKET
  }

  def typeString(atype: Type[Obj]): String = {
    val range = (atype match {
      case arec: RecType[_, _] => if (!atype.root && Tokens.named(arec.name)) arec.name else arec.name + mapString(arec.ground)
      case apoly: Poly[_] => apoly.name + polyList(apoly)
      case _ => atype.name
    }) + qString(atype.q)
    val domain = if (atype.root) Tokens.empty else {
      (atype.domain() match {
        case arec: RecType[_, _] => if (!atype.root && Tokens.named(arec.name)) arec.name else arec.name + mapString(arec.ground)
        case apoly: Poly[_] => apoly.name + polyList(apoly)
        case btype: Type[_] => btype.name
      }) + qString(atype.domain().q)
    }
    (if (domain.equals(EMPTY) || range.equals(domain)) range else (range + LDARROW + (if (atype.domain().alive() && !atype.domain().equals(obj.q(qStar))) domain else Tokens.empty))) + atype.trace.map(_._2.toString()).fold(Tokens.empty)((a, b) => a + b)
  }

  def valueString(avalue: Value[Obj]): String = {
    val named = Tokens.named(avalue.name)
    (if (named) avalue.name + COLON else EMPTY) + (
      avalue match {
        case arec: RecValue[_, _] => mapString(arec.ground, empty = EMPTYREC)
        case astr: StrValue => SQUOTE + astr.ground + SQUOTE
        case _ => avalue.ground
      }) + qString(avalue.q)
  }

  def instString(inst: Inst[_, _]): String = {
    (inst.op() match {
      case Tokens.to => LANGLE + inst.arg0[StrValue]().ground + RANGLE
      case Tokens.from => LANGLE + PERIOD + inst.arg0[StrValue]().ground + RANGLE
      case Tokens.choose => LBRACKET + Tokens.choose + COMMA + mapString(inst.arg0[RecType[Obj, Obj]]().ground, PIPE) + RBRACKET
      case Tokens.branch => LBRACKET + Tokens.branch + COMMA + mapString(inst.arg0[RecType[Obj, Obj]]().ground, AMPERSAND) + RBRACKET
      case Tokens.split => Tokens.split_op + polyList(inst.arg0[Poly[_]]())
      case Tokens.merge => Tokens.merge_op
      case _ => inst.args() match {
        case Nil => LBRACKET + inst.op() + RBRACKET
        case args: List[Obj] => LBRACKET + inst.op() + COMMA + args.map(arg => arg.toString + COMMA).fold(EMPTY)((a, b) => a + b).dropRight(1) + RBRACKET
      }
    }) + qString(inst.q)
  }
}
