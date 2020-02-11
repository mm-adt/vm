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

package org.mmadt.language.obj.`type`

import org.mmadt.language.model.SimpleModel
import org.mmadt.language.obj.Str
import org.mmadt.processor.obj.value.IteratorChainProcessor
import org.mmadt.storage.obj.{int, rec, str}
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class PersonTest extends FunSuite {

  test("person rec") {
    val marko = rec(str("name") -> str("marko"), str("age") -> int(29))
    assertResult("['name':'marko','age':29]")(marko.toString)
    assertResult("person['name':'marko','age':29]")(marko.as("person").toString)
    assertResult(str("marko"))(IteratorChainProcessor(marko, rec[Str, Str].get[StrType]("name", str)).next().obj())
    assertResult(int(29))(IteratorChainProcessor(marko, rec[Str, Str].get[StrType]("age", str)).next().obj())
  }

  /*test("person compilation") {
    val marko = new TRec[Str, Obj](Map(str("name") -> str("marko"), str("age") -> int(29)), Nil, qOne).as("person")
    val model = new SimpleModel().
      put(int, int.mult(2), int.plus(int)).
      put(int, int.plus(0), int).
      typePut("person", rec[Str, Str].get[StrType]("firstname", str), rec[Str, Str].get[StrType]("name", str))
    val processor = new CompilingProcessor[Obj, Obj](model)
    println(model)
    println(processor.apply(marko, rec[Str, Str].get[StrType]("firstname", str)).toList)
  }*/

  test("person evaluation") {
    val marko = rec(str("name") -> str("marko"), str("age") -> int(29)).as("person")
    val model = new SimpleModel().
      put(int, int.mult(2), int.plus(int)).
      put(int, int.plus(0), int).
      typePut("person", rec.get(str("firstname"), str), rec.get(str("name"), str))
    println(model)
  }


}

