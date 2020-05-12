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

package org.mmadt.processor.inst.model

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.value.IntValue
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ModelInstTest extends FunSuite {
  val modelA: RecType[Type[Obj], Type[Obj]] = trec((int <= int.is(int.gt(0))) -> int.named("nat"))
  val modelB: RecType[Type[Obj], Type[Obj]] = trec(int.named("nat") -> int)

  test("[model] w/ values") {
    assertResult("nat")(int(5).model[IntValue](modelA).name)
    assertResult(5)(int(5).model[IntValue](modelA).g)
    //
    assertResult("int")(int(5).model[IntValue](modelA).plus(1).model[IntValue](modelB).name)
    assertResult(6)(int(5).model[IntValue](modelA).plus(1).model[IntValue](modelB).g)

  }
  test("[map] w/ types") {
    //    assertResult("int[plus,1][model,rec[int[is,bool<=int[gt,0]]:nat]]")(int.plus(1).model(modelA).toString)
    //   assertResult("int[plus,1][model,rec[int[is,bool<=int[gt,0]]:nat]][plus,10][model,rec[nat:int]]")(int.plus(1).model[Int](modelA).plus(10).model(modelB).toString)
  }

  test("[model] as functor") {
    val functor: RecType[Type[Obj], Type[Obj]] = trec(int.mult(10) -> str.plus("0"), int.mult(1) -> str)
    //    assertResult(str("32002"))(str("32") ===> (int.mult(1).mult(10).mult(10).model[StrType](functor).plus("2")))
  }
}