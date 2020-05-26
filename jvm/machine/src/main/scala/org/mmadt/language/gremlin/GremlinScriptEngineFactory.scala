package org.mmadt.language.gremlin
import org.mmadt.language.jsr223.{mmADTScriptEngine, mmADTScriptEngineFactory}

class GremlinScriptEngineFactory extends mmADTScriptEngineFactory {
  override def getLanguageName: String = "gremlin"
  override def getLanguageVersion: String = "0.1-alpha"
  override def getMethodCallSyntax(obj: String, m: String, args: String*): String = obj + "(" + m + "," + args + ")";
  override def getProgram(statements: String*): String = statements.foldLeft("")((a, b) => a + " " + b).trim();
  override def getScriptEngine: mmADTScriptEngine = new GremlinScriptEngine(this)
}
