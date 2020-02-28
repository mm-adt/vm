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

import org.mmadt.language.model.Model
import org.mmadt.language.obj.{Bool, TypeObj,Int}
import org.mmadt.language.obj.`type`.{BoolType, IntType, Type}
import org.mmadt.processor.Processor
import org.mmadt.storage.obj._
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CompilingProcessorTest extends FunSuite with TableDrivenPropertyChecks with Matchers {
  final var processor:Processor = new CompilingProcessor()

  test("compiler w/ linear singleton type"){
    var result:List[Int] = processor.apply(int,int.mult(int(2))).map(_.obj()).toList
    assertResult(1)(result.length)
    assertResult(int.mult(int(2)))(result.head)
    /////
    result = processor.apply(int,int.mult(int(2)).plus(int(3))).map(_.obj()).toList
    assertResult(1)(result.length)
    assertResult(int.mult(int(2)).plus(int(3)))(result.head)
  }

  test("compiler w/ linear quantified type"){
    var result:List[Int] = processor.apply(int.q(int(2)),int.mult(int(2))).map(_.obj()).toList
    assertResult(1)(result.length)
    assertResult(int.q(int(2)).mult(int(2)))(result.head)
    assertResult(int.q(int(2)) <= int.q(int(2)).mult(int(2)))(result.head)
    /////
    result = processor.apply(int.q(2),int.mult(int(2)).plus(int(3))).map(_.obj()).toList
    assertResult(1)(result.length)
    assertResult(int.q(int(2)).mult(int(2)).plus(int(3)))(result.head)
    assertResult(int.q(int(2)) <= int.q(int(2)).mult(int(2)).plus(int(3)))(result.head)
    /////
    result = processor.apply(int.q(int(2)),int.mult(int(2)).is(int.gt(int(2)))).map(_.obj()).toList
    assertResult(1)(result.length)
    // int{0,2}<=int{2}[mult,2][is,bool{2}<=int{2}[gt,2]]
    assertResult(int.q(0,2) <= int.q(2).mult(2).is(bool.q(2) <= int.q(2).gt(2)))(result.head)
  }

  test("compiler w/ linear quantified type and model"){
    processor = new CompilingProcessor(
      Model.simple().
        put(int.mult(2),int.plus(int)).
        put(int.plus(0),int).
        put(int.plus(1).plus(-1),int))

    /////
    forAll(Table(
      "int reductions",
      int,
      int.plus(0),
      int.plus(0).plus(0),
      int.plus(1).plus(-1),
      int.plus(1).plus(-1).plus(0),
      int.plus(0).plus(1).plus(-1).plus(0),
      int.plus(1).plus(-1).plus(0).plus(1).plus(-1),
      int.plus(1).plus(-1).plus(0).plus(0).plus(1).plus(-1),
      int.plus(1).plus(-1).plus(0).plus(1).plus(0).plus(-1).plus(0),
      int.plus(0).plus(1).plus(-1).plus(0).plus(1).plus(0).plus(-1).plus(0),
      int.plus(0).plus(1).plus(-1).plus(0).plus(0).plus(1).plus(-1).plus(0).plus(0),
      int.plus(1).plus(1).plus(-1).plus(0).plus(0).plus(-1).plus(1).plus(0).plus(-1).plus(0),
      int.plus(1).plus(1).plus(-1).plus(0).plus(0).plus(-1).plus(1).plus(0).plus(-1).plus(1).plus(-1))){
      i =>
        val result = processor.apply(int,i).map(_.obj()).toList
        assertResult(1)(result.length)
        assertResult(int)(result.head)
    }
  }
  test("compiler w/ model"){
    processor = new CompilingProcessor(
      Model.simple().
        put(int.plus(int),int.mult(int(2))).
        put(int.mult(int(2)).mult(int(2)),int.mult(int(4))). // TODO: mult(x).mult(x) -> mult(x.mult(2))   (variables in patterns)
        put(int.plus(int(1)).plus(int(-1)),int)) // TODO: plus(x).plus(-1) -> id
    /////
    assertResult(int.mult(2))(processor.apply(int,int.plus(int)).next().obj())
    assertResult(int.mult(4))(processor.apply(int,int.plus(int).mult(int(2))).next().obj())
  }

  test("compiler w/ [choose]"){
    processor = new CompilingProcessor()
    var result:List[Int] = processor.apply(int,int.mult(1).choose(int.is(int.gt(5)) -> int.plus(2),int -> int.plus(1)).is(int.gt(3))).map(_.obj()).toList
    assertResult(1)(result.length)
    assertResult("int{?}<=int[mult,1][choose,[int{?}<=int[is,bool<=int[gt,5]]->int[plus,2]|int->int[plus,1]]][is,bool<=int[gt,3]]")(result.head.toString)
  }

  test("compiler w/ multi-types"){
    val processor:Processor = new CompilingProcessor(
      Model.simple().
        put(int.plus(int(0)),int).
        put(int.gt(int(0)),int.eqs(int(0))))

    assertResult(int.eqs(int(0)))(processor.apply(int.plus(int(0)).gt(int(0))))
    assertResult(int.gt(int(20)))(processor.apply(int.plus(int(0)).gt(int(20))))
    assertResult(int.plus(int(10)).gt(int(20)))(processor.apply(int.plus(int(10)).gt(int(20))))
    assertResult(int.plus(int(10)).gt(int(20)).and(bool))(processor.apply(int.plus(int(10)).gt(int(20)).and(bool)))
    assertResult(int.plus(int(10)).gt(int(20)).and(bool))(processor.apply(int.plus(int(0)).plus(int(10)).plus(int(0)).gt(int(20)).and(bool)))
  }

  test("compiler w/ nested instructions"){
    processor = new CompilingProcessor(
      Model.simple().
        put(int.mult(int(2)),int.plus(int)).
        put(int.plus(int(0)),int).
        put(int.plus(int(1)).plus(-1),int))

    processor.apply(int.plus(int(0)).plus(int.plus(int(1)).plus(int(-1)).plus(int(0))))
    assertResult(int.plus(int.plus(int(2)).plus(int(3)).plus(int(4))))(processor.apply(int.plus(int(0)).plus(int.plus(int(2)).plus(int(3)).plus(int(4))).asInstanceOf[Type[Int]]))
    assertResult(int.plus(int))(processor.apply(int.plus(0).plus(int.plus(0))))
    assertResult(int.plus(int))(processor.apply(int.plus(int(0)).plus(int.plus(int(1)).plus(int(-1)).plus(int(0)))))
  }
}