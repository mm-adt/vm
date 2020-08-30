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

package org.mmadt.language.mmlang

import org.mmadt.language.Tokens
import org.mmadt.language.Tokens.{LBRACKET, int => _, obj => _, _}
import org.mmadt.language.obj.Obj.IntQ
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{LstType, RecType, Type}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object mmlangPrinter {

  private def aliveString(obj: Any): String = if (obj.asInstanceOf[Obj].alive) obj.toString else "{0}"

  def qString(x: IntQ): String = x match {
    case `qOne` => blank
    case `qZero` => QZERO
    case `qMark` => s"${LCURL}${Tokens.q_mark}${RCURL}"
    case `qPlus` => s"${LCURL}${Tokens.q_plus}${RCURL}"
    case `qStar` => s"${LCURL}${Tokens.q_star}${RCURL}"
    case (x, y) if x == y => s"${LCURL}${x}${RCURL}"
    case (x, y) if y == int(Long.MaxValue) => "{" + x + ",}"
    case (x, y) if x == int(Long.MinValue) => "{," + y + "}"
    case x if null == x => Tokens.blank
    case _ => "{" + x._1.g + "," + x._2.g + "}"
  }

  private def recString(arec: Rec[_, _]): String = arec match {
    case _: Strm[_] => strmString(arec.asInstanceOf[Strm[Obj]])
    case _: RecType[_, _] if Tokens.named(arec.name) => arec.name
    case _ if arec.ctype => Tokens.rec
    case _ if arec.isEmpty => EMPTYREC
    case _ => arec.gmap.foldLeft(LROUND)((string, kv) => string + (aliveString(kv._1) + Tokens.-> + aliveString(kv._2) + arec.gsep)).dropRight(1) + RROUND
  }

  private def listString(alst: Lst[_]): String = alst match {
    case _: Strm[_] => strmString(alst.asInstanceOf[Strm[Obj]])
    case _: LstType[_] if Tokens.named(alst.name) => alst.name
    case _ if alst.ctype => Tokens.lst
    case _ if alst.isEmpty => EMPTYLST
    case _ => alst.glist.foldLeft(LROUND)((string, element) => string + aliveString(element) + alst.gsep).dropRight(1) + RROUND
  }

  def strmString(strm: Strm[_]): String = if (!strm.alive) zeroObj.toString else strm.values.foldLeft(LBRACKET)((a, b) => a + b.toString + COMMA).dropRight(1) + RBRACKET

  def typeString(atype: Type[_]): String = {
    val range = (atype match {
      case arec: Rec[_, _] => recString(arec)
      case alst: Lst[_] => listString(alst)
      case atype: Type[_] => atype.name
    }) + qString(atype.q)
    val domain = if (atype.root) EMPTY else {
      (atype.domainObj match {
        case arec: Rec[_, _] => recString(arec)
        case alst: Lst[_] => listString(alst)
        case atype: Type[_] => atype.name
        case avalue: Value[_] => avalue.toString
      }) + qString(atype.domain.q)
    }
    (if (domain.equals(EMPTY) || range.equals(domain)) range else range + LDARROW + domain) +
      atype.trace.map(_._2.toString()).fold(EMPTY)((a, b) => a + b)
  }

  def valueString(avalue: Value[_]): String = {
    val named = Tokens.named(avalue.name)
    (if (named) avalue.name + COLON else EMPTY) + (
      avalue match {
        case arec: Rec[_, _] => recString(arec)
        case alst: Lst[_] => listString(alst)
        case astr: StrValue => SQUOTE + astr.g + SQUOTE
        case _ => avalue.g
      }) + qString(avalue.q)
  }

  def instString(inst: Inst[_, _]): String = {
    (inst.op match {
      case Tokens.model | Tokens.define | Tokens.noop => Tokens.blank
      case Tokens.to => LANGLE + inst.arg0[StrValue].g + RANGLE
      case Tokens.from => LANGLE + PERIOD + inst.arg0[StrValue].g + RANGLE
      case Tokens.branch => LBRACKET +
        Some[Obj](inst.arg0[Obj])
          .filter(x => x.isInstanceOf[Poly[Obj]])
          .map(x => x.asInstanceOf[Poly[Obj]])
          .filter(x => !x.isEmpty)
          .map(x => x.hardQ(1).toString.drop(1).dropRight(1))
          .getOrElse(inst.arg0[Obj]) + RBRACKET
      case Tokens.split => Tokens.split_op + inst.arg0[Poly[_]].toString
      case Tokens.merge => Tokens.merge_op
      case _ => inst.args match {
        case Nil => LBRACKET + inst.op + RBRACKET
        case args: List[Obj] => LBRACKET + inst.op + COMMA + args.map(arg => arg.toString + COMMA).fold(EMPTY)((a, b) => a + b).dropRight(1) + RBRACKET
      }
    }) + qString(inst.q)
  }
}
