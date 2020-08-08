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

package org.mmadt.processor.obj.`type`

import org.mmadt.processor.Processor
import org.mmadt.storage.StorageFactory._
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CompilingProcessorTest extends FunSuite with TableDrivenPropertyChecks with Matchers {
  final var processor: Processor = Processor.compiler

  test("compiler w/ linear singleton type") {
    assertResult(int.mult(int(2)))(processor.apply(int, int.mult(int(2))))
    assertResult(int.mult(int(2)).plus(int(3)))(processor.apply(int, int.mult(int(2)).plus(int(3))))
  }

  test("compiler w/ linear quantified type") {
    assertResult(int.q(int(2)).mult(int(2)))(processor.apply(int.q(int(2)), int.q(*).mult(int(2))))
    assertResult(int.q(int(2)) <= int.q(int(2)).mult(int(2)))(processor.apply(int.q(int(2)), int.q(*).mult(int(2))))
    /////
    assertResult(int.q(int(2)).mult(int(2)).plus(int(3)))(processor.apply(int.q(2), int.q(1, 3).mult(int(2)).plus(int(3))))
    assertResult(int.q(int(2)) <= int.q(int(2)).mult(int(2)).plus(int(3)))(processor.apply(int.q(2), int.q(1, 3).mult(int(2)).plus(int(3))))
    /////
    assertResult(int.q(2).mult(2).is(bool.q(2) <= int.q(2).gt(2)))(processor.apply(int.q(int(2)), int.q(2).mult(int(2)).is(int.gt(int(2)))))
    assertResult(int.q(2).mult(2).is(int.q(2).gt(2)))(processor.apply(int.q(int(2)), int.q(2).mult(int(2)).is(int.gt(int(2)))))
  }

  test("compiler w/ linear quantified type and model") {
    processor = Processor.compiler
    val rewrites = int
      .rewrite((int.plus(int) `,`) <= (int.mult(2) `,`))
      .rewrite((int`,`) <= (int.plus(0) `,`))
      .rewrite((int`,`) <= (int.plus(1).plus(-1) `,`))
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
      int.plus(1).plus(1).plus(-1).plus(0).plus(0).plus(-1).plus(1).plus(0).plus(-1).plus(1).plus(-1))) {
      i => assertResult(int)(processor.apply(rewrites `=>` i))
    }
  }

  test("compiler w/ model") {
    processor = Processor.compiler
    val rewrites = int
      .rewrite((int.mult(2) `;`) <= (int.plus(int) `;`))
      .rewrite((int.mult(4) `;`) <= (int.mult(2).mult(2) `;`))
      .rewrite((int`,`) <= (int.plus(1).plus(-1) `;`))
    /////
    assertResult(int.mult(2))(processor.apply(int, rewrites `=>` int.plus(int)))
    assertResult(int.mult(4))(processor.apply(int, rewrites `=>` int.plus(int).mult(int(2))))
  }

  test("compiler w/ nested instructions") {
    processor = Processor.compiler

    val definitions = int
      //.define((int.mult(2)`;`)<=(int.plus(int)`;`))
      .rewrite((int`,`) <= (int.plus(0) `;`))
    //      .rewrite((int.zero() `;`) <= (int.plus(1).plus(-1) `;`))
    //  assertResult(int)(processor.apply(definitions.plus(int.plus(1).plus(-1))))
    //    assertResult(int)(processor.apply(definitions.plus(int.plus(1).plus(-1)).plus(0)))
   assertResult(int.plus(int.plus(2).plus(3).plus(4)))(processor.apply(definitions.plus(int.plus(2).plus(3).plus(4))))
    assertResult(int.plus(int))(processor.apply(definitions.plus(0).plus(int.plus(0))))
    //  assertResult(int)(processor.apply(definitions.plus(0).plus(int.plus(int(1)).plus(-1).plus(int(0)))))
  }

  /*test("compiler with domain rewrites") {
    val socialToMM: Model = Model.simple()
    val mmToSocial: Model = Model.simple()
    //
    mmToSocial.put(int <= int.is(int.gt(0)), int.named("nat"))
    val nat: IntType = mmToSocial("nat")
    mmToSocial.put(rec(str("name") -> str, str("age") -> int), rec(str("name") -> str, str("age") -> nat).named("person"))
    val person: Rec[Str, Obj] = mmToSocial("person")
    socialToMM.put(person, rec(str("name") -> str, str("age") -> int))
    socialToMM.put(nat, int)
    println(mmToSocial + "\n" + socialToMM)
    //
    assertResult("nat")(mmToSocial(int(32)).name)
    assertResult(32)(mmToSocial(int(32)).g)
    assertResult("int")(socialToMM(int(32).named("nat")).name)
    assertResult(32)(socialToMM(int(32).named("nat")).g)
    //
    val compile1 = Processor.compiler(mmToSocial).apply(rec(str("name") -> str, str("age") -> int))
    assertResult("person")(compile1.domain.name)
    assertResult("nat")(compile1.domain.asInstanceOf[Rec[Str, Obj]].get(str("age")).name)
    assertResult("person")(compile1.range.name)
    assertResult("nat")(compile1.range.asInstanceOf[Rec[Str, Obj]].get(str("age")).name)

    val compile2 = Processor.compiler(mmToSocial).apply(rec(str("name") -> str, str("age") -> int).id().get("age", int))
    assertResult("person")(compile2.domain.name)
    assertResult("nat")(compile2.domain.asInstanceOf[Rec[Str, Obj]].get(str("age")).name)
    assertResult("nat")(compile2.range.name)

    val compile3 = Processor.compiler(socialToMM).apply(compile1)
    assertResult("rec")(compile3.domain.name)
    assertResult("int")(compile3.domain.asInstanceOf[Rec[Str, Obj]].get(str("age")).name)
    assertResult("rec")(compile3.range.name)
    assertResult("int")(compile3.range.asInstanceOf[Rec[Str, Obj]].get(str("age")).name)

    val compile4 = Processor.compiler(socialToMM).apply(compile2)
    assertResult("rec")(compile4.domain.name)
    assertResult("int")(compile4.domain.asInstanceOf[Rec[Str, Obj]].get(str("age")).name)
    assertResult("int")(compile4.range.name)
  }*/
}