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

import javax.script.Bindings
import org.mmadt.VmException
import org.mmadt.language.Tokens
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.processor.inst.BaseInstTest.{Result, bindings, engine, prepModel}
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor4}

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
abstract class BaseInstTest(testSets: (String, Model, TableFor4[Obj, Obj, Result, String])*) extends FunSuite with TableDrivenPropertyChecks {
  testSets.foreach(testSet => {
    test(testSet._1) {
      val model = testSet._2
      var lastComment: String = ""
      forEvery(testSet._3) {
        // ignore comment lines - with comments as "data" it's easier to track which line in the table
        // has failing data
        case (null, null, comment, null) => lastComment = comment.toString
        case (lhs, rhs, result: Result, query) => evaluate(lastComment, lhs, rhs, result, query = query, model = model)
      }
    }
  })

  private def evaluate(lastComment: String = "", start: Obj, middle: Obj, end: Result, query: String = null, model: Model = null): Unit = {
    val querying = List[(String, Obj => Obj)](
      ("query-1", _ => engine.eval(query, bindings(model)))
    )
    val evaluating = List[(String, Obj => Obj)](
      ("eval-1", s => engine.eval(s"$s => $middle", bindings(model))),
      ("eval-2", s => engine.eval(s"$s $middle", bindings(model))),
      ("eval-3", s => s ==> (middle.domain ==> middle)),
      ////////// WITH MODELS THERE ARE TOO MANY WAYS IN WHICH TO EVALUATE A QUERY (NEED TO RESTRICT THIS)
      //("compiling-1", s => if (!middle.alive) s.q(qZero) else (asType(s.rangeObj) ==> middle).trace.foldLeft(s)((a, b) => b._2.exec(a))),
      //("compiling-2", s => if (!middle.alive) s.q(qZero) else middle.trace.foldLeft(s)((a, b) => b._2.exec(a))),
      //("compiling-3", s => s `=>` (s.range ==> middle)),
      //("compiling-4", s => s ==> (s.range ==> middle)),
      //("compiling-5", s => s `=>` (middle.domain ==> middle)),
      //("compiling-7", s => s `=>` (asType(s.rangeObj) ==> middle)),
      //("compiling-8", s => s ==> (s.range `=>` middle)))
    )
    (evaluating ++
      (if (null != query) querying else Nil))
      .foreach(example => {
        end match {
          case Left(result: Obj) =>
            assertResult(result, s"[${example._1}] $lastComment")(example._2(prepModel(start, model)))
          case Right(exception: VmException) =>
            assertResult(exception, s"[${example._1}] $lastComment")(intercept[VmException](example._2(prepModel(start, model))))
        }
      })
  }

}
object BaseInstTest {
  type Result = Either[Obj, VmException]
  val engine: mmADTScriptEngine = new mmlangScriptEngineFactory().getScriptEngine
  private val modelEngine: mmADTScriptEngine = new mmlangScriptEngineFactory().getScriptEngine
  def model(model: String): Model = {
    modelEngine.eval(":")
    modelEngine.eval(model).asInstanceOf[Model]
  }
  def bindings(model: Model): Bindings = {
    val bindings: Bindings = engine.createBindings()
    bindings.put(Tokens.::, if (null == model) null else __.model(model))
    bindings
  }
  def prepModel(start: Obj, model: Model): Obj = if (null == model) start else start.model(model)
}
