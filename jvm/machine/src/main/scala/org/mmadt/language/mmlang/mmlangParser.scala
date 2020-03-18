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
import org.mmadt.language.model.Model
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.OpInstResolver
import org.mmadt.language.obj.op.branch.ChooseOp
import org.mmadt.language.obj.op.map.GetOp
import org.mmadt.language.obj.op.model.AsOp
import org.mmadt.language.obj.op.traverser.{FromOp, ToOp}
import org.mmadt.language.obj.value.strm.{BoolStrm, IntStrm, StrStrm, Strm}
import org.mmadt.language.obj.value.{BoolValue, IntValue, StrValue, Value}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory.{strm => estrm, _}

import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmlangParser(val model:Model) extends JavaTokenParsers {

  override val whiteSpace:Regex = """[\s\n]+""".r

  // all mm-ADT languages must be able to accept a string representation of an expression in the language and return an Obj
  private def parse[O <: Obj](input:String):O ={
    this.parseAll(expr | emptySpace,input.trim).get.asInstanceOf[O]
  }
  private def emptySpace[O <: Obj]:Parser[O] = (Tokens.empty | whiteSpace) ^^ (_ => estrm[O])

  // specific to mmlang execution
  lazy val expr       :Parser[Obj] = compilation | evaluation | (objValue | anonType)
  lazy val compilation:Parser[Obj] = aType ^^ (x => (x.domain() ==> this.model) (x))
  lazy val evaluation :Parser[Obj] = (strm | objValue) ~ (anonType | aType) ^^ (x => x._1 ===> (Type.resolveAnonymous(x._1,x._2).domain() ==> this.model) (x._2))

  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////////////

  // mmlang's language structure
  lazy val obj:Parser[Obj] = objValue | objType

  // type parsing
  lazy val objType  :Parser[Type[Obj]]    = anonType | aType
  lazy val boolType :Parser[BoolType]     = Tokens.bool ^^ (_ => bool)
  lazy val intType  :Parser[IntType]      = Tokens.int ^^ (_ => int)
  lazy val strType  :Parser[StrType]      = Tokens.str ^^ (_ => str)
  lazy val recType  :Parser[ORecType]     = (Tokens.rec ~> opt(recStruct)) ^^ (x => trec(value = x.getOrElse(Map.empty)))
  lazy val recStruct:Parser[Map[Obj,Obj]] = (LBRACKET ~> repsep((obj <~ Tokens.:->) ~ obj,(COMMA | PIPE)) <~ RBRACKET) ^^ (x => x.map(o => (o._1,o._2)).toMap)
  lazy val namedType:Parser[Type[Obj]]    = ("^(?!(" + instOp + "))([a-zA-Z]+)").r <~ not(":") ^^ (x => this.model.get(x) match {
    case Some(atype) => atype
    case None => tobj(x)
  })
  lazy val cType    :Parser[Type[Obj]]    = (boolType | intType | strType | recType | namedType) ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q)).getOrElse(x._1))
  lazy val aType    :Parser[Type[Obj]]    = opt(cType <~ Tokens.:<=) ~ cType ~ rep[Inst[Obj,Obj]](stateAccess ^^ (x => ToOp[Obj](str(x._2))) | inst | cType ^^ (t => AsOp(t))) ^^ {
    case Some(range) ~ domain ~ insts => (range <= insts.foldLeft(domain)((x,y) => y(Traverser.standard(x,model = this.model)).obj().asInstanceOf[Type[Obj]]))
    case None ~ domain ~ insts => insts.foldLeft(domain)((x,y) => y(Traverser.standard(x,model = this.model)).obj().asInstanceOf[Type[Obj]])
  }
  lazy val anonType :Parser[__]           = (stateAccess ^^ (x => FromOp(str(x._2))) | inst) ~ rep[Inst[Obj,Obj]](inst | stateAccess ^^ (x => ToOp[Obj](str(x._2))) | cType ^^ (t => AsOp(t))) ^^ (x => __(x._1 :: x._2))
  lazy val instOp   :String               = Tokens.reserved.foldRight(EMPTY)((a,b) => b + PIPE + a).drop(1)

  // value parsing
  lazy val objValue :Parser[Value[Obj]] = (boolValue | intValue | strValue | recValue) ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q)).getOrElse(x._1))
  lazy val boolValue:Parser[BoolValue]  = opt(valueType) ~ (Tokens.btrue | Tokens.bfalse) ^^ (x => vbool(x._1.getOrElse(Tokens.bool),x._2.toBoolean,qOne))
  lazy val intValue :Parser[IntValue]   = opt(valueType) ~ wholeNumber ^^ (x => vint(x._1.getOrElse(Tokens.int),x._2.toLong,qOne))
  lazy val strValue :Parser[StrValue]   = opt(valueType) ~ ("""'([^'\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*'""").r ^^ (x => vstr(x._1.getOrElse(Tokens.str),x._2.subSequence(1,x._2.length - 1).toString,qOne))
  lazy val recValue :Parser[ORecValue]  = opt(valueType) ~ (LBRACKET ~> repsep((objValue <~ Tokens.:->) ~ objValue,COMMA) <~ RBRACKET) ^^ (x => vrec(x._1.getOrElse(Tokens.rec),x._2.map(o => (o._1,o._2)).toMap,qOne))
  lazy val valueType:Parser[String]     = "[a-zA-Z]+".r <~ ":"
  lazy val strm     :Parser[Strm[Obj]]  = boolStrm | intStrm | strStrm | recStrm
  lazy val boolStrm :Parser[BoolStrm]   = (boolValue <~ COMMA) ~ rep1sep(boolValue,COMMA) ^^ (x => bool(x._1,x._2.head,x._2.tail:_*))
  lazy val intStrm  :Parser[IntStrm]    = (intValue <~ COMMA) ~ rep1sep(intValue,COMMA) ^^ (x => int(x._1,x._2.head,x._2.tail:_*))
  lazy val strStrm  :Parser[StrStrm]    = (strValue <~ COMMA) ~ rep1sep(strValue,COMMA) ^^ (x => str(x._1,x._2.head,x._2.tail:_*))
  lazy val recStrm  :Parser[ORecStrm]   = (recValue <~ COMMA) ~ rep1sep(recValue,COMMA) ^^ (x => vrec(x._1,x._2.head,x._2.tail:_*))

  // instruction parsing
  lazy val inst         :Parser[Inst[Obj,Obj]]              = (sugarlessInst | infixSugar | getSugar | chooseSugar) ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q).asInstanceOf[Inst[Obj,Obj]]).getOrElse(x._1))
  lazy val infixSugar   :Parser[Inst[Obj,Obj]]              = (Tokens.plus_op | Tokens.mult_op | Tokens.gt_op | Tokens.eqs_op | Tokens.a_op | Tokens.is) ~ obj ^^ (x => OpInstResolver.resolve(x._1,List(x._2)))
  lazy val chooseSugar  :Parser[Inst[Obj,Obj]]              = (LBRACKET ~> repsep((obj <~ Tokens.:->) ~ obj,PIPE)) <~ RBRACKET ^^ (x => ChooseOp(trec(value = x.map(o => (o._1,o._2)).toMap)))
  lazy val getSugar     :Parser[Inst[Obj,Obj]]              = Tokens.get_op ~> "[a-zA-Z]+".r ^^ (x => GetOp[Obj,Obj](str(x)).asInstanceOf[Inst[Obj,Obj]])
  lazy val sugarlessInst:Parser[Inst[Obj,Obj]]              = LBRACKET ~> ("""=?[a-z]+""".r <~ opt(COMMA)) ~ repsep(obj,COMMA) <~ RBRACKET ^^ (x => OpInstResolver.resolve(x._1,x._2))
  // traverser instruction parsing
  lazy val stateAccess  :Parser[Option[Type[Obj]] ~ String] = (opt(cType) <~ LANGLE) ~ "[a-zA-z]+".r <~ RANGLE

  // quantifier parsing
  lazy val quantifier    :Parser[IntQ] = (LCURL ~> quantifierType <~ RCURL) | (LCURL ~> intValue ~ opt(COMMA ~> intValue) <~ RCURL) ^^ (x => (x._1,x._2.getOrElse(x._1)))
  lazy val quantifierType:Parser[IntQ] = (Tokens.q_star | Tokens.q_mark | Tokens.q_plus) ^^ {
    case Tokens.q_star => qStar
    case Tokens.q_mark => qMark
    case Tokens.q_plus => qPlus
  }
}

object mmlangParser {
  def parse[O <: Obj](script:String,model:Model):O = new mmlangParser(model).parse[O](script)
}