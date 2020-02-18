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
import org.mmadt.language.obj.op.{GtOp,MultOp,PlusOp}
import org.mmadt.language.obj.value.{BoolValue,IntValue,StrValue}
import org.mmadt.language.obj.{Inst,OType,OValue}
import org.mmadt.storage.obj._

import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object mmLangParser extends JavaTokenParsers {
  def expr:Parser[OType] = ((canonicalType <~ "<=") ?) ~ canonicalType ~ rep[Inst](inst) ^^ {
    case Some(a) ~ b ~ c => a <= c.foldLeft(b)((x,y) => y.apply(x).asInstanceOf[OType])
    case None ~ b ~ c => c.foldLeft(b)((x,y) => y.apply(x).asInstanceOf[OType])
  }

  def canonicalType:Parser[OType] = (Tokens.bool | Tokens.int | Tokens.str) ^^ {
    case Tokens.int => int
    case Tokens.str => str
    case Tokens.bool => bool
  }

  def intValue:Parser[IntValue] = """[0-9]+""".r ^^ (x => int(x.toLong))
  def strValue:Parser[StrValue] = "'[a-z]+'".r ^^ (x => str(x))
  def boolValue:Parser[BoolValue] = "true|false".r ^^ (x => bool(x.toBoolean))
  def objValue:Parser[OValue] = boolValue | intValue | strValue

  def op:Parser[String] = """[a-z]+""".r
  def inst:Parser[Inst] = "[" ~> (op <~ ",") ~ objValue <~ "]" ^^ {
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
    mmLangParser.parseAll(mmLangParser.expr,"bool<=int[plus,4][mult,2][gt,15]") match {
      case mmLangParser.Success(result,_) => println(result + ":" + result.getClass)
      case _ => println("Could not parse the input string.")
    }
  }
}*/