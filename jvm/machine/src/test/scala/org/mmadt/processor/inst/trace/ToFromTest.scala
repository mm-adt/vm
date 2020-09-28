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
import org.mmadt.language.obj.Int
import org.mmadt.language.obj.Obj._
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp.{MM, MMX, NONE}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{excepting, _}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ToFromTest extends BaseInstTest(
  testSet("[to][from] table test", List(NONE, MM, MMX),
    comment("types"),
    testing(int, to('x).plus(1).from('x), int.to('x).plus(1).from('x), "int<x>[plus,1]<.x>"),
    testing(int, to('x).plus(1).plus(from('x)), int.to('x).plus(1).plus(from('x)), "int<x>[plus,1][plus,int<.x>]"),
    testing(__, int.to('x).map(__).to('y).from('x).from('y), int.to('x).map(int).to('y).from('x).from('y), "int<x>[map,_]<y><.x><.y>"),
    testing(__, int.to('x).map(from('x)).to('y).from('x).from('y), int.to('x).map(int.from('x)).to('y).from('x).from('y), "int<x>[map,int<.x>]<y><.x><.y>"),
    testing(int, int.to('x).map(from('x)).to('y).from('x).from('y), int.to('x).map(int.from('x)).to('y).from('x).from('y), "int<x>[map,<.x>]<y><.x><.y>"),
    testing(int, int.to('x).map(plus(mult(from('x)))).to('y).from('x).from('y), int.to('x).map(int.plus(int.mult(int.from('x)))).to('y).from('x).from('y), "int<x>[map,[plus,[mult,<.x>]]]<y><.x><.y>"),
    excepting(1, int.plus(1).plus(int.mult(10).to('x)).from('x), LanguageException.labelNotFound(1 `;` 2 `;` 22, "x"), "1=>int[plus,1][plus,int[mult,10]<x>]<.x>"),
    comment("values"),
    testing(1.q(5), int.q(5).to('x).plus(1).from('x), 1.q(5), "1{5} => int{5}<x>+1<.x>"),
    testing(1.q(5), int.q(5).to('x).plus(1).q(10).from('x), 1.q(50), "1{5} => int{5}<x>+{10}1<.x>"),
    testing(1.q(5), int.q(5).to('x).plus(1).q(10).from('x).q(2), 1.q(100), "1{5} => int{5}<x>+{10}1<.x>{2}"),
    testing(1, int.to('x).plus(1).plus(from('x, int)), 3, "1 => int<x>+1[plus,<.x>]"),
    testing(1.q(10), int.q(10).to('x).plus(1).plus(from('x, int)), 3.q(10), "1{10} => int{10}<x>+1[plus,<.x>]"),
    testing(1, int.to('x).plus(1).plus(2).plus(int.from('x, int)), 5, "1 => int<x>[plus,1][plus,2][plus,<.x>]"),
    testing(1, int.to('x).plus(1).plus(2).plus(int.from('x, int)), 5, "1 => int<x>[plus,1][plus,2][plus,x]"),
    testing(1, int.to('x).plus(1).plus(int.from('x).plus(2)), 5, "1 => int<x>[plus,1][plus,x+2]"),
    testing(1, int.to('x).plus(1).plus(int.plus(2).from('x)), 3, "1 => int<x>[plus,1][plus,int[plus,2]<.x>]"),
    testing(1, int.to('x).plus(1).map(int.from('x, int)), 1, "1 => int<x>[plus,1][map,<.x>]"),
    testing(1, int.to('x).plus(1).map('x), 1, "1 => int<x>[plus,1][map,x]"),
    testing(1, int.to('x).plus(1).plus(int.from('x)), 3, "1 => int<x>[plus,1][plus,int<.x>]"),
    testing(1, int.to('x).plus(1).map[Int](100).from('x), 1, "1 => int<x>[plus,1][map,100]<.x>"),
    testing(1, int.to('x).plus(1).map[Int](100).from('x), 1, "1 => int<x>[plus,1][map,100][x]"),
    testing(1.q(10), int.q(10).to('x).plus(1).map[Int](100).from('x), 1.q(10), "1{10} => int{10}<x>[plus,1][map,100][x]"),
    testing(1, int.plus(1).map[Int](5).to('x).from('x), 5, "1 => int[plus,1][map,5]<x><.x>"),
    testing(1, int.plus(2).to('x).plus(1).to('y).plus(int.from('x, int).mult(int.from('y, int))), 16, "1 => int[plus,2]<x>[plus,1]<y>[plus,x[mult,y]]"),
    testing(1, int.plus(2).to('x).plus(1).to('y).map(int.plus(int.from('x, int).mult('y))), 16, "1 => int[plus,2]<x>[plus,1]<y>[map,int[plus,<.x>[mult,y]]]"),
    testing(1, int.plus(2).to('x).plus(1).to('y).map(int.plus('x.mult('y))), 16, "1 => int[plus,2]<x>[plus,1]<y>[map,int[plus,[x][mult,y]]]"),
    testing(1, int.plus(2).to('x).plus(1).to('y).map(int.plus('x.mult('y))), 16, "1 => int[plus,2]<x>[plus,1]<y>[map,int[plus,x[mult,y]]]"),
    excepting(1, int.from('x).plus(1).map(int.mult(10)), LanguageException.labelNotFound(lst(int(1)), "x"), "1 => int<.x>+1[map,int*10]"),
    excepting(1, int.from('x).plus(1), LanguageException.labelNotFound(lst(int(1)), "x"), "1 => int<.x>+1"),
  ), testSet("[to][from] id rewrite", List(MMX, MM),
    comment("types"),
    testing(int, id.to('x).map(int.plus(int.mult(int.from('x)))).to('y).from('x).from('y), int.to('x).map(int.plus(int.mult(int.from('x)))).to('y).from('x).from('y), "int[id]<x>[map,int[plus,int[mult,int<.x>]]]<y><.x><.y>"),
    testing(__, int.id.to('x).map(int.plus(int.from('x))).to('y).from('x).from('y), int.to('x).map(int.plus(int.from('x))).to('y).from('x).from('y), "int[id]<x>[map,int[plus,int<.x>]]<y><.x><.y>"),
    testing(__, int.id.to('x).id.plus(int.from('x).id).to('y).from('x).id.from('y).id, int.to('x).plus(int.from('x)).to('y).from('x).from('y), "int[id]<x>[id][plus,int<.x>[id]]<y><.x>[id]<.y>[id]"),
    testing(int, int.id.to('x).map(int.from('x)).to('y).from('x).from('y), int.to('x).map(int.from('x)).to('y).from('x).from('y), "int[id]<x>[map,int<.x>]<y><.x><.y>"),
    testing(int, int.id.to('x).map(__('x)).to('y).from('x).from('y), int.to('x).map(__('x)).to('y).from('x).from('y), "int[id]<x>[map,x]<y><.x><.y>"),
    testing(__, int.to('x).map(id).to('y).from('x).from('y), int.to('x).map(int).to('y).from('x).from('y), "int<x>[map,[id]]<y><.x><.y>"))) {

  test("[to][from] model") {
    val model = int.to('y).plus(2).to('x).plus(int.to('z).from('y)).model
    assert(model.vars("x").isDefined)
    assert(model.vars("y").isDefined)
    assert(model.vars("z").isEmpty)
  }


  /*
    test("to/from state parsing") {
    assertResult(real(45.5))(engine.eval("45.0<x>[mult,0.0][plus,<.x>][plus,0.5]"))
    assertResult(int.to("a").plus(int(10)).to("b").mult(int(20)))(engine.eval("int<a>[plus,10]<b>[mult,20]"))
    assertResult(int.to("a").plus(int(10)).to("b").mult(int.from("a")))(engine.eval("int<a>[plus,10]<b>[mult,<.a>]"))
    assertResult(int.to("a").plus(int(10)).to("b").mult(int.from("a")))(engine.eval("int<a>[plus,10]<b>[mult,int<.a>]"))
    assertResult(int.to("x").plus(int(10)).to("y").mult('x))(engine.eval("int<x>[plus,10]<y>[mult,x]"))
    assertResult(int(600))(engine.eval("19[plus,1]<x>[plus,10][mult,x]"))
    assertResult(int(21))(engine.eval("5[plus,2]<x>[mult,2][plus,<.x>]"))
    assertResult(int(21))(engine.eval("5[plus,2]<x>[mult,2][plus,int<.x>]"))
    assertResult("int[plus,2]<x>[mult,2]<y>[plus,int<.x>[plus,int<.y>]]")(engine.eval("int[plus,2]<x>[mult,2]<y>[plus,<.x>[plus,<.y>]]").toString)
    assertResult(int(35))(engine.eval("5[plus,2]<x>[mult,2]<y>[plus,int<.x>[plus,<.y>]]"))
    assertResult(int(13))(engine.eval("5 => int<x>[plus,1][plus,x[plus,2]]"))
    assertResult(int(14))(engine.eval("5 => int<x>[plus,1][plus,<x>[plus,2]]"))
    assertResult(int(19))(engine.eval("5 => int<x>[plus,1][plus,<x>[plus,2]][plus,x]"))
    assertResult(int(28))(engine.eval("5 => int<x>[plus,1][plus,<x>[plus,2]][plus,<x>]"))
    assertResult(int(28).q(3))(engine.eval("[5,5,5] => int{3}<x>[plus,1][plus,<x>[plus,2]][plus,<x>]"))
    assertResult(int(28, 32, 36))(engine.eval("[5,6,7] => int{3}<x>[plus,1][plus,<x>[plus,2]][plus,<x>]"))
    assertThrows[LanguageException] {
      engine.eval("50[is>dog]")
    }
  }
   */
}