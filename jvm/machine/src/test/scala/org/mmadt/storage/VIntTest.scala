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

package org.mmadt.storage

import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`.{IntType, Type}
import org.mmadt.language.obj.{Int, Obj}
import org.mmadt.processor.obj.`type`.CompilingProcessor
import org.mmadt.storage.obj._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VIntTest extends FunSuite {

  test("int values") {
    assertResult(int(3))(int(1) + int(2))
    assertResult(int(-4))(-int(4))
    assertResult(int(-4))(int(3) ==> int.plus(1).neg())
  }

  test("int types") {
    val model = Model.simple()
    //put("nat", int.as[IntType]("nat"), int.is(int.gt(0))) // TODO: structure representation in model
    val processor = new CompilingProcessor[Obj, Obj with Type[_]](model)
    val compiled = processor.apply(int.as("nat"), int.plus(10)).next().obj()
    assertResult("nat<=int[as,nat][plus,10]")(compiled.toString)
    assertResult("nat[60]")((int(50).as[Int]("nat") ==> int.plus(10)).toString)
    assertResult("nat[60]")((int(50) ==> int.as[IntType]("nat").plus(10)).toString)
    assertResult("nat[60]")((int(50).as[Int]("nat") ==> compiled).toString)
    assertResult("nat[60]")((int(50) ==> compiled).toString)
  }

}


