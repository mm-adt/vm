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
import org.mmadt.language.obj.op.traverser.ToOp
import org.mmadt.language.obj.value.strm.{BoolStrm, IntStrm, StrStrm, Strm}
import org.mmadt.language.obj.value.{BoolValue, IntValue, StrValue, Value}
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

  def parse[T <: Obj](input:String,_model:Model = Model.id):Iterator[T] ={
    if (null != _model) this.model = _model
    this.parseAll(expr | emptySpace,input.trim).map{
      case itty:Iterator[T] => itty
      case obj:T => Iterator(obj)
    }.get
  }

  def emptySpace[T]:Parser[Iterator[T]] = (Tokens.empty | whiteSpace) ^^ (_ => Iterator.empty)
  lazy val expr         :Parser[Any]           = evaluation | compilation | obj
  lazy val evaluation   :Parser[Iterator[Obj]] = (strm | objValue) ~ opt(aType | anonType) ^^ (x => x._1 ===> {
    val t = InstUtil.resolveAnonymous(x._1,x._2.getOrElse(asType(x._1).id()))
    (t.domain() ==> this.model) (t)
  })
  lazy val compilation  :Parser[Obj]           = objType ^^ (x => (x.domain() ==> this.model) (x))
  lazy val instOp       :String                = Tokens.ops.foldRight(EMPTY)((a,b) => b + PIPE + a).drop(1)
  lazy val canonicalType:Parser[Type[Obj]]     = (Tokens.bool | Tokens.int | Tokens.str | Tokens.rec | ("^(?!(" + instOp + "))([a-zA-Z]+)").r <~ not(":")) ~ opt(quantifier) ^^ {
    case atype ~ q => q.foldRight(atype match {
      //case Tokens.obj => tobj
      case Tokens.bool => bool
      case Tokens.int => int
      case Tokens.str => str
      case Tokens.rec => rec
      case name:String =>
        this.model.get(name) match { // NOTE: model lookup when a domain is unknown (ONLY PLACE MODEL IS USED BY PARSER -- GET RID OF)
          case Some(atype) => atype
          case None => tobj(name)
        }
    })((q,t) => t.q(q))
  }

  lazy val name       :Parser[String]    = "[a-zA-Z]+".r <~ ":"
  lazy val obj        :Parser[Obj]       = objValue | objType
  lazy val objType    :Parser[Type[Obj]] = aType | recType | anonType
  lazy val domainRange:Parser[Type[Obj]] = (Tokens.rec ~> recType) | canonicalType

  lazy val aType   :Parser[Type[Obj]] = opt(domainRange <~ Tokens.:<=) ~ domainRange ~ rep[Inst](inst | stateAccess ^^ (x => ToOp(str(x._2)))) ^^ {
    case Some(range) ~ domain ~ insts => (range <= insts.foldLeft(domain)((x,y) => y(model.resolve(x)).asInstanceOf[Type[Obj]]))
    case None ~ domain ~ insts => insts.foldLeft(domain)((x,y) => y(model.resolve(x)).asInstanceOf[Type[Obj]])
  }
  lazy val recType :Parser[ORecType]  = opt(name) ~ (LBRACKET ~> repsep((obj <~ Tokens.:->) ~ obj,(COMMA | PIPE))) <~ RBRACKET ^^ (x => trec(x._1.getOrElse(Tokens.rec),x._2.map(o => (o._1,o._2)).toMap))
  lazy val anonType:Parser[__]        = rep1[Inst](inst | stateAccess ^^ (x => ToOp(str(x._2)))) ^^ (x => __(x:_*))

  lazy val stateAccess:Parser[Option[Type[Obj]] ~ String] = (opt(canonicalType) <~ LANGLE) ~ "[a-zA-z]+".r <~ RANGLE

  lazy val quantifier    :Parser[IntQ] = (LCURL ~> quantifierType <~ RCURL) | (LCURL ~> intValue ~ opt(COMMA ~> intValue) <~ RCURL) ^^ (x => (x._1,x._2.getOrElse(x._1)))
  lazy val quantifierType:Parser[IntQ] = (Tokens.q_star | Tokens.q_mark | Tokens.q_plus) ^^ {
    case Tokens.q_star => qStar
    case Tokens.q_mark => qMark
    case Tokens.q_plus => qPlus
  }

  lazy val objValue :Parser[Value[Obj]] = (boolValue | intValue | strValue | recValue) ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q)).getOrElse(x._1))
  lazy val boolValue:Parser[BoolValue]  = opt(name) ~ (Tokens.btrue | Tokens.bfalse) ^^ (x => vbool(x._1.getOrElse(Tokens.bool),x._2.toBoolean,qOne))
  lazy val intValue :Parser[IntValue]   = opt(name) ~ wholeNumber ^^ (x => vint(x._1.getOrElse(Tokens.int),x._2.toLong,qOne))
  lazy val strValue :Parser[StrValue]   = opt(name) ~ ("""'([^'\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*'""").r ^^ (x => vstr(x._1.getOrElse(Tokens.str),x._2.subSequence(1,x._2.length - 1).toString,qOne))
  lazy val recValue :Parser[ORecValue]  = opt(name) ~ (LBRACKET ~> repsep((objValue <~ Tokens.:->) ~ objValue,COMMA) <~ RBRACKET) ^^ (x => vrec(x._1.getOrElse(Tokens.rec),x._2.map(o => (o._1,o._2)).toMap,qOne))
  lazy val strm     :Parser[Strm[Obj]]  = boolStrm | intStrm | strStrm | recStrm
  lazy val boolStrm :Parser[BoolStrm]   = (boolValue <~ COMMA) ~ rep1sep(boolValue,COMMA) ^^ (x => bool(x._1,x._2.head,x._2.tail:_*))
  lazy val intStrm  :Parser[IntStrm]    = (intValue <~ COMMA) ~ rep1sep(intValue,COMMA) ^^ (x => int(x._1,x._2.head,x._2.tail:_*))
  lazy val strStrm  :Parser[StrStrm]    = (strValue <~ COMMA) ~ rep1sep(strValue,COMMA) ^^ (x => str(x._1,x._2.head,x._2.tail:_*))
  lazy val recStrm  :Parser[ORecStrm]   = (recValue <~ COMMA) ~ rep1sep(recValue,COMMA) ^^ (x => vrec(x._1,x._2.head,x._2.tail:_*))

  lazy val instArg      :Parser[Obj]  = (stateAccess ^^ (x => x._1.getOrElse(int).from[Obj](str(x._2)))) | obj // TODO: hardcoded int for unspecified state type
  lazy val inst         :Parser[Inst] = sugarlessInst | chooseSugar | infixSugar
  lazy val infixSugar   :Parser[Inst] = (Tokens.plus_op | Tokens.mult_op | Tokens.gt_op | Tokens.eqs_op) ~ instArg ^^ (x => OpInstResolver.resolve(x._1,List(x._2)))
  lazy val chooseSugar  :Parser[Inst] = (LBRACKET ~> repsep((obj <~ Tokens.:->) ~ obj,PIPE)) <~ RBRACKET ^^ (x => ChooseOp(trec(value = x.map(o => (o._1,o._2)).toMap)))
  lazy val sugarlessInst:Parser[Inst] = LBRACKET ~> ("""[a-z]+""".r <~ opt(COMMA)) ~ repsep(instArg,COMMA) <~ RBRACKET ^^ (x => OpInstResolver.resolve(x._1,x._2))

}