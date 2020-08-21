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

import javax.script.ScriptContext
import org.mmadt.VmException
import org.mmadt.language.Tokens
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.StorageFactory.{asType, qZero}
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor5}

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
abstract class BaseInstTest(testSets: (String, Model, TableFor5[Obj, Obj, Any, String, Boolean])*) extends FunSuite with TableDrivenPropertyChecks {
  protected val engine: mmADTScriptEngine = BaseInstTest.engine

  testSets.foreach(testSet => {
    test(testSet._1) {
      val model = testSet._2
      var lastComment: String = ""
      forEvery(testSet._3) {
        // ignore comment lines - with comments as "data" it's easier to track which line in the table
        // has failing data
        case (null, null, comment, null, false) => lastComment = comment.toString
        case (lhs, rhs, result: Obj, query, compile) => evaluate(lhs, rhs, result, lastComment, query = query, compile = compile, model = model)
        case (lhs, rhs, result: VmException, query, compile) => evaluate(lhs, rhs, result, lastComment, query = query, compile = compile, model = model)
      }
    }
  })

  def prepModel(start: Obj, model: Model): Obj = if (null == model) start else start.model(model)
  def evaluate(start: Obj, middle: Obj, end: Any, lastComment: String = "", inst: Inst[Obj, Obj] = null,
               engine: mmADTScriptEngine = engine, query: String = null, compile: Boolean = true, model: Model = null): Unit = {

    // engine.eval(s":[model,'${getClass.getResource("/model/mm.mm").getPath}']")
    engine.eval(":")
    if (null != model)
      engine.getContext.getBindings(ScriptContext.ENGINE_SCOPE).put(Tokens.::, __.model(model))

    val querying = List[(String, Obj => Obj)](
      ("querying-1", _ => engine.eval(query))
    )
    val evaluating = List[(String, Obj => Obj)](
      ("evaluating-1", s => engine.eval(s"$s => $middle")),
      ("evaluating-2", s => s ==> middle),
      // ("evaluating-3", s => s.compute(middle)), // you have to go through compiler now
      // ("evaluating-4", s => s `=>`l middle) // you have to go through compiler now
    )
    val compiling = List[(String, Obj => Obj)](
      ("compiling-1", s => if (!middle.alive) s.q(qZero) else (asType(s.rangeObj) ==> middle).trace.foldLeft(s)((a, b) => b._2.exec(a))),
      ("compiling-2", s => if (!middle.alive) s.q(qZero) else middle.trace.foldLeft(s)((a, b) => b._2.exec(a))),
      ("compiling-3", s => s `=>` (start.range ==> middle)),
      ("compiling-4", s => s ==> (start.range ==> middle)),
      ("compiling-5", s => s `=>` (middle.domain ==> middle)),
      ("compiling-6", s => s ==> (middle.domain ==> middle)),
      ("compiling-7", s => s `=>` (asType(start.rangeObj) ==> middle)),
      ("compiling-8", s => s ==> (asType(start.rangeObj) ==> middle)))
    val instructioning = List[(String, Obj => Obj)](("instructioning-1", s => inst.exec(s)))

    (evaluating ++
      (if (compile) compiling else Nil) ++
      (if (null != query) querying else Nil) ++
      (if (null != inst) instructioning else Nil))
      .foreach(example => {
        end match {
          case _: Obj => assertResult(end, s"[${example._1}] $lastComment")(example._2(prepModel(start, model)))
          case _: VmException => assertResult(end, s"[${example._1}] $lastComment")(intercept[VmException](example._2(prepModel(start, model))))
        }
      })
    engine.eval(":")

  }
}
object BaseInstTest {
  protected val engine: mmADTScriptEngine = new mmlangScriptEngineFactory().getScriptEngine
  def model(model:String):Model = engine.eval(model).asInstanceOf[Model]
}
