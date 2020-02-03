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

import org.mmadt.machine.obj.impl.obj.`type`.TBool
import org.mmadt.machine.obj.impl.obj.`type`.TInt.int
import org.mmadt.machine.obj.impl.obj.value.VBool.{boolF, boolT}
import org.mmadt.machine.obj.impl.traverser.RecursiveTraverser

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
  // println(c.insts())
  println(int)
  println(int.is(int.gt(5)))
  println(int + 2 > 4)
  val t = new RecursiveTraverser(int(2))
  println(t(int.plus(2).mult(10).plus(60)).obj())
  println(t(int.plus(int.plus(1)).mult(int.plus(1))).obj())
  println(int(43).plus(int).gt(57))
  println(boolT.and(new TBool()))

}