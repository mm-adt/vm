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

package org.mmadt.processor

import org.mmadt.language.obj.Int
import org.mmadt.language.obj.`type`.{IntType,Type}
import org.mmadt.storage.obj.int
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class FoldInstTest extends FunSuite {

  test("[fold] w/ int"){
    assertResult("int[fold,'seed',0,int[plus,int<seed>]]")(int.fold[Int](int(0))(int.plus(int.from[IntType]("seed"))).toString)
    assertResult(int(2))(int(2).fold[Int](int(1))(int.id()))
    assertResult(int(7))((int(1,2,3) ===> int.fold[Int](int(1))(int.plus(int.from[IntType]("seed"))).asInstanceOf[Type[Int]]).next)
    assertResult(int(7))((int(1,2,3) ===> int.fold[Int](int(1))(int.plus(int.from[IntType]("seed")).mult(int(1))).asInstanceOf[Type[Int]]).next)
    assertResult(int(8))((int(0,1) ===> int.fold[Int](int(1))(int.plus(int.from[IntType]("seed")).mult(int(2))).asInstanceOf[Type[Int]]).next)
    assertResult(int(0))((int(1,2,3) ===> int.fold[Int](int(1))(int.plus(int.from[IntType]("seed")).mult(int(1)).mult(int(0))).asInstanceOf[Type[Int]]).next)
  }
}
