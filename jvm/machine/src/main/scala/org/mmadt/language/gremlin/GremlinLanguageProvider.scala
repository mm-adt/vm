package org.mmadt.language.gremlin
import java.util.Optional

import javax.script.{Bindings, ScriptEngineManager, SimpleBindings}
import org.mmadt.language.LanguageProvider
import org.mmadt.language.jsr223.mmADTScriptEngine

class GremlinLanguageProvider extends LanguageProvider {
  override val name: String = GremlinLanguageProvider._name
  override def getEngine: Optional[mmADTScriptEngine] = Optional.of(GremlinLanguageProvider.scriptEngine())
}

object GremlinLanguageProvider {
  private val _name: String = "gremlin"
  private lazy val scriptEngineManager: ScriptEngineManager = {
    val manager: ScriptEngineManager = new ScriptEngineManager() // want to constrain the manager to only accessing mmADTScriptEngines
    manager
  }
  private def scriptEngine(): mmADTScriptEngine = scriptEngineManager.getEngineByName(_name).asInstanceOf[GremlinScriptEngine]

  private def bindings(pairs: Tuple2[String, Any]*): Bindings = {
    val bindings: Bindings = new SimpleBindings()
    pairs.foreach(s => bindings.put(s._1, s._2))
    bindings
  }
}

