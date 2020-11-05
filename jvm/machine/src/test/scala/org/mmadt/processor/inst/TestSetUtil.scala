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

import org.mmadt.VmException
import org.mmadt.language.obj.Obj.tupleToRecYES
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Lst, Obj, Rec}
import org.mmadt.processor.inst.BaseInstTest.Result
import org.mmadt.storage.StorageFactory.{int, str}
import org.scalatest.prop.TableFor5

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
object TestSetUtil {

  val marko:Rec[StrValue, Obj] = (str("name") -> str("marko")) `,`(str("age") -> int(29))
  val vadas:Rec[StrValue, Obj] = (str("name") -> str("vadas")) `,`(str("age") -> int(27))
  val person:Rec[StrValue, Obj] = (str("name") -> str) `,`(str("age") -> int)
  val oldPerson:Rec[StrValue, Obj] = (str("name") -> str) `,`(str("age") -> int.is(int.gt(28)))
  val youngPerson:Rec[StrValue, Obj] = str("name") -> str `_,` str("age") -> __.is(__.lt(28))
  val car:Rec[StrValue, Obj] = (str("name") -> str) `,`(str("year") -> int)
  val alst:Lst[StrValue] = str("a") `,` "b"

  def testSet(testName:String, models:List[Model], data:(Obj, Obj, Result, List[String], List[(Model, String)])*):(String, List[Model], TableFor5[Obj, Obj, Result, List[String], List[(Model, String)]]) =
    (testName, models, new TableFor5[Obj, Obj, Result, List[String], List[(Model, String)]](("lhs", "rhs", "result", "query", "ignore"), data:_*))
  def testSet(testName:String, model:Model, data:(Obj, Obj, Result, List[String], List[(Model, String)])*):(String, List[Model], TableFor5[Obj, Obj, Result, List[String], List[(Model, String)]]) =
    (testName, List(model), new TableFor5[Obj, Obj, Result, List[String], List[(Model, String)]](("lhs", "rhs", "result", "query", "ignore"), data:_*))
  def testSet(testName:String, data:(Obj, Obj, Result, List[String], List[(Model, String)])*):(String, List[Model], TableFor5[Obj, Obj, Result, List[String], List[(Model, String)]]) =
    (testName, Nil, new TableFor5[Obj, Obj, Result, List[String], List[(Model, String)]](("lhs", "rhs", "result", "query", "ignore"), data:_*))

  def testing(lhs:Obj, rhs:Obj, result:Obj):(Obj, Obj, Result, List[String], List[(Model, String)]) = (lhs, rhs, Left(result), Nil, Nil)
  def testing(lhs:Obj, rhs:Obj, result:Obj, query:String):(Obj, Obj, Result, List[String], List[(Model, String)]) = (lhs, rhs, Left(result), List(query), Nil)
  def excepting(lhs:Obj, rhs:Obj, result:VmException, query:String = null):(Obj, Obj, Result, List[String], List[(Model, String)]) = (lhs, rhs, Right(result), Option(query).map(x=>List(x)).getOrElse(Nil), Nil)
  //def IGNORING(ignore:Model)(lhs:Obj, rhs:Obj, result:VmException, query:String = null):(Obj, Obj, Result, String, List[(Model, String)]) = (lhs, rhs, Right(result), query, List((ignore,null)))
  def IGNORING(ignore:Model)(lhs:Obj, rhs:Obj, result:Obj, query:String):(Obj, Obj, Result, List[String], List[(Model, String)]) = (lhs, rhs, Left(result), List(query), List((ignore, null)))
  def IGNORING(ignore:List[(Model, String)])(lhs:Obj, rhs:Obj, result:Obj, query:String):(Obj, Obj, Result, List[String], List[(Model, String)]) = (lhs, rhs, Left(result), List(query), ignore)
  def IGNORING(ignore:String*)(lhs:Obj, rhs:Obj, result:Obj, query:String*):(Obj, Obj, Result, List[String], List[(Model, String)]) = (lhs, rhs, Left(result), query.toList, ignore.toList.map(x => (null, x)))

  def comment(comment:String):(Obj, Obj, Result, List[String], List[(Model, String)]) = (null, null, Left(str(comment)), Nil, Nil)
}


