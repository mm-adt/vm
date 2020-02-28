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

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.IntType
import org.mmadt.processor.obj.value.I1Traverser
import org.mmadt.storage.obj._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TraverserTest extends FunSuite {

  def trav(obj:Obj):Traverser[Obj] = new I1Traverser[Obj](obj)

  test("traverser toString"){
    assertResult("[3|a->3]"){
      trav(int(3))(int.to("a")).toString
    }
    assertResult("[8|a->3,b->8]"){
      trav(int(3))(int.to("a").plus(int(5)).to("b")).toString
    }
    assertResult("[11|a->3]"){
      trav(int(3))(int.to("a").plus(int(5).to("b").plus(int.from[IntType]("a")))).toString
    }
  }

  test("traverser state"){
    assertResult(Map("a" -> int(5))){
      trav(int(3))(int.plus(int(2)).to("a").mult(3)).state
    }
    assertResult(Map(
      "a" -> int(5),
      "b" -> int(15))){
      trav(int(3))(int.plus(2).to("a").mult(3).to("b")).state
    }
    assertResult(int(5)){
      int(3) ==> int <= int.plus(int(2)).to("a").mult(3).to("b").plus(1000).from("a")
    }
  }

  test("traverser chain"){
    assertResult(int(100)){
      int(2) ==> int.plus(2).is(int.plus(55).gt(3)).mult(10).plus(60)
    }
    assertResult(int(30)){
      int(2) ==> int.plus(int.plus(1)).mult(int.plus(1))
    }
  }

  test("multi input"){
    /*assertResult(int(100)) {
      int(1,2,3) ==> int.plus(2).is(int.plus(55).gt(3)).mult(10).plus(60)
    }*/
    assertResult(int(30)){
      int(2) ==> int.plus(int.plus(1)).mult(int.plus(1))
    }
  }
}