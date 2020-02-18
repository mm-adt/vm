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

import scala.util.matching.Regex
import scala.util.parsing.combinator.RegexParsers

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object mmLangParser extends RegexParsers {
  private val spaceRegex  = new Regex("[ \\n]+");
  private val numberRegex = new Regex("[0-9]+");
  private val wordRegex   = new Regex("[a-zA-Z][a-zA-Z0-9-]*");

  private def space:Parser[String] = regex(spaceRegex)
  private def regexAndSpace(re:Regex):Parser[String] = regex(re) <~ space

  override def skipWhitespace:Boolean = false

  def number:Parser[String] = regexAndSpace(numberRegex)
  def word:Parser[String] = regexAndSpace(wordRegex)
  def string:Parser[String] = regex(numberRegex) >> {len => ":" ~> regexAndSpace(new Regex(".{" + len + "}"))}
  def list:Parser[List[java.io.Serializable]] = "(" ~> space ~> (item +) <~ ")" <~ space

  def item:Parser[java.io.Serializable] = (number | word | string | list)

  def parseItem(str:String):ParseResult[java.io.Serializable] = parse(item,str)

}

/*object Appd extends App {
  override def main(args:Array[String]):Unit ={
    parseItem("( 5:abcde 3:abc  \n   20:three separate words     (  abc def     \n\n\n   123 ) ) ") match {
      case mmLangParser.Success(result,_) => println(result.toString)
      case _ => println("Could not parse the input string.")
    }
  }
}*/