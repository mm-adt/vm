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

package org.mmadt.storage.obj.value

import org.mmadt.language.model.Model
import org.mmadt.language.obj.Obj
import org.mmadt.processor.obj.`type`.CompilingProcessor
import org.mmadt.storage.obj._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VStrTest extends FunSuite {

  test("str values") {
    assertResult(str("mar"))(str("m").plus("a").plus("r"))
    assertResult(btrue)(str("m").gt(str("a")))
    assertResult(bfalse)(str("m").gt(str("r")))
  }

  test("type names on str") {
    assertResult("address['103 P.V.']")(str("103 P.V.").as("address").toString)
  }

  test("type names on model") {
    val model = Model.simple().
      put(str.plus(str("a")), str.plus(str("b")))
    val processor = new CompilingProcessor[Obj, Obj]()
    // println(str ==> str.plus("a").is(str.gt("bb")))
    assertResult("address{?}<=str[as,address][plus,'ed'][is,bool<=address[gt,'xx']]")(processor.apply(str.as("address"), str.plus("ed").is(str.gt("xx"))).next().obj().toString)
  }
}