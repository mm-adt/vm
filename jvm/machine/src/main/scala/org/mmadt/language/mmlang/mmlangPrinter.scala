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
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.`type`.{TLst, TRec}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object mmlangPrinter {

  def qString(x: IntQ): String = x match {
    case `qOne` => empty
    case `qZero` => QZERO
    case `qMark` => s"${LCURL}${Tokens.q_mark}${RCURL}"
    case `qPlus` => s"${LCURL}${Tokens.q_plus}${RCURL}"
    case `qStar` => s"${LCURL}${Tokens.q_star}${RCURL}"
    case (x, y) if x == y => s"${LCURL}${x}${RCURL}"
    case (x, y) if y == int(Long.MaxValue) => "{" + x + ",}"
    case (x, y) if x == int(Long.MinValue) => "{," + y + "}"
    case x if null == x => Tokens.empty
    case _ => "{" + x._1.g + "," + x._2.g + "}"
  }


  def recString(rec: Rec[_, _]): String = {
    if (rec.isInstanceOf[Strm[_]]) return strmString(rec.asInstanceOf[Strm[Obj]])
    else if (rec.root) mapString(rec, map = rec.gmap, sep = rec.gsep, empty = EMPTYREC)
    if (rec.isInstanceOf[RecType[Obj,Obj]])
      if (Tokens.named(rec.name)) rec.name
      else mapString(rec, map = rec.gmap, sep = rec.gsep, empty = EMPTYREC)
    else
      mapString(rec, map = rec.gmap, sep = rec.gsep, empty = EMPTYREC)
  }

  private def aliveString(obj: Any): String = if (obj.asInstanceOf[Obj].alive) obj.toString else Tokens.empty
  private def mapString(rec: Rec[_, _], map: collection.Map[_, _], sep: String = COMMA, empty: String = Tokens.empty): String = {
    if (map.isEmpty)
      empty else
      map.foldLeft(if (rec.isInstanceOf[TRec[_, _]]) LROUND else LROUND)((string, kv) => string + (aliveString(kv._1) + Tokens.-> + aliveString(kv._2) + sep)).dropRight(1) +
        (if (rec.isInstanceOf[TRec[_, _]]) RROUND else RROUND)
  }
  private def listString(lst: Lst[_]): String = {
    if (lst.isInstanceOf[Strm[_]]) return strmString(lst.asInstanceOf[Strm[Obj]])
    if (lst.glist.isEmpty)
      LROUND + Tokens.space + RROUND
    else
      lst.glist.foldLeft(if (lst.isInstanceOf[TLst[_]]) LROUND else LROUND)((string, element) => string + aliveString(element) + lst.gsep).dropRight(1) +
        (if (lst.isInstanceOf[TLst[_]]) RROUND else RROUND)
  }

  def strmString(strm: Strm[_]): String = if (!strm.alive) zeroObj.toString else strm.name + qString(strm.q)
  def typeString(atype: Type[_]): String = {
    val range = (atype match {
      case arec: Rec[_, _] => recString(arec)
      case alst: Lst[_] => listString(alst)
      case _ => atype.name
    }) + qString(atype.q)
    val domain = if (atype.root) Tokens.empty else {
      (atype.domain match {
        case arec: Rec[_, _] => recString(arec)
        case alst: Lst[_] => listString(alst)
        case btype: Type[_] => btype.name
      }) + qString(atype.domain.q)
    }
    (if (domain.equals(EMPTY) || range.equals(domain)) range else (range + LDARROW + (if (atype.domain.alive && !atype.domain.equals(obj.q(qStar))) domain else Tokens.empty))) + atype.trace.map(_._2.toString()).fold(Tokens.empty)((a, b) => a + b)
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
      case Tokens.define => Tokens.empty
      case Tokens.to => LANGLE + inst.arg0[StrValue].g + RANGLE
      case Tokens.from => LANGLE + PERIOD + inst.arg0[StrValue].g + RANGLE
      case Tokens.split => Tokens.split_op + inst.arg0[Poly[_]].toString
      case Tokens.merge => Tokens.merge_op
      case _ => inst.args match {
        case Nil => LBRACKET + inst.op + RBRACKET
        case args: List[Obj] => LBRACKET + inst.op + COMMA + args.map(arg => arg.toString + COMMA).fold(EMPTY)((a, b) => a + b).dropRight(1) + RBRACKET
      }
    }) + qString(inst.q)
  }
}
