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

package org.mmadt.language.model.examples

import org.mmadt.language.model.examples.patternTest.PATTERN
import org.mmadt.language.obj.Obj.{intToInt, stringToStr, symbolToToken, tupleToRecYES}
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__.{as, symbolToRichToken}
import org.mmadt.language.obj.op.sideeffect.LoadOp
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.BaseInstTest.{bindings, engine}
import org.mmadt.processor.inst.TestSetUtil.{testing, _}
import org.mmadt.storage
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object patternTest {
  val PATTERN:Model = storage.model(LoadOp.loadObj[Model](getClass.getResource("/test/pattern.mm").getPath))
}
class patternTest extends BaseInstTest {

  test("basic pattern") {
    evaluate(testSet("basic patterns", PATTERN,
      comment("ipair"),
      IGNORING("eval-.", "query-2")(int, 'ipair, 'ipair(int `;` int) <= int.as('ipair(int `;` int) <= int.split(int `;` int)), "int => ipair"),
      IGNORING("eval-.", "query-2")(int, 'ipair.as('isnd), 'isnd(int) <= int.as('ipair(int `;` int) <= int.split(int `;` int)).as('isnd <= 'ipair.get(1)), "int => ipair => isnd"),
      testing(5, 'ipair, 'ipair(5 `;` 5), "5 => ipair"),
      testing('ipair(5 `;` 5), (int `;` int), (5 `;` 5), "ipair:(5;5) => (int;int)"),
      IGNORING("eval-.", "query-2")(int, int ==> 'ipair ==>(int `;` int), (int `;` int) <= (int.as('ipair <= int.split(int `;` int))), "int => ipair => (int;int)"),
      IGNORING("eval-[3-5]")(5, int ==> 'ipair ==>(int `;` int), (5 `;` 5), "5 => int => ipair => (int;int)"),
      excepting("4", 'ipair, LanguageException.typingError("4", 'ipair), "'4' => ipair"),
      excepting("five", 'ipair, LanguageException.typingError("five", 'ipair), "'five' => ipair"),
      IGNORING("eval-[2-5]", "query-2")(int, as('ipair), int.as('ipair(int `;` int) <= int -< (int `;` int)), "int => ipair"),
      comment("pair"),
      testing(5, 'pair, 'pair(5 `;` 5), "5 => pair"),
      testing("4", 'pair, 'pair("4" `;` "4"), "'4' => pair"),
      testing("five", 'pair, 'pair("five" `;` "five"), "'five' => pair"),
      IGNORING("eval-[2-5]", "query-2")(int, int.as('pair), int ==> ('pair(int`;`int) <= int -< (int `;` int)), "int => pair"),
    ))
  }
  test("productions") {
    evaluate(testSet("projections", PATTERN,
      comment("fst and snd"),
      IGNORING("eval-[2-5]", "query-2")('ipair, __ ==> 'ifst, 'ifst <= 'ipair(int `;` int).as('ifst <= 'ipair(int `;` int).get(0)), "ipair => ifst"),
      testing('ipair(1 `;` 2), 'ifst, 'ifst(1), "ipair:(1;2) => ifst"),
      testing((1 `;` 2), 'ifst, 'ifst(1), "(1;2) => ifst"),
      excepting(("one" `;` "two"), 'ifst, LanguageException.typingError("one" `;` "two", 'ifst), "('one';'two') => ifst"),
      testing(("one" `;` "two"), 'fst, 'fst("one"), "('one';'two') => fst")
    ))
  }

  test("bi-uni-products") {
    evaluate(testSet("bi-uni-products", PATTERN,
      IGNORING("eval-[3-5]")(6, int.split(__ `;` __).as(int `;`(int.as('ipair))).as('pair), 'pair(6 `;` 'ipair(6 `;` 6)),
        "6 => int-<(_;_)=>(int;int=>ipair)=>pair",
        "6 => int => -<(int;int)=>(int;int=>ipair)=>pair",
        "6-<(_;_)=>pair:(int;int=>ipair)",
        "6 => -<(_;_)=>pair:(int;int=>ipair)",
        "6 => int => -<(_;_)=>pair:(int;int=>ipair)",
        "6-<(int;int=>ipair)=>pair",
        "6 => -<(int;int=>ipair)=>pair",
        "6 => int => -<(int;int=>ipair) => pair",
        "6 => int => -<pair:(int;int=>ipair)",
      )))
  }

  test("biproducts") {
    evaluate(testSet("biproducts", PATTERN,
      testing((1 `;` 2), 'ipair.combine(int.plus(2) `;` int.plus(3)), 'ipair(3 `;` 5), "(1;2)=>ipair=(+2;+3)"),
      IGNORING("eval-[3-5]")((1 `;` 2), 'pair.combine('ipair `;` int.plus(3)), lst(name = "pair", g = (Tokens.`;`, List('ipair(1 `;` 1), int(5)))),
        "(1;2)=>pair=>=(ipair;+3)=>pair",
        // "(1;2)=>(int;int)=(int=>ipair;int=>+3)=>pair",
        "(1;2)=>(int;int)=>(ipair;int+3)=>pair",
        "(1;2)=>(int;int)=>(ipair;int)=>pair=>=(_;+3)",
        "(1;2)=>(int;int)=>(ipair;int+3)=>(_;int)=>pair",
        "(1;2)=>(int;int)=>(ipair;int+3)=>pair=>pair=>pair",
        "(1;2)=>(int;int)=>(ipair;int+3)=>pair=>=(ipair;int)=>pair",
        "(1;2)=>(int;int)=>(ipair;int+3)=>pair=>=(_;_)=>pair",
        "(1;2)=>(int;int)=>pair:(ipair;int+3)",
        "(1;2)=>pair=>=(int;int)=>=(ipair;+3)",
        "(1;2)=>pair=>=(int;int)=>=(ipair;+3)=>pair",
        "(1;2)=>pair=>=(int;int)=>=(ipair;+3)=>=(ipair;int)=>pair",
        "(1;2)=>pair=>=(int;int)=>=(ipair;+2)=>=(ipair;int+1)=>pair",
        //"(1;2)=>pair=>(int;int)=>(ipair;+2)=>(ipair;int+1)=>pair",
      )))
  }

  test("quantifiers") {
    evaluate(testSet("quantifiers", PATTERN,
      IGNORING("eval-2", "eval-4", "query-2")(int.q(4), as('dble.plus(10)), 'dble.q(4) <= int.q(4).as('dble.q(4) <= int.q(4).mult(2)).plus(10), "int{4} => dble+10"),
      testing(3.q(4), 'dble, 'dble(6.q(4)), "3{4} => dble"),
      testing(4.q(5), 'dble.plus(10).q(6), 'dble(18.q(30)), "4{5} => dble[plus,10]{6}"),
    ))
  }

  test("custom instructions") {
    evaluate(testSet("custom instructions", PATTERN,
      IGNORING("eval-[3-5]", "query-2")(2 `;` 3, (int `;` int)==>'aplus, 'aplus(5),
        "(2;3)[aplus]",
       // "(2;3)=>aplus",
        "(2;3)=>(int;int)=>aplus",
        "(2;3)=>(dble;dble)=>(int+-2;int+-3)=>aplus",
        "(2;3)=>(dble+-2;dble+-3)=>aplus",
        "(1;1)=(int+1;int+2)=>aplus",
        "(1;1)=>(int;int)=(int+1;int+2)=>aplus",
      ),
    ))
  }

  test("rec coercions")(evaluate(testSet("rec coercions", PATTERN,
    testing(str("a") -> int(1) `_,` str("b") -> str("two"), 'abc, 'abc(str("a") -> int(1) `_,` str("b") -> str("two")), "('a'->1,'b'->'two')=>abc"),
    //testing(str("a")->int(1)`_,`str("b")->str("two")`_,`str("x")->int(123),'abc,'abc(str("a")->int(1)`_,`str("b")->str("two")),"('a'->1,'b'->'two','x'->123)=>abc"),
    excepting(str("a") -> int(1) `_,` str("b") -> int(2), 'abc, LanguageException.typingError(str("a") -> int(1) `_,` str("b") -> int(2), 'abc), "('a'->1,'b'->2)=>abc"),
  )))

  test("blah") {
    println(engine.eval("(4;5)=>(int;int)=(ipair;int)", bindings(PATTERN)))
  }

}
