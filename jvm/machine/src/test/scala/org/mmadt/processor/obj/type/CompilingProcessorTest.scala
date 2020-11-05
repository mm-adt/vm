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

import org.mmadt.language.obj.Obj.intToInt
import org.mmadt.language.obj.`type`.__._
import org.mmadt.processor.Processor
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CompilingProcessorTest extends BaseInstTest {
  final var processor:Processor = Processor.compiler

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
    val rewrites = int.define(
      (int.plus(int) `,`) <= '^(int.mult(2) `,`),
      (int `,`) <= '^(int.plus(0) `,`),
      (int `,`) <= '^(int.plus(1).plus(-1) `,`))
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
      i => assertResult(int)(processor.apply(rewrites `=>>` i))
    }
  }

  test("compiler w/ model") {
    processor = Processor.compiler
    val rewrites = int.define(
      (int.mult(2) `;`) <= '^(int.plus(int) `;`),
      (int.mult(4) `;`) <= '^(int.mult(2).mult(2) `;`),
      (int `,`) <= '^(int.plus(1).plus(-1) `;`))
    /////
    assertResult(int.mult(2))(processor.apply(int, rewrites `=>>` int.plus(int)))
    assertResult(int.mult(4))(processor.apply(int, rewrites `=>>` int.plus(int).mult(int(2))))
  }

  test("compiler w/ nested instructions") {
    processor = Processor.compiler

    val definitions = int
      //.define((int.mult(2)`;`)<=(int.plus(int)`;`))
      .define((int `,`) <= '^(int.plus(0) `;`))
    //      .rewrite((int.zero() `;`) <= (int.plus(1).plus(-1) `;`))
    //  assertResult(int)(processor.apply(definitions.plus(int.plus(1).plus(-1))))
    //    assertResult(int)(processor.apply(definitions.plus(int.plus(1).plus(-1)).plus(0)))
    assertResult(int.plus(int.plus(2).plus(3).plus(4)))(processor.apply(definitions.plus(int.plus(2).plus(3).plus(4))))
    assertResult(int.plus(int))(processor.apply(definitions.plus(0).plus(int.plus(0))))
    //  assertResult(int)(processor.apply(definitions.plus(0).plus(int.plus(int(1)).plus(-1).plus(int(0)))))
  }

}