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

import java.util.{Optional, ServiceLoader}

import javax.script.{Bindings, ScriptEngineManager, SimpleBindings}
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.model.Model
import org.mmadt.language.{LanguageProvider, Tokens}
import org.mmadt.storage.StorageProvider

import scala.collection.JavaConverters.asScalaIterator

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmlangLanguageProvider extends LanguageProvider {
  override val name :String = mmlangLanguageProvider._name
  override val model:Model  = Model.id
  override def getEngine:Optional[mmADTScriptEngine] = Optional.of(mmlangLanguageProvider.scriptEngine())
}

object mmlangLanguageProvider {
  private      val _name              :String              = "mmlang"
  private lazy val scriptEngineManager:ScriptEngineManager = {
    val model  :Model               = asScalaIterator(ServiceLoader.load(classOf[StorageProvider]).iterator()).toSeq.map(x => x.model()).headOption.getOrElse(Model.id)
    val manager:ScriptEngineManager = new ScriptEngineManager()
    manager.setBindings(bindings(Tokens.model -> model))
    manager
  }
  private def scriptEngine():mmADTScriptEngine = scriptEngineManager.getEngineByName(_name).asInstanceOf[mmlangScriptEngine]

  private def bindings(pairs:Tuple2[String,Any]*):Bindings ={
    val bindings:Bindings = new SimpleBindings()
    pairs.foreach(s => bindings.put(s._1,s._2))
    bindings
  }
}

