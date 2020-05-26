package org.mmadt.language.gremlin
import org.mmadt.VmException
import org.mmadt.language.LanguageException
import org.mmadt.language.model.Model
import org.mmadt.language.obj.Obj
import org.mmadt.storage.StorageFactory._

import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers

class GremlinParser extends JavaTokenParsers {

  override val whiteSpace: Regex = """[\s\n]+""".r
  override def decimalNumber: Parser[String] = """-?\d+\.\d+""".r

  // all mm-ADT languages must be able to accept a string representation of an expression in the language and return an Obj
  private def parse[O <: Obj](input: String): O = {
    str("smear colorful poop across the canvas. -- gremlin 2020").asInstanceOf[O]
  }
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