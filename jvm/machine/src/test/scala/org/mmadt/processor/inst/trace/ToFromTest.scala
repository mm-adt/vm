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

package org.mmadt.processor.inst.trace

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.`type`.__
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.TableDrivenPropertyChecks

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ToFromTest extends FunSuite with TableDrivenPropertyChecks {

  test("[to][from] model") {
    val model = int.to("y").plus(2).to("x").plus(int.to("z").from("y")).model
    assert(model.vars("x").isDefined)
    assert(model.vars("y").isDefined)
    assert(model.vars("z").isEmpty)
  }

  test("[to][from] w/ values") {
    assertResult("int<x>[plus,1]<.x>")(int.to("x").plus(1).from("x").toString)
    assertResult("int<x>[plus,1]<.x>")(int.to("x").plus(1).from("x").toString)
    assertResult("int<x>[plus,1][plus,int<.x>]")((int ==> int.to("x").plus(1).plus(int.from("x"))).toString)
    assertResult("int<x>[plus,1][plus,int<.x>]")((int ==> int.to("x").plus(1).plus(int.from("x"))).toString)
    assertResult(int(3))(int(1) ==> int.to("x").plus(1).plus(int.from("x", int)))
    assertResult(int(5))(int(1) ==> int.to("x").plus(1).plus(2).plus(int.from("x", int)))
    assertResult(int(5))(int(1) ==> int.to("x").plus(1).plus(int.from("x", int).plus(2)))
    assertResult(int(3))(int(1) ==> int.to("x").plus(1).plus(int.plus(2).from("x", int)))
    assertResult(int(1))(int(1) ==> int.to("x").plus(1).map(int.from("x", int)))
    //
    assertResult(int(3))(int(1) ==> int.to("x").plus(1).plus(int.from("x")))
    assertResult(int(5))(int(1) ==> int.to("x").plus(1).plus(2).plus(int.from("x")))
    assertResult(int(5))(int(1) ==> int.to("x").plus(1).plus(int.from("x").plus(2)))
    assertResult(int(3))(int(1) ==> int.to("x").plus(1).plus(int.plus(2).from("x")))
    assertResult(int(1))(int(1) ==> int.to("x").plus(1).map(int.from("x")))
    assertResult(int(1))(int(1) ==> int.to("x").plus(1).from("x", int))
    assertResult(int(1))(int(1) ==> int.to("x").plus(1).from("x"))
    assertResult(int(1))(int(1) ==> int.to("x").plus(1).map(int(100)).from("x", int))
    //
    assertResult(int(5))(int(1) ==> int.plus(1).map(int(5)).to("x").from("x", int))
    assertResult(int(16))(int(1) ==> int.plus(2).to("x").plus(1).to("y").map(int.plus(int.from("x", int).mult(int.from("y", int)))))
    //
    intercept[LanguageException] {
      int(1) ==> int.from("x").plus(1).map(int.mult(10))
    }
  }

  test("[to][from] w/ types") {
    assertResult("int[plus,1][map,int]<x>")(int.plus(1).map(int).to("x").toString)
    assertResult("int<x>[map,int<.x>]<y><.x><.y>")(int.to("x").map(int.from("x")).to("y").from("x").from("y").toString)
    assertResult("int<x>[map,int[plus,int<.x>]]<y><.x><.y>")(int.to("x").map(int.plus(int.from("x"))).to("y").from("x").from("y").toString)
    assertResult("int<x>[map,int[plus,int[mult,int<.x>]]]<y><.x><.y>")(int.to("x").map(int.plus(int.mult(int.from("x")))).to("y").from("x").from("y").toString)
    assertResult("int<x>[plus,int<.x>]<y><.x><.y>")(int.to("x").plus(int.from("x")).to("y").from("x").from("y").toString)
    assertResult("int<x>[map,int<.x>]<y><.x><.y>")((int ==> int.to("x").map(int.from("x")).to("y").from("x").from("y")).toString)
    //
    intercept[LanguageException] {
      assertResult(int(20))(int(1) ==> int.plus(1).plus(int.mult(10).to("x")).from("x"))
    }
  }

  test("[to][from] w/ anonymous types") {
    assertResult("int<x>[map,int[id]]<y><.x><.y>")(int.to("x").map(__.id).to("y").from("x").from("y").toString)
    assertResult("int<x>[map,int<.x>]<y><.x><.y>")(int.to("x").map(__.from("x", int)).to("y").from("x").from("y").toString)
    assertResult("int<x>[map,int<.x>]<y><.x><.y>")((int ==> int.to("x").map(__.from("x")).to("y").from("x").from("y")).toString)
    assertResult("int<x>[map,int[plus,int[mult,int<.x>]]]<y><.x><.y>")((int ==> int.to("x").map(__.plus(__.mult(__.from("x")))).to("y").from("x").from("y")).toString)
  }
}