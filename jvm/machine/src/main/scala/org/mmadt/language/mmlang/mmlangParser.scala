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
import org.mmadt.language.obj.op.traverser.ToOp
import org.mmadt.language.obj.value.strm.{BoolStrm,IntStrm,StrStrm,Strm}
import org.mmadt.language.obj.value.{BoolValue,IntValue,StrValue,Value}
import org.mmadt.processor.obj.`type`.util.InstUtil
import org.mmadt.storage.StorageFactory._

import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object mmlangParser extends JavaTokenParsers {

  override val whiteSpace:Regex = """[\s\n]+""".r
  var model:Model = Model.id

  // all mm-ADT languages must be able to accept a string representation of an expression in the language and return an Iterator[Obj]
  def parse[T <: Obj](input:String,_model:Model = Model.id):Iterator[T] ={
    if (null != _model) this.model = _model
    this.parseAll(expr | emptySpace,input.trim).map{
      case itty:Iterator[T] => itty
      case obj:T => Iterator(obj)
    }.get
  }
  def emptySpace[T]:Parser[Iterator[T]] = (Tokens.empty | whiteSpace) ^^ (_ => Iterator.empty)

  // specific to mmlang execution
  lazy val expr       :Parser[Any]           = compilation | evaluation | (objValue | anonType)
  lazy val compilation:Parser[Obj]           = aType ^^ (x => (x.domain() ==> this.model) (x))
  lazy val evaluation :Parser[Iterator[Obj]] = (strm | objValue) ~ (anonType | aType) ^^ (x => x._1 ===> (InstUtil.resolveAnonymous(x._1,x._2).domain() ==> this.model) (x._2))

  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////////////

  // mmlang's language structure
  lazy val obj:Parser[Obj] = objValue | objType

  // type parsing
  lazy val objType  :Parser[Type[Obj]]    = aType | anonType
  lazy val boolType :Parser[BoolType]     = Tokens.bool ^^ (_ => bool)
  lazy val intType  :Parser[IntType]      = Tokens.int ^^ (_ => int)
  lazy val strType  :Parser[StrType]      = Tokens.str ^^ (_ => str)
  lazy val recType  :Parser[ORecType]     = (Tokens.rec ~> recStruct) ^^ (x => trec(value = x))
  lazy val recStruct:Parser[Map[Obj,Obj]] = (LBRACKET ~> repsep((obj <~ Tokens.:->) ~ obj,(COMMA | PIPE)) <~ RBRACKET) ^^ (x => x.map(o => (o._1,o._2)).toMap)
  lazy val namedType:Parser[Type[Obj]]    = ("^(?!(" + instOp + "))([a-zA-Z]+)").r <~ not(":") ^^ (x => this.model.get(x) match {
    case Some(atype) => atype
    case None => tobj(x)
  })
  lazy val cType    :Parser[Type[Obj]]    = (boolType | intType | strType | recType | namedType) ~ opt(quantifier) ^^ (x => x._2.map(x._1.q).getOrElse(x._1))
  lazy val aType    :Parser[Type[Obj]]    = opt(cType <~ Tokens.:<=) ~ cType ~ rep[Inst](inst | stateAccess ^^ (x => ToOp(str(x._2)))) ^^ {
    case Some(range) ~ domain ~ insts => (range <= insts.foldLeft(domain)((x,y) => y(this.model.resolve(x)).asInstanceOf[Type[Obj]]))
    case None ~ domain ~ insts => insts.foldLeft(domain)((x,y) => {
      y(this.model.resolve(x)).asInstanceOf[Type[Obj]]
    })
  }
  lazy val anonType :Parser[__]           = rep1[Inst](inst | stateAccess ^^ (x => ToOp(str(x._2)))) ^^ (x => __(x:_*))
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
  lazy val instArg      :Parser[Obj]                        = (stateAccess ^^ (x => x._1.getOrElse(int).from[Obj](str(x._2)))) | obj // TODO: hardcoded int for unspecified state type
  lazy val inst         :Parser[Inst]                       = (sugarlessInst | infixSugar | getSugar | chooseSugar) ~ opt(quantifier) ^^ (x => x._2.map(x._1.q).getOrElse(x._1))
  lazy val infixSugar   :Parser[Inst]                       = (Tokens.plus_op | Tokens.mult_op | Tokens.gt_op | Tokens.eqs_op) ~ instArg ^^ (x => OpInstResolver.resolve(x._1,List(x._2)))
  lazy val chooseSugar  :Parser[Inst]                       = (LBRACKET ~> repsep((obj <~ Tokens.:->) ~ obj,PIPE)) <~ RBRACKET ^^ (x => ChooseOp(trec(value = x.map(o => (o._1,o._2)).toMap)))
  lazy val getSugar     :Parser[Inst]                       = Tokens.get_op ~> "[a-zA-Z]+".r ^^ (x => GetOp(str(x)))
  lazy val sugarlessInst:Parser[Inst]                       = LBRACKET ~> ("""=?[a-z]+""".r <~ opt(COMMA)) ~ repsep(instArg,COMMA) <~ RBRACKET ^^ (x => OpInstResolver.resolve(x._1,x._2))
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