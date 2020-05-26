package org.mmadt.language.gremlin
import java.io.{BufferedReader, Reader}

import javax.script.{AbstractScriptEngine, Bindings, ScriptContext, ScriptEngineFactory}
import org.mmadt.language.Tokens
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.model.Model
import org.mmadt.language.obj.Obj

class GremlinScriptEngine(factory: GremlinScriptEngineFactory) extends AbstractScriptEngine with mmADTScriptEngine {
  override def eval(script: String): Obj = super.eval(script)
  override def eval(script: String, context: ScriptContext): Obj = GremlinParser.parse[Obj](script, getModel(context))
  override def eval(script: String, bindings: Bindings): Obj = GremlinParser.parse[Obj](script, getModel(bindings))
  override def eval(reader: Reader, context: ScriptContext): Obj = eval(new BufferedReader(reader).readLine(), context)
  override def eval(reader: Reader): Obj = eval(new BufferedReader(reader).readLine(), this.getContext)
  override def getFactory: ScriptEngineFactory = factory

  private def getModel(bindings: Bindings): Model = if (bindings.containsKey(Tokens.model)) bindings.get(Tokens.model).asInstanceOf[Model] else Model.id
  private def getModel(context: ScriptContext): Model = Option(context.getAttribute(Tokens.model).asInstanceOf[Model]).getOrElse(Model.id)
}

