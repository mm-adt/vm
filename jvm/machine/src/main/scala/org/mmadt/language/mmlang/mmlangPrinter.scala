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
import org.mmadt.language.Tokens._
import org.mmadt.language.obj.Obj.IntQ
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{LstType, RecType, Type}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.storage.StorageFactory
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object mmlangPrinter {

  private val prettyPrint:Boolean = true
  private val tab:String = "\n   "

  private def aliveString(obj:Any):String = if (obj.asInstanceOf[Obj].alive) obj.toString else "{0}"
  private def typeName(aobj:Obj):String = if (Tokens.named(aobj.name)) aobj.name + COLON else EMPTY


  private def recString(arec:Rec[_, _]):String = {
    if (arec.ctype) return arec.name
    typeName(arec) +
      (arec match {
        case _:Strm[_] => strmString(arec.asInstanceOf[Strm[Obj]])
        case _:RecType[_, _] if Tokens.named(arec.name) => return arec.name
        case _ if arec.isEmpty => EMPTYREC
        case _ =>
          val recString:String = arec.gmap.foldLeft(LROUND)((string, kv) => string + (aliveString(kv._1) + Tokens.-> + aliveString(kv._2) + arec.gsep)).dropRight(1) + RROUND
          if (prettyPrint && (arec.isInstanceOf[Type[_]] && !arec.trace.identity)) recString + tab else recString
      })
  }

  private def listString(alst:Lst[_]):String = {
    if (alst.ctype) return alst.name
    typeName(alst) +
      (alst match {
        case _:Strm[_] => strmString(alst.asInstanceOf[Strm[Obj]])
        case _:LstType[_] if Tokens.named(alst.name) => return alst.name
        case _ if alst.isEmpty => EMPTYLST
        case _ =>
          val lstString:String = alst.glist.foldLeft(LROUND)((string, element) => string + aliveString(element) + alst.gsep).dropRight(1) + RROUND
          if (prettyPrint && (alst.isInstanceOf[Type[_]] && !alst.trace.identity)) lstString + tab else lstString
      })
  }


  def qString(x:IntQ):String = x match {
    case `qOne` => blank
    case `qZero` => QZERO
    case `qMark` => s"${LCURL}${Tokens.q_mark}${RCURL}"
    case `qPlus` => s"${LCURL}${Tokens.q_plus}${RCURL}"
    case `qStar` => s"${LCURL}${Tokens.q_star}${RCURL}"
    case (x, y) if x == y => s"${LCURL}${x}${RCURL}"
    case (x, y) if y == StorageFactory.int(Long.MaxValue) => "{" + x + ",}"
    case (x, y) if x == StorageFactory.int(Long.MinValue) => "{," + y + "}"
    case x if null == x => Tokens.blank
    case _ => "{" + x._1.g + "," + x._2.g + "}"
  }

  def strmString(strm:Strm[_]):String = if (!strm.alive) zeroObj.toString else strm.values.foldLeft(LBRACKET)((a, b) => a + b.toString + COMMA).dropRight(1) + RBRACKET

  def typeString(atype:Type[_]):String = {
    val range = (atype match {
      case arec:Rec[_, _] => recString(arec)
      case alst:Lst[_] => listString(alst)
      case atype:Type[_] => atype.name
    }) + qString(atype.q)
    val domain = if (atype.root) EMPTY else {
      (atype.domainObj match {
        case arec:Rec[_, _] => recString(arec)
        case alst:Lst[_] => listString(alst)
        case atype:Type[_] => atype.name
        case avalue:Value[_] => avalue.hardQ(qOne).toString
      }) + qString(atype.domain.q)
    }

    (if (domain.equals(EMPTY) || range.equals(domain)) range else range + LDARROW + domain) +
      atype.trace.map(_._2.toString()).fold(EMPTY)((a, b) => a + b)
  }

  def valueString(avalue:Value[_]):String = (avalue match {
    case arec:Rec[_, _] => recString(arec)
    case alst:Lst[_] => listString(alst)
    case astr:StrValue => typeName(astr) + SQUOTE + astr.g + SQUOTE
    case _ => typeName(avalue) + avalue.g
  }) + qString(avalue.q)

  def instString(inst:Inst[_, _]):String = {
    (inst.op match {
      case Tokens.model | Tokens.noop => Tokens.blank
      case Tokens.to => LANGLE + inst.arg0[Obj].name + RANGLE
      case Tokens.from => LANGLE + PERIOD + inst.arg0[StrValue].g + RANGLE
      case Tokens.branch => LBRACKET +
        Some[Obj](inst.arg0[Obj])
          .filter(x => x.isInstanceOf[Poly[Obj]])
          .map(x => x.asInstanceOf[Poly[Obj]])
          .filter(x => !x.isEmpty)
          .map(x => x.hardQ(1).toString.drop(1).dropRight(1))
          .getOrElse(inst.arg0[Obj]) + RBRACKET
      case _ => inst.args match {
        case Nil => LBRACKET + inst.op + RBRACKET
        case args:List[Obj] => LBRACKET + inst.op + COMMA + args.map(arg => arg.toString + COMMA).fold(EMPTY)((a, b) => a + b).dropRight(1) + RBRACKET
      }
    }) + qString(inst.q)
  }
}
