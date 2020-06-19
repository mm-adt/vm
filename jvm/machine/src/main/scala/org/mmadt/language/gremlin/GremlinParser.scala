/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.gremlin
import org.mmadt.VmException
import org.mmadt.language.LanguageException
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.__
import org.mmadt.storage.StorageFactory._

import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers

class GremlinParser extends JavaTokenParsers {

  override val whiteSpace: Regex = """[\s\n]+""".r
  override def decimalNumber: Parser[String] = """-?\d+\.\d+""".r

  // all mm-ADT languages must be able to accept a string representation of an expression in the language and return an Obj
  private def parse[O <: Obj](input: String): O = {
    this.parseAll(expr, input.trim) match {
      case Success(result, _) => (result `,`).asInstanceOf[O]
      case NoSuccess(y) => throw LanguageException.parseError(
        y._1,
        y._2.source.toString,
        y._2.pos.line.asInstanceOf[java.lang.Integer],
        y._2.pos.column.asInstanceOf[java.lang.Integer])
    }
  }

  lazy val expr: Parser[Obj] = rep1sep(step, opt(".")) ^^ (x => {
    x.flatten.foldLeft[Obj](new __())((a, b) => b.exec(a))
  })

  lazy val aobj: Parser[Obj] = astr | abool | aint | astr
  lazy val abool: Parser[Bool] = ("true" | "false") ^^ (x => bool(x.equals("true")))
  lazy val aint: Parser[Int] = wholeNumber ^^ (x => int(x.toLong))
  lazy val astr: Parser[Str] = """'([^'\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*'""".r ^^ (x => str(x.subSequence(1, x.length - 1).toString))


  lazy val step: Parser[List[Inst[Obj, Obj]]] = "[a-zA-Z]+".r ~ ("(" ~> repsep(aobj, ",") <~ ")") ^^ (x => TraversalMonoid.resolve(x._1, x._2))
}
object GremlinParser {
  def parse[O <: Obj](script: String): O = try {
    new GremlinParser().parse[O](script)
  } catch {
    case e: VmException => throw e
    case e: Exception => {
      e.printStackTrace()
      throw new LanguageException(e.getMessage)
    }
  }
}