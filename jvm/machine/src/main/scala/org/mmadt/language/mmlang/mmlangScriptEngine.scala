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

import java.io.{BufferedReader, Reader}

import javax.script._
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.model.Model
import org.mmadt.language.obj.Obj

import scala.collection.JavaConverters._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmlangScriptEngine extends AbstractScriptEngine with mmADTScriptEngine {
  override def eval(script:String):java.util.Iterator[Obj] = mmlangScriptEngine.super.eval(script)
  override def eval(script:String,context:ScriptContext):java.util.Iterator[Obj] = asJavaIterator(mmlangParser.parse[Obj](script,context.getBindings(ScriptContext.ENGINE_SCOPE).get("model").asInstanceOf[Model]))
  override def eval(reader:Reader,context:ScriptContext):java.util.Iterator[Obj] = eval(new BufferedReader(reader).readLine(),context)
  override def eval(reader:Reader):java.util.Iterator[Obj] = eval(new BufferedReader(reader).readLine(),this.context)
  override def createBindings():Bindings = new SimpleBindings
  override def getFactory:ScriptEngineFactory = new mmlangScriptEngineFactory
}

