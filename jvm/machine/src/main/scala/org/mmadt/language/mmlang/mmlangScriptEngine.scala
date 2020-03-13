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
import org.mmadt.language.Tokens
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.model.Model
import org.mmadt.language.obj.Obj

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmlangScriptEngine(factory:ScriptEngineFactory) extends AbstractScriptEngine with mmADTScriptEngine {
  override def eval(script:String):Obj = mmlangScriptEngine.super.eval(script)
  override def eval(script:String,context:ScriptContext):Obj = mmlangParser.parse[Obj](script,getModel(context.getBindings(ScriptContext.ENGINE_SCOPE)))
  override def eval(script:String,bindings:Bindings):Obj = mmlangParser.parse[Obj](script,getModel(bindings))
  override def eval(reader:Reader,context:ScriptContext):Obj = eval(new BufferedReader(reader).readLine(),context)
  override def eval(reader:Reader):Obj = eval(new BufferedReader(reader).readLine(),new SimpleScriptContext())
  override def createBindings():Bindings = new SimpleBindings()
  override def getFactory:ScriptEngineFactory = factory

  private def getModel(bindings:Bindings):Model = if (bindings.containsKey(Tokens.model)) bindings.get(Tokens.model).asInstanceOf[Model] else Model.id
}

