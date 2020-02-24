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
import org.mmadt.language.obj.`type`.{BoolType, IntType, StrType, __}
import org.mmadt.language.obj.op._
import org.mmadt.language.obj.value.{BoolValue, IntValue, RecValue, StrValue}
import org.mmadt.storage.obj._
import org.mmadt.storage.obj.value.VRec
import org.mmadt.storage.obj.value.strm.VIntStrm

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

  def emptySpace[T]:Parser[Iterator[T]] = ("" | whiteSpace) ^^ (_ => Iterator.empty)
  lazy val expr:Parser[Any] = single | multiple | multipleInt | obj

  lazy val single     :Parser[O]           = (obj <~ Tokens.:=>) ~ objType ^^ (x => (x._1 ==> x._2).asInstanceOf[O]) // TODO: I'm improperly typing to Type (why?)
  lazy val multiple   :Parser[Iterator[O]] = (obj <~ "==>") ~ objType ^^ (x => x._1 ===> x._2)
  lazy val multipleInt:Parser[Iterator[O]] = (rep1sep(intValue,",") <~ "==>") ~ objType ^^ (x => new VIntStrm(x._1) ===> x._2) // TODO: a demo around int as we figure out the strm structure

  lazy val canonicalType:Parser[OType] = (Tokens.bool | Tokens.int | Tokens.str | Tokens.rec) ~ (quantifier ?) ^^ {
    case atype ~ q => q.foldLeft(atype match {
      case Tokens.bool => bool
      case Tokens.int => int
      case Tokens.str => str
      case Tokens.rec => rec
    })((t,q) => t.q(q))
  }

  lazy val objType:Parser[OType] = ((canonicalType <~ Tokens.:<=) ?) ~ canonicalType ~ rep[Inst](inst | stateAccess ^^ (x => ToOp(str(x._2)))) ^^ {
    case Some(range) ~ domain ~ insts => (range <= insts.foldLeft(domain)((x,y) => y(x).asInstanceOf[OType]))
    case None ~ domain ~ insts => insts.foldLeft(domain)((x,y) => y(x).asInstanceOf[OType])
  } | rep1[Inst](inst | stateAccess ^^ (x => ToOp(str(x._2)))) ^^ (x => new __(x)) // anonymous type (instructions only -- no domain/range)

  lazy val stateAccess:Parser[Option[OType] ~ String] = ((canonicalType ?) <~ "<") ~ "[a-zA-z]*".r <~ ">"

  lazy val quantifier    :Parser[TQ] = ("{" ~> quantifierType <~ "}") | ("{" ~> intValue ~ (("," ~> intValue) ?) <~ "}") ^^ (x => (x._1,x._2.getOrElse(x._1)))
  lazy val quantifierType:Parser[TQ] = (Tokens.q_star | Tokens.q_mark | Tokens.q_plus) ^^ {
    case Tokens.q_star => qStar
    case Tokens.q_mark => qMark
    case Tokens.q_plus => qPlus
  }

  lazy val obj      :Parser[O]             = objValue | objType
  lazy val boolValue:Parser[BoolValue]     = (Tokens.btrue | Tokens.bfalse) ^^ (x => bool(x.toBoolean))
  lazy val intValue :Parser[IntValue]      = wholeNumber ^^ (x => int(x.toLong))
  lazy val strValue :Parser[StrValue]      = ("""'([^'\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*'""").r ^^ (x => str(x.subSequence(1,x.length - 1).toString))
  lazy val recValue :Parser[RecValue[O,O]] = "[" ~> repsep((obj <~ Tokens.::) ~ obj,",") <~ "]" ^^ (x => rec(x.map(o => (o._1,o._2)).toMap))
  lazy val objValue :Parser[OValue]        = (boolValue | intValue | strValue | recValue) ~ (quantifier ?) ^^ (x => x._2.map(q => x._1.q(q)).getOrElse(x._1))

  lazy val instArg      :Parser[O]    = stateAccess ^^ (x => x._1.getOrElse(int).from[OType](str(x._2))) | obj // TODO: need to have an instantiable obj type as the general type (see hardcoded use of int here)
  lazy val inst         :Parser[Inst] = sugarlessInst | operatorSugar | chooseSugar
  lazy val operatorSugar:Parser[Inst] = (Tokens.plus_op | Tokens.mult_op | Tokens.gt_op) ~ instArg ^^ (x => instMatrix(x._1,List(x._2)))
  lazy val chooseSugar  :Parser[Inst] = "[" ~> repsep((objType <~ Tokens.:->) ~ obj,Tokens.:|) <~ "]" ^^ (x => ChooseOp(rec(x.map(o => (o._1,o._2)).toMap)))
  lazy val sugarlessInst:Parser[Inst] = "[" ~> ("""[a-zA-Z][a-zA-Z0-9]*""".r <~ opt(",")) ~ repsep(instArg,",") <~ "]" ^^ (x => instMatrix(x._1,x._2)) // TODO: (hint:Option[OType] = None) (so users don't have to prefix their instruction compositions with a domain)

  private def instMatrix(op:String,arg:List[O]):Inst ={
    op match {
      case Tokens.plus | Tokens.plus_op => arg.head match {
        case arg:IntValue => PlusOp(arg)
        case arg:IntType => PlusOp(arg)
        case arg:StrValue => PlusOp(arg)
        case arg:StrType => PlusOp(arg)
        case arg:ORecValue => PlusOp(arg)
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
      case Tokens.is => arg.head match {
        case arg:BoolValue => IsOp(arg)
        case arg:BoolType => IsOp(arg)
        case arg:__ => IsOp(arg)
      }
      case Tokens.get => arg match {
        case List(key:O,typeHint:TType[O]) => GetOp(key,typeHint)
        case List(key:O) => GetOp(key)
      }
      case Tokens.put => PutOp(arg.head,arg.tail.head)
      case Tokens.from => FromOp(arg.head.asInstanceOf[StrValue])
      case Tokens.to => ToOp(arg.head.asInstanceOf[StrValue])
      case Tokens.choose => ChooseOp(arg.head.asInstanceOf[RecValue[OType,O]])
      case Tokens.id => IdOp()
    }
  }
}

/*object LocalApp extends App {
  override def main(args:Array[String]):Unit ={
    mmlangParser.parseAll(mmlangParser.expr,"['marko':'44']") match {
      case mmlangParser.Success(result,_) => println(result + ":" + result.getClass)
      case _ => println("Could not parse the input string.")
    }
  }
}*/