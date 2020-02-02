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

package org.mmadt

import org.mmadt.machine.obj.impl.obj.`type`.TInt.int
import org.mmadt.machine.obj.impl.obj.value.VBool.{boolF, boolT}
import org.mmadt.machine.obj.impl.traverser.TTraverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object Play extends App {

  val a = int(34)
  val b = int(4)
  println(a.+(b))
  println(a * b)
  println(boolT & boolF)
  // println(a.is(int.gt(4)).map(boolF).or(boolT))
  val c = int.plus(int.plus(34)).mult(4).is(int.plus(4).gt(20)).gt(45).or(boolT)
  println(c)
  println(c.head())
  println(int.head())
  println(int)

  println(new TTraverser(int(2),int.plus(2).mult(10)).apply().apply().obj())

}