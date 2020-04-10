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

package org.mmadt.storage.obj.`type`

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.initial.IntOp
import org.mmadt.language.obj.op.map.{MultOp, PlusOp}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TIntTest extends FunSuite {
  test("canonical int"){
    assert(int.root)
    assertResult(IntOp())(int.via._2)
    assertResult(null)(int.via._1)
  }

  test("derived int"){
    assert(!int.plus(2).root)
    assertResult(int)(int.plus(2).via._1)
    assertResult(PlusOp(2))(int.plus(2).via._2)
    //
    assert(!int.plus(2).mult(5).root)
    assertResult(int.plus(2))(int.plus(2).mult(5).via._1)
    assertResult(MultOp(5))(int.plus(2).mult(5).via._2)
  }

  test("int type"){
    assertResult("int")(int.name)
    assertResult(int.plus(int))(int + int)
    assertResult(int.plus(int(-4)))(int + -4)
    assertResult(int.mult(int(10)))(int * 10)
    assertResult(int.plus(int.plus(int)))(int + (int + int))
    assertResult(int.plus(int.plus(int.mult(int))))(int + (int + (int * int)))
  }

  test("canonical int type"){
    assertThrows[LanguageException]{bool <= int}
    assert(int.q(?).test((int.q(?) <= int.is(int.gt(5))).range))
    assert(!int.test(bool))
    assert(!bool.test(int))
  }
}