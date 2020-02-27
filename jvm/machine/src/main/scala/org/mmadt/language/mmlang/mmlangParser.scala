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
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.branch.ChooseOp
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.reduce.{CountOp,FoldOp}
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.op.traverser.{ExplainOp,FromOp,ToOp}
import org.mmadt.language.obj.value.strm.{IntStrm,Strm}
import org.mmadt.language.obj.value.{BoolValue,IntValue,StrValue}
import org.mmadt.storage.obj._
import org.mmadt.storage.obj.value.strm.{VIntStrm,VRecStrm}

import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object mmlangParser extends JavaTokenParsers {

  override val whiteSpace:Regex = """[\s\n]+""".r

  def parse[T](input:String):Iterator[T] = this.parseAll(expr | emptySpace,input).map{
    case itty:Iterator[T] => itty
    case obj:T => Iterator(obj)
  }.get

  def emptySpace[T]:Parser[Iterator[T]] = (Tokens.empty | whiteSpace) ^^ (_ => Iterator.empty)
  lazy val expr:Parser[Any] = multiple | single | obj

  lazy val single  :Parser[O]           = ((strm | obj) <~ Tokens.:=>) ~ (aType | anonType) ^^ (x => (x._1 ==> x._2).asInstanceOf[O]) // TODO: I'm improperly typing to Type (why?)
  lazy val multiple:Parser[Iterator[O]] = ((strm | obj) <~ "==>") ~ objType ^^ (x => x._1 ===> x._2)

  lazy val canonicalType:Parser[OType] = (Tokens.bool | Tokens.int | Tokens.str | Tokens.rec) ~ opt(quantifier) ^^ {
    case atype ~ q => q.foldRight(atype match {
      //case Tokens.obj => tobj
      case Tokens.bool => bool
      case Tokens.int => int
      case Tokens.str => str
      case Tokens.rec => rec
    })((q,t) => t.q(q))
  }

  lazy val objType:Parser[OType] = aType | recType | anonType

  lazy val aType   :Parser[OType]    = (opt(canonicalType <~ Tokens.:<=)) ~ canonicalType ~ rep[Inst](inst | stateAccess ^^ (x => ToOp(str(x._2)))) ^^ {
    case Some(range) ~ domain ~ insts => (range <= insts.foldLeft(domain)((x,y) => y(x).asInstanceOf[OType]))
    case None ~ domain ~ insts => insts.foldLeft(domain)((x,y) => y(x).asInstanceOf[OType])
  }
  lazy val recType :Parser[ORecType] = "[" ~> repsep((obj <~ Tokens.:->) ~ obj,Tokens.:|) <~ "]" ^^ (x => trec(x.map(o => (o._1,o._2)).toMap))
  lazy val anonType:Parser[__]       = rep1[Inst](inst | stateAccess ^^ (x => ToOp(str(x._2)))) ^^ (x => new __(x)) // anonymous type (instructions only -- no domain/range)

  lazy val stateAccess:Parser[Option[OType] ~ String] = (opt(canonicalType) <~ "<") ~ "[a-zA-z]*".r <~ ">"

  lazy val quantifier    :Parser[TQ] = ("{" ~> quantifierType <~ "}") | ("{" ~> intValue ~ opt("," ~> intValue) <~ "}") ^^ (x => (x._1,x._2.getOrElse(x._1)))
  lazy val quantifierType:Parser[TQ] = (Tokens.q_star | Tokens.q_mark | Tokens.q_plus) ^^ {
    case Tokens.q_star => qStar
    case Tokens.q_mark => qMark
    case Tokens.q_plus => qPlus
  }

  lazy val obj      :Parser[O]         = objValue | objType
  lazy val boolValue:Parser[BoolValue] = (Tokens.btrue | Tokens.bfalse) ^^ (x => bool(x.toBoolean))
  lazy val intValue :Parser[IntValue]  = wholeNumber ^^ (x => int(x.toLong))
  lazy val strValue :Parser[StrValue]  = ("""'([^'\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*'""").r ^^ (x => str(x.subSequence(1,x.length - 1).toString))
  lazy val recValue :Parser[ORecValue] = "[" ~> repsep((obj <~ Tokens.::) ~ obj,",") <~ "]" ^^ (x => rec(x.map(o => (o._1,o._2)).toMap))
  lazy val objValue :Parser[OValue]    = (boolValue | intValue | strValue | recValue) ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q)).getOrElse(x._1))
  lazy val strm     :Parser[Strm[_]]   = intStrm | recStrm
  lazy val intStrm  :Parser[IntStrm]   = (intValue <~ ",") ~ rep1sep(intValue,",") ^^ (x => new VIntStrm(x._1 +: x._2))
  lazy val recStrm  :Parser[ORecStrm]  = (recValue <~ ",") ~ rep1sep(recValue,",") ^^ (x => new VRecStrm[O,O](x._1 +: x._2:_*))

  lazy val instArg      :Parser[O]    = stateAccess ^^ (x => x._1.getOrElse(int).from[OType](str(x._2))) | obj // TODO: need to have an instantiable obj type as the general type (see hardcoded use of int here)
  lazy val inst         :Parser[Inst] = chooseSugar | sugarlessInst | infixSugar
  lazy val infixSugar   :Parser[Inst] = (Tokens.plus_op | Tokens.mult_op | Tokens.gt_op | Tokens.eqs_op) ~ instArg ^^ (x => instMatrix(x._1,List(x._2)))
  lazy val chooseSugar  :Parser[Inst] = recType ^^ (x => ChooseOp(x.asInstanceOf[RecType[OType,O]]))
  lazy val sugarlessInst:Parser[Inst] = "[" ~> ("""[a-zA-Z][a-zA-Z0-9]*""".r <~ opt(",")) ~ repsep(instArg,",") <~ "]" ^^ (x => instMatrix(x._1,x._2))

  private def instMatrix(op:String,arg:List[O]):Inst ={
    op match {
      case Tokens.plus | Tokens.plus_op => arg.head match {
        case arg:IntValue => PlusOp(arg)
        case arg:IntType => PlusOp(arg)
        case arg:StrValue => PlusOp(arg)
        case arg:StrType => PlusOp(arg)
        case arg:ORecValue => PlusOp(arg)
        case arg:ORecType => PlusOp(arg)
        case arg:__ => PlusOp(arg)
      }
      case Tokens.mult | Tokens.mult_op => arg.head match {
        case arg:IntValue => MultOp(arg)
        case arg:IntType => MultOp(arg)
        case arg:StrValue => MultOp(arg)
        case arg:StrType => MultOp(arg)
        case arg:__ => MultOp(arg)
      }
      case Tokens.gt | Tokens.gt_op => arg.head match {
        case arg:IntValue => GtOp(arg)
        case arg:IntType => GtOp(arg)
        case arg:StrValue => GtOp(arg)
        case arg:StrType => GtOp(arg)
        case arg:__ => GtOp(arg)
      }
      case Tokens.eqs | Tokens.eqs_op => arg.head match {
        case arg:BoolValue => EqsOp(arg)
        case arg:BoolType => EqsOp(arg)
        case arg:IntValue => EqsOp(arg)
        case arg:IntType => EqsOp(arg)
        case arg:StrValue => EqsOp(arg)
        case arg:StrType => EqsOp(arg)
        case arg:ORecValue => EqsOp(arg)
        case arg:ORecType => EqsOp(arg)
        case arg:__ => EqsOp(arg)
      }
      case Tokens.is => arg.head match {
        case arg:BoolValue => IsOp(arg)
        case arg:BoolType => IsOp(arg)
        case arg:__ => IsOp(arg)
      }
      case Tokens.get => arg match {
        case List(key:O,typeHint:TType[O]) => GetOp(key,typeHint)
        case List(key:O) => GetOp(key)
      }
      case Tokens.map => arg.head match {
        case arg:__ => MapOp(arg)
        case arg:Obj => MapOp(arg)
      }
      case Tokens.count => CountOp()
      case Tokens.explain => ExplainOp()
      case Tokens.put => PutOp(arg.head,arg.tail.head)
      case Tokens.from => FromOp(arg.head.asInstanceOf[StrValue])
      case Tokens.fold => arg.tail.tail.head match{
        case x:__ => FoldOp(("seed",arg.tail.head),x)
        case x:TType[O] => FoldOp(("seed",arg.tail.head),x)
      }
      case Tokens.to => ToOp(arg.head.asInstanceOf[StrValue])
      case Tokens.choose => ChooseOp(arg.head.asInstanceOf[RecType[OType,O]])
      case Tokens.id => IdOp()
    }
  }
}