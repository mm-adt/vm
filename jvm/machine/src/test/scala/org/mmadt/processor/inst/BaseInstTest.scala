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

package org.mmadt.processor.inst

import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.StorageFactory.{asType, zeroObj, _}
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor5}

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
abstract class BaseInstTest(testSets: (String, TableFor5[Obj, Obj, Obj, String, Boolean])*) extends FunSuite with TableDrivenPropertyChecks {
  protected val engine: mmADTScriptEngine = new mmlangScriptEngineFactory().getScriptEngine

  testSets.foreach(testSet => {
    test(testSet._1) {
      var lastComment: String = ""
      forEvery(testSet._2) {
        // ignore comment lines - with comments as "data" it's easier to track which line in the table
        // has failing data
        case (null, null, comment, null, false) => lastComment = comment.toString
        case (lhs, rhs, result, query, c) => evaluate(lhs, rhs, result, lastComment, query = query, compile = c)
      }
    }
  })

  def stringify(obj: Obj): String = if (obj.isInstanceOf[Strm[_]]) {
    if (!obj.alive)
      zeroObj.toString
    else
      obj.toStrm.values.foldLeft("[")((a, b) => a.concat(b + ",")).dropRight(1).concat("]")
  } else obj.toString


  def evaluate(start: Obj, middle: Obj, end: Obj, lastComment: String = "", inst: Inst[Obj, Obj] = null,
               engine: mmADTScriptEngine = engine, query: String = null, compile: Boolean = true): Unit = {
    engine.eval(":")
    val querying = List[Obj => Obj](
      _ => engine.eval(query)
    )
    val evaluating = List[Obj => Obj](
      s => engine.eval(s"${stringify(s)} => ${middle}"),
      s => s.compute(middle),
      s => s ==> middle,
      s => s `=>` middle,
    )
    val compiling = List[Obj => Obj](
      s => if (!middle.alive) s.q(qZero) else (asType(s.rangeObj) ==> middle).trace.foldLeft(s)((a, b) => b._2.exec(a)),
      s => if (!middle.alive) s.q(qZero) else middle.trace.foldLeft(s)((a, b) => b._2.exec(a)),
      s => s `=>` (start.range ==> middle),
      s => s ==> (start.range ==> middle),
      s => s `=>` (middle.domain ==> middle),
      s => s ==> (middle.domain ==> middle),
      s => s `=>` (asType(start.rangeObj) ==> middle),
      s => s ==> (asType(start.rangeObj) ==> middle))
    val instructioning = List[Obj => Obj](s => inst.exec(s))
    (evaluating ++
      (if (compile) compiling else Nil) ++
      (if (null != query) querying else Nil) ++
      (if (null != inst) instructioning else Nil))
      .foreach(example => assertResult(end, lastComment)(example(start)))
  }
}
