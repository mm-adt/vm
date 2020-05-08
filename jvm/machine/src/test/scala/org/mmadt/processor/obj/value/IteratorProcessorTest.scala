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

package org.mmadt.processor.obj.value

import org.mmadt.language.LanguageException
import org.mmadt.processor.Processor
import org.mmadt.storage.StorageFactory._
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class IteratorProcessorTest extends FunSuite with TableDrivenPropertyChecks with Matchers {
  test("process single value w/ quantifiers") {
    assertResult(int(110))(Processor.iterator().apply(int(5), int.mult(int(2)).plus(int(100))))
    assertResult(int(110))(Processor.iterator()(int(5), int * 2 + 100))
    assertResult(int(110).q(10))(Processor.iterator().apply(int(5).q(10), int.q(10).mult(int(2)).plus(int(100))))
    assertResult(int(110).q(10))(Processor.iterator().apply(int(5), int.mult(int(2)).plus(int(100)).q(10)))
    // assertResult(List(int(110).q(100)))(Processor.iterator().apply(int(5).q(10),int.mult(int(2)).plus(int(100)).q(10)).map(int.plus(0)).toList)
  }
  test("process multiple values w/ quantifiers") {
    assertResult(int(102, 104, 106))(Processor.iterator().apply(int(1, 2, 3), int.q(*).mult(int(2)).plus(int(100))))
    assertResult(int(102, 104, 106))((int(1, 2, 3) ===> int.q(1, 10).mult(int(2)).plus(int(100))))
    assertResult(int(11, 22, 33))(int(10, 20, 30) ===> int.q(*).split(
      int.is(int.gt(int(20))) --> int.plus(int(3)) |
        int.is(int.gt(int(10))) --> int.plus(int(2)) |
        int --> int.plus(int(1))).>-.plus(int(0)))
    /*    assertResult(int(50))((int(10).q(int(50)) ===> int.q(50).plus(int(2)).count()))
        assertResult(int(3))((int(10,20,30) ===> int.q(+).plus(int(2)).count()))
        assertResult(int(4))((int(10,20,30,40) ===> int.q(*).plus(int(2)).mult(int(100)).count()))
        assertResult(int(2))((int(10,20,30,40) ===> int.q(*).is(int.gt(int(20))).count()))
        assertResult(int(12))((int(10,20,30,40) ===> int.q(1,57).is(int.gt(int(20))).count().plus(int(10))))
        assertResult(int(1))((int(10,20,30,40) ===> int.q(+).is(int.gt(int(20))).count().plus(int(10)).count()))*/
    /*    assertResult(List(int(11).q(100),int(22).q(200),int(33).q(300)))((int(10,20,30) ===> int.choose(
      int.is(int.gt(int(20))) -> int.plus(int(3)).q(300),
      int.is(int.gt(int(10))) -> int.plus(int(2)).q(200),
      int -> int.q(10).plus(int(1)).q(10)).plus(int(0))).toList)*/
  }
  test("process nested single values w/ quantifiers") {
    assertResult(int(2))(int(1) ===> int.mult(int(2)).is(int.gt(int(1))))
    assertResult(int(2))(int(1) ===> int.mult(int(2)).is(int.plus(int(10)).gt(int(1))))
  }
  test("process canonical type") {
    int(10) ===> int

    int(1, 2, 3) ==> int.q(+)

    assertThrows[LanguageException] {
      int(10) ===> int.q(0)
    }

    assertThrows[LanguageException] {
      int(1, 2, 3) ==> int.q(0)
    }

    assertThrows[LanguageException] {
      int(10) ===> bool.and(bfalse)
    }

    assertThrows[LanguageException] {
      int(10) ===> str
    }

    assertThrows[LanguageException] {
      int(10) ===> str.q(2)
    }

    assertThrows[LanguageException] {
      str("hello") ===> bool
    }
  }
}