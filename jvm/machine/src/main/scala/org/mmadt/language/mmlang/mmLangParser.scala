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
import org.mmadt.language.obj.`type`.IntType
import org.mmadt.language.obj.op.PlusOp
import org.mmadt.language.obj.value.IntValue
import org.mmadt.language.obj.{Inst, OType}
import org.mmadt.storage.obj._

import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object mmLangParser extends JavaTokenParsers {

  def op:Parser[String] = """[a-z]+""".r
  def expr:Parser[OType] = canonicalType ~ inst ^^ (x => x._1.asInstanceOf[IntType].plus(x._2.arg[IntValue]()))
  def intValue:Parser[IntValue] = """[0-9]+""".r ^^ (x => int(x.toLong))
  def canonicalType:Parser[OType] = (Tokens.int | Tokens.str) ^^ ({
    case Tokens.int => int
    case Tokens.str => str
  })
  def inst:Parser[Inst] = "[" ~ op ~ "," ~ intValue ~ "]" ^^ (x => PlusOp(x._1._2))

}

object LocalApp extends App {
  override def main(args:Array[String]):Unit ={
    mmLangParser.parseAll(mmLangParser.expr,"int[plus,2]") match {
      case mmLangParser.Success(result,_) => println(result)
      case _ => println("Could not parse the input string.")
    }
  }
}