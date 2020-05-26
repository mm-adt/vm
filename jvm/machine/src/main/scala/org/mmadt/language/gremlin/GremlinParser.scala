package org.mmadt.language.gremlin
import org.mmadt.VmException
import org.mmadt.language.LanguageException
import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.{Inst, Obj}
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

  lazy val step: Parser[List[Inst[Obj, Obj]]] = "[a-zA-Z]+".r ~ opt("('" ~> "[a-zA-Z]+".r <~ "')") ^^ (x => {
    TraversalMonoid.resolve(x._1, x._2.map(y => List(str(y))).getOrElse(List.empty[Obj]))
  })
}
object GremlinParser {
  def parse[O <: Obj](script: String, model: Model): O = try {
    new GremlinParser().parse[O](script)
  } catch {
    case e: VmException => throw e
    case e: Exception => {
      e.printStackTrace()
      throw new LanguageException(e.getMessage)
    }
  }
}