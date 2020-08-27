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
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.value.Value
import org.mmadt.processor.inst.BaseInstTest._
import org.mmadt.storage.StorageFactory.int
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor4}

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 * @author Marko A. Rodriguez (http://markorodriguez.com)
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
      ("query-1", _ => engine.eval(query, bindings(model))),
      ("query-2", _ => engine.eval(query, bindings(model)) match {
        case atype: Type[_] => atype.domainObj ==> atype
        case avalue: Value[_] => avalue.domainObj ==> reconstructPath(avalue)
      })
    )
    val evaluating = List[(String, Obj => Obj)](
      ("eval-1", s => engine.eval(s"$s => $middle", bindings(model))),
      ("eval-2", s => engine.eval(s"$s $middle", bindings(model))),
      ("eval-3", s => s ==> (middle.domain ==> middle)),
      ("eval-4", s => s ==> (middle.domain ==> middle) match {
        case aobj: Obj if org.mmadt.language.obj.op.rewrite.exists(middle, Tokens.split) => aobj
        case atype: Type[_] => atype.domainObj ==> atype
        case avalue: Value[_] => avalue.domainObj ==> reconstructPath(avalue)
      }),
      ("eval-5", s => {
        val result = s ==> (middle.domain ==> middle)
        if (!middle.trace.exists(x => List(Tokens.one, Tokens.map, Tokens.neg).contains(x._2.op) || (x._2.op.equals(Tokens.plus) && x._2.arg0[Obj].equals(int(0)))))
          result.trace.modeless.drop(1).zip(middle.trace.modeless).foreach(x => {
            assert(x._1._1.test(x._2._1), s"${x._1._1} -- ${x._2._1}")
            assertResult(x._1._2.op)(x._2._2.op)
            // x._1._2.args.zip(x._2._2.args).headOption.map(y => assert(y._1.test(y._2), s"${x._1._2} -- ${x._2._2}"))
          })
        result
      }),
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
    modelEngine.eval(Tokens.::)
    modelEngine.eval(model).asInstanceOf[Model]
  }
  def bindings(model: Model): Bindings = {
    val bindings: Bindings = engine.createBindings()
    bindings.put(Tokens.::, if (null == model) null else __.model(model))
    bindings
  }
  def prepModel(start: Obj, model: Model): Obj = if (null == model) start else start.model(model)
  def reconstructPath(obj: Obj): Obj = obj.trace.map(x => x._2).foldLeft(obj.domain.asInstanceOf[Obj])((a, b) => b.exec(a))
}
