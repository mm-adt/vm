package org.mmadt.language.gremlin
import java.io.Reader

import javax.script.{AbstractScriptEngine, Bindings, ScriptContext, ScriptEngineFactory}
import org.mmadt.language.Tokens
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.model.Model
import org.mmadt.language.obj.Obj

class GremlinScriptEngine(factory: GremlinScriptEngineFactory) extends AbstractScriptEngine with mmADTScriptEngine {
  override def eval(script: String): Obj = super.eval(script)
  override def eval(reader: Reader): Obj = super.eval(reader)
  override def eval(script: String, context: ScriptContext): Obj = super.eval(script, context)
  override def eval(script: String, bindings: Bindings): Obj = GremlinParser.parse[Obj](script, getModel(bindings))
  override def getFactory: ScriptEngineFactory = factory
  private def getModel(bindings: Bindings): Model = if (bindings.containsKey(Tokens.model)) bindings.get(Tokens.model).asInstanceOf[Model] else Model.id
}

