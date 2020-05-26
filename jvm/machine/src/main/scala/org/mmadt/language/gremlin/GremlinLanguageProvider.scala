package org.mmadt.language.gremlin
import java.util.{Optional, ServiceLoader}

import javax.script.{Bindings, ScriptEngineManager, SimpleBindings}
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.model.Model
import org.mmadt.language.{LanguageProvider, Tokens}
import org.mmadt.storage.StorageProvider

import scala.collection.JavaConverters.asScalaIterator

trait GremlinLanguageProvider extends LanguageProvider {
  override val name: String = GremlinLanguageProvider._name
  override val model: Model = Model.id
  override def getEngine: Optional[mmADTScriptEngine] = Optional.of(GremlinLanguageProvider.scriptEngine())
}

object GremlinLanguageProvider {
  private val _name: String = "gremlin"
  private lazy val scriptEngineManager: ScriptEngineManager = {
    val model: Model = asScalaIterator(ServiceLoader.load(classOf[StorageProvider]).iterator()).toSeq.map(x => x.model()).headOption.getOrElse(Model.id)
    val manager: ScriptEngineManager = new ScriptEngineManager() // want to constrain the manager to only accessing mmADTScriptEngines
    manager.setBindings(bindings(Tokens.model -> model))
    manager
  }
  private def scriptEngine(): mmADTScriptEngine = scriptEngineManager.getEngineByName(_name).asInstanceOf[GremlinScriptEngine]

  private def bindings(pairs: Tuple2[String, Any]*): Bindings = {
    val bindings: Bindings = new SimpleBindings()
    pairs.foreach(s => bindings.put(s._1, s._2))
    bindings
  }
}

