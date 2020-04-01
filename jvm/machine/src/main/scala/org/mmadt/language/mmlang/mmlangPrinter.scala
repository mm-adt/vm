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
import org.mmadt.language.obj.`type`.{RecType,Type}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{ObjValue,RecValue,StrValue,Value}
import org.mmadt.language.obj.{Inst,IntQ,Obj}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object mmlangPrinter {

  def qString(x:IntQ):String = x match {
    case `qOne` => Tokens.empty
    case `qZero` => QZERO
    case `qMark` => s"${LCURL}${Tokens.q_mark}${RCURL}"
    case `qPlus` => s"${LCURL}${Tokens.q_plus}${RCURL}"
    case `qStar` => s"${LCURL}${Tokens.q_star}${RCURL}"
    case (x,y) if x == y => s"${LCURL}${x}${RCURL}"
    case (x,y) if y == int(Long.MaxValue) => "{" + x + ",}"
    case (x,y) if x == int(Long.MinValue) => "{," + y + "}"
    case _ => "{" + x._1.value + "," + x._2.value + "}"
  }

  def strmString(strm:Strm[Obj]):String = strm.value.foldLeft(Tokens.empty)((a,b) => a + b + COMMA).dropRight(1)

  def traverserString(trav:Traverser[_]):String ={
    "[" + trav.obj() + "|" + trav.state.foldLeft(EMPTY)((string,x) => string + x._1.toString.replace("'","") + "->" + x._2 + ",").dropRight(1) + "]"
  }

  private def mapString(map:Map[_,_],sep:String = COMMA):String = if (map.isEmpty) EMPTYREC else map.foldLeft(LBRACKET)((string,kv) => string + (kv._1 + COLON + kv._2 + sep)).dropRight(1) + RBRACKET

  def typeString(atype:Type[Obj]):String ={
    val range  = (atype match {
      case arec:RecType[_,_] => if (atype.isDerived && Tokens.named(arec.name)) arec.name else arec.name + mapString(arec.value())
      case _ => atype.name
    }) + qString(atype.q)
    val domain = if (atype.isCanonical) Tokens.empty else {
      (atype.domain() match {
        case arec:RecType[_,_] => if (!atype.isCanonical && Tokens.named(arec.name)) arec.name else arec.name + mapString(arec.value())
        case btype:Type[_] => btype.name
      }) + qString(atype.domain().q)
    }
    (if (domain.equals(EMPTY) || range.equals(domain)) range else (range + LDARROW + (if (atype.domain().alive() && !atype.domain().equals(obj.q(qStar))) domain else Tokens.empty))) + atype.insts.map(_._2.toString()).fold(Tokens.empty)((a,b) => a + b)
  }

  def valueString(avalue:Value[Obj]):String ={
    val named = Tokens.named(avalue.name)
    (if (named) avalue.name + COLON else EMPTY) + (
      avalue match {
        case arec:RecValue[_,_] => mapString(arec.value)
        case astr:StrValue => SQUOTE + astr.value + SQUOTE
        case _ => avalue.value
      }) + qString(avalue.q)
  }

  def instString(inst:Inst[_,_]):String ={
    (inst.op() match {
      case Tokens.to | Tokens.from => LANGLE + inst.arg0[StrValue]().value + RANGLE
      case Tokens.choose => LBRACKET + Tokens.choose + COMMA + mapString(inst.arg0[RecType[Obj,Obj]]().value(),PIPE) + RBRACKET
      case _ => inst.args() match {
        case Nil => LBRACKET + inst.op() + RBRACKET
        case args:List[Obj] => LBRACKET + inst.op() + COMMA + args.map(arg => arg.toString + COMMA).fold(EMPTY)((a,b) => a + b).dropRight(1) + RBRACKET
      }
    }) + qString(inst.q)
  }
}
