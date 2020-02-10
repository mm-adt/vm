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

package org.mmadt.processor.obj.`type`

import org.mmadt.language.model.SimpleModel
import org.mmadt.language.obj.`type`.IntType
import org.mmadt.processor.Processor
import org.mmadt.storage.obj._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CompilingProcessorTest extends FunSuite {
  final var processor: Processor[IntType, IntType] = new CompilingProcessor()

  test("compiler w/ linear singleton type") {
    var result: List[IntType] = processor.apply(int, int.mult(int(2))).map(_.obj()).toList
    assertResult(1)(result.length)
    assertResult(int.mult(int(2)))(result.head)
    /////
    result = processor.apply(int, int.mult(int(2)).plus(int(3))).map(_.obj()).toList
    assertResult(1)(result.length)
    assertResult(int.mult(int(2)).plus(int(3)))(result.head)
  }

  test("compiler w/ linear quantified type") {
    var result: List[IntType] = processor.apply(int.q(int(2)), int.mult(int(2))).map(_.obj()).toList
    assertResult(1)(result.length)
    assertResult(int.q(int(2)).mult(int(2)))(result.head)
    assertResult(int.q(int(2)) <= int.q(int(2)).mult(int(2)))(result.head)
    /////
    result = processor.apply(int.q(2), int.mult(int(2)).plus(int(3))).map(_.obj()).toList
    assertResult(1)(result.length)
    assertResult(int.q(int(2)).mult(int(2)).plus(int(3)))(result.head)
    assertResult(int.q(int(2)) <= int.q(int(2)).mult(int(2)).plus(int(3)))(result.head)
    /////
    result = processor.apply(int.q(int(2)), int.mult(int(2)).is(int.gt(int(2)))).map(_.obj()).toList
    assertResult(1)(result.length)
    // int{0,2}<=int{2}[mult,2][is,bool{2}<=int{2}[gt,2]]
    assertResult(int.q(0, 2) <= int.q(2).mult(2).is(bool.q(2) <= int.q(2).gt(2)))(result.head)
  }

  test("compiler w/ linear quantified type and model") {
    processor = new CompilingProcessor(new SimpleModel().put(int, int.mult(2), int.plus(int)).put(int,int.plus(0),int))
    var result: List[IntType] = processor.apply(int, int.mult(int(2))).map(_.obj()).toList
    assertResult(1)(result.length)
    assertResult(int.plus(int))(result.head)
    /////
    result = processor.apply(int,int.plus(0)).map(_.obj()).toList
    assertResult(1)(result.length)
    assertResult(int)(result.head)
  }
}