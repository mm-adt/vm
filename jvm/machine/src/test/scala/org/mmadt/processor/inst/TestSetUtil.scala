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

import org.mmadt.language.obj.Obj
import org.mmadt.storage.StorageFactory.str
import org.scalatest.prop.TableFor5

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
object TestSetUtil {

  def testSet(testName: String, data: (Obj, Obj, Obj, String, Boolean)*): (String, TableFor5[Obj, Obj, Obj, String, Boolean]) =
    (testName, new TableFor5[Obj, Obj, Obj, String, Boolean](("lhs", "rhs", "result", "query", "compile"), data: _*))


  def testing(lhs: Obj, rhs: Obj, result: Obj, compile: Boolean = true): (Obj, Obj, Obj, String, Boolean) = (lhs, rhs, result, null, compile)
  def testing(lhs: Obj, rhs: Obj, result: Obj, query: String): (Obj, Obj, Obj, String, Boolean) = (lhs, rhs, result, query, false)

  def comment(comment: String): (Obj, Obj, Obj, String, Boolean) = (null, null, str(comment), null, false)
}


