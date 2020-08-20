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
import org.mmadt.storage.StorageFactory.{int, str}
import org.scalatest.prop.TableFor5

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
object TestSetUtil {

  val marko: Rec[StrValue, Obj] = (str("name") -> str("marko")) `,` (str("age") -> int(29))
  val vadas: Rec[StrValue, Obj] = (str("name") -> str("vadas")) `,` (str("age") -> int(27))
  val person: Rec[StrValue, Obj] = (str("name") -> str) `,` (str("age") -> int)
  val oldPerson: Rec[StrValue, Obj] = (str("name") -> str) `,` (str("age") -> int.is(int.gt(28)))
  val youngPerson: Rec[StrValue, Obj] = str("name") -> str `_,` str("age") -> __.is(__.lt(28))
  val car: Rec[StrValue, Obj] = (str("name") -> str) `,` (str("year") -> int)
  val alst: Lst[StrValue] = str("a") `,` "b"

  def testSet(testName: String, model: Model, data: (Obj, Obj, Any, String, Boolean)*): (String, Model, TableFor5[Obj, Obj, Any, String, Boolean]) =
    (testName, model, new TableFor5[Obj, Obj, Any, String, Boolean](("lhs", "rhs", "result", "query", "compile"), data: _*))
  def testSet(testName: String, data: (Obj, Obj, Any, String, Boolean)*): (String, Model, TableFor5[Obj, Obj, Any, String, Boolean]) =
    (testName, null, new TableFor5[Obj, Obj, Any, String, Boolean](("lhs", "rhs", "result", "query", "compile"), data: _*))

  def testing(lhs: Obj, rhs: Obj, result: Obj, compile: Boolean = true): (Obj, Obj, Obj, String, Boolean) = (lhs, rhs, result, null, compile)
  def testing(lhs: Obj, rhs: Obj, result: Obj, query: String): (Obj, Obj, Obj, String, Boolean) = (lhs, rhs, result, query, false)
  def testing(lhs: Obj, rhs: Obj, result: VmException, query: String): (Obj, Obj, VmException, String, Boolean) = (lhs, rhs, result, query, false)

  def comment(comment: String): (Obj, Obj, Obj, String, Boolean) = (null, null, str(comment), null, false)
}


