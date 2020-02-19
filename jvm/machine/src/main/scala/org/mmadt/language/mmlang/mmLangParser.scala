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
import org.mmadt.language.obj.op.{GtOp,MultOp,PlusOp}
import org.mmadt.language.obj.value.{BoolValue,IntValue,RecValue,StrValue}
import org.mmadt.storage.obj._

import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object mmLangParser extends JavaTokenParsers {

  def parse[T](expression:String):T = this.parseAll(expr,expression).get.asInstanceOf[T]

  def expr:Parser[Any] = (single | multiple | obj)

  def single:Parser[O] = (obj <~ "=>") ~ objType ^^ (x => (x._1 ==> x._2).asInstanceOf[O]) // TODO: I'm improperly typing to Type (why?)
  def multiple:Parser[Iterator[O]] = (obj <~ "==>") ~ objType ^^ (x => x._1 ===> x._2)

  def canonicalType:Parser[OType] = (Tokens.bool | Tokens.int | Tokens.str | Tokens.rec) ~ (quantifier ?) ^^ {
    case a ~ b => a match {
      case Tokens.bool => bool.q(b.getOrElse(qOne))
      case Tokens.int => int.q(b.getOrElse(qOne))
      case Tokens.str => str.q(b.getOrElse(qOne))
      case Tokens.rec => rec.q(b.getOrElse(qOne))
    }
  }

  def objType:Parser[OType] = ((canonicalType <~ "<=") ?) ~ canonicalType ~ rep[Inst](inst) ^^ {
    case Some(a) ~ b ~ c => a <= c.foldLeft(b)((x,y) => y(x).asInstanceOf[OType])
    case None ~ b ~ c => c.foldLeft(b)((x,y) => y(x).asInstanceOf[OType])
  }

  def quantifier:Parser[TQ] = ("{" ~> quantifierType <~ "}") | ("{" ~> (intValue <~ ("," ?)) ~ (intValue ?) <~ "}") ^^ (x => (x._1,x._2.getOrElse(x._1)))
  def quantifierType:Parser[TQ] = ("\\*".r | "\\?".r | "\\+".r) ^^ {
    case "*" => qStar
    case "?" => qMark
    case "+" => qPlus
  }

  def boolValue:Parser[BoolValue] = "true|false".r ^^ (x => bool(x.toBoolean))
  def intValue:Parser[IntValue] = """[0-9]+""".r ^^ (x => int(x.toLong))
  def strValue:Parser[StrValue] = "'[[a-z]|\\s]*'".r ^^ (x => str(x.subSequence(1,x.length - 1).toString))
  def recValue:Parser[RecValue[O,O]] = "[" ~> rep((obj <~ ":") ~ obj <~ ("," ?)) <~ "]" ^^ (x => rec(x.reverse.map(o => (o._1,o._2)).toMap))
  def objValue:Parser[OValue] = (boolValue | intValue | strValue | recValue) ~ (quantifier ?) ^^ (x => x._1.q(x._2.getOrElse(qOne)))

  def obj:Parser[O] = objValue | objType

  def inst:Parser[Inst] = "[" ~> ("""[a-z]+""".r <~ ",") ~ objValue <~ "]" ^^ {
    case a ~ b => a match {
      case Tokens.plus => b match {
        case b:IntValue => PlusOp(b)
        case b:StrValue => PlusOp(b)
      }
      case Tokens.mult => b match {
        case b:IntValue => MultOp(b)
        case b:StrValue => MultOp(b)
      }
      case Tokens.gt => b match {
        case b:IntValue => GtOp(b)
        case b:StrValue => GtOp(b)
      }
    }
  }
}

/*object LocalApp extends App {
  override def main(args:Array[String]):Unit ={
    mmLangParser.parseAll(mmLangParser.expr,"int{?}") match {
      case mmLangParser.Success(result,_) => println(result + ":" + result.getClass)
      case _ => println("Could not parse the input string.")
    }
  }
}*/