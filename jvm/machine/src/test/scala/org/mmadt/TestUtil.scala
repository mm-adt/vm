/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */
package org.mmadt

import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, Obj, asType}
import org.mmadt.storage.StorageFactory.zeroObj
import org.scalatest.Matchers.assertResult

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object TestUtil {

  private val engine:mmADTScriptEngine = new mmlangScriptEngineFactory().getScriptEngine

  def stringify(obj:Obj):String = if (obj.isInstanceOf[Strm[_]]) {
    if (!obj.alive)
      zeroObj.toString
    else
      obj.toStrm.drain.foldLeft("[")((a, b) => a.concat(b + ",")).dropRight(1).concat("]")
  } else obj.toString

  def evaluate(start:Obj, middle:Obj, end:Obj, inst:Inst[Obj, Obj] = null, engine:mmADTScriptEngine = engine, compile:Boolean = true):Unit = {
    engine.eval(":")
    val evaluating = List[Obj => Obj](
      s => engine.eval(s"${stringify(s)} => ${middle}"),
      s => s.compute(middle),
      s => s =>> middle,
      s => s ==> middle,
    )
    val compiling = List[Obj => Obj](
      s => (asType(s.rangeObj) =>> middle).trace.foldLeft(s)((a, b) => b._2.exec(a)))
    val instructioning = List[Obj => Obj](s => inst.exec(s))
    (evaluating ++
      (if (compile) compiling else Nil) ++
      (if (null != inst) instructioning else Nil))
      .foreach(example => assertResult(end)(example(start)))
  }
}

