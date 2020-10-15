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
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{LstValue, Value}
import org.mmadt.language.obj.{Obj, asType}
import org.mmadt.processor.inst.BaseInstTest._
import org.mmadt.storage.StorageFactory.{int, oneObj, strm}
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor5}

import scala.util.matching.Regex

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
abstract class BaseInstTest(testSets:(String, List[Model], TableFor5[Obj, Obj, Result, String, List[(Model, String)]])*) extends FunSuite with TableDrivenPropertyChecks {
  testSets.foreach(testSet => {
    test(testSet._1) {
      val models = testSet._2
      var lastComment:String = Tokens.blank
      forEvery(testSet._3) {
        // ignore comment lines - with comments as "data" it's easier to track which line in the table
        // has failing data
        case (null, null, comment, null, Nil) => lastComment = comment.toString
        case (lhs, rhs, result:Result, query, ignore) => evaluate(lastComment, lhs, rhs, result, query = query, models = models, ignore = ignore)
      }
    }
  })

  private def evaluate(lastComment:String = "", start:Obj, middle:Obj, end:Result, query:String = null, models:List[Model] = Nil, ignore:List[(Model, String)]):Unit = {
    if (models.isEmpty) println(s"WARNING: No models defined for query: $start => $middle")
    models.foreach(model => {
      val querying = List[(String, Obj => Obj)](
        ("query-1", _ => engine.eval(query, bindings(model))),
        ("query-2", _ => engine.eval(query, bindings(model)) match {
          case x:Strm[_] => x // TODO: reconstruct type from a stream
          case x:Value[_] if query.contains(">-") || query.contains("[merge") => x // TODO: not rebuild type up correctly
          case atype:Type[_] => atype.domainObj ==> atype
          case alst:LstValue[_] if alst.named && !alst.isEmpty => alst // nested typing not reconstructing
          case avalue:Value[_] => (avalue.domainObj ==> avalue.trace.reconstruct[Obj](avalue.domainObj, avalue.name)).hardQ(avalue.q)
        })
      )
      val evaluating = List[(String, Obj => Obj)](
        ("eval-1", s => engine.eval(s"$s => $middle", bindings(model))),
        ("eval-2", s => engine.eval(s"$s $middle", bindings(model))),
        ("eval-3", s => s ==> (middle.domain ==> middle)),
        ("eval-4", s => s ==> (middle.domain ==> middle) match {
          case aobj:Obj
            if middle.via.exists(x => List(Tokens.split, Tokens.lift).contains(x._2.op)) ||
              (aobj.isInstanceOf[Strm[_]] && aobj.toStrm.drain.headOption.exists(y => y.via.exists(x => List(Tokens.get).contains(x._2.op)))) => aobj // nested poly have their quantifiers altered
          case atype:Type[_] => atype.domainObj ==> atype
          case astrm:Strm[_] => strm(astrm.drain.map(x => (x.domainObj ==> x.trace.reconstruct[Obj](x.domainObj, x.name)).hardQ(x.q)))
          case alst:LstValue[_] if alst.named && !alst.isEmpty => alst // nested typing not reconstructing
          case avalue:Value[_] => (avalue.domainObj ==> avalue.trace.reconstruct[Obj](avalue.domainObj, avalue.name)).hardQ(avalue.q)
        }),
        ("eval-5", s => {
          val result = s ==> (middle.domain ==> middle)
          if (!middle.trace.nexists(x => List(Tokens.one, Tokens.noop, Tokens.map, Tokens.neg, Tokens.repeat).contains(x._2.op) ||
            (x._2.op.equals(Tokens.lift) || x._2.op.equals(Tokens.plus) && (x._2.arg0[Obj].equals(int(0)) || x._2.arg0[Obj].equals(int(1))))))
            result.trace.modeless.zip((asType(s) ==> middle).trace.modeless).foreach(x => { // test trace of compiled form (not __ form)
              assert(asType(x._1._1).test(x._2._1.rangeObj), s"\n\t${x._1._1} -- ${x._2._1}\n\t\t==>${result.trace + "::" + middle.trace}") // test via tuples' obj
              assertResult(x._1._2.op)(x._2._2.op) // test via tuples' inst opcode
              if (!List(Tokens.split, Tokens.combine).contains(x._1._2.op))
                assert(x._1._2.test(x._2._2), s"\n\t${x._1._2} -- ${x._2._2}\n\t\t==>${x}") // test via tuples' inst
            })
          result
        }),
        ("eval-6", s => {
          val print:Boolean = false
          end match {
            case Left(value) => Some(middle.trace.reconstruct[Obj](s).explain).filter(_ => print).foreach(x => println(x)); value
            case Right(value) => throw value
          }
        }),
        // ("eval-6", s => s ==> (s.range ==> middle)),
        // ("eval-7", s => s ==> (s.range ==> (middle.domain ==> middle))),
      )
      (evaluating ++
        (if (null != query) querying else Nil))
        .foreach(example => {
          if (ignore.exists(i => (i._1 == null || i._1 == model) && (i._2 == null || i._2 == example._1 || new Regex(i._2).pattern.matcher(example._1).matches())))
            println(s"IGNORING[${example._1}][${model.name}]: $start => $middle")
          else
            end match {
              case Left(result:Obj) =>
                assertResult(result, s"[${example._1}][${model.name}] $lastComment")(example._2(prepModel(start, model)))
              case Right(exception:VmException) =>
                assertResult(exception, s"[${example._1}][${model.name}] $lastComment")(intercept[VmException](example._2(prepModel(start, model))))
            }
        })
    })
  }
}
object BaseInstTest {
  type Result = Either[Obj, VmException]
  val engine:mmADTScriptEngine = new mmlangScriptEngineFactory().getScriptEngine
  private val modelEngine:mmADTScriptEngine = new mmlangScriptEngineFactory().getScriptEngine
  def model(model:String):Model = {
    modelEngine.eval(Tokens.:: + oneObj)
    modelEngine.eval(model).asInstanceOf[Model]
  }
  def bindings(model:Model):Bindings = {
    val bindings:Bindings = engine.createBindings()
    bindings.put(Tokens.::, if (null == model) null else __.model(model))
    bindings
  }
  def prepModel(start:Obj, model:Model):Obj = if (null == model) start else start.model(model)
}
