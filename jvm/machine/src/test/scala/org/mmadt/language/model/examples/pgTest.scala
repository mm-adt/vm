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


import org.mmadt.language.obj.Obj.{intToInt, symbolToToken, tupleToRecYES}
import org.mmadt.language.obj.`type`.__.symbolToRichToken
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.BaseInstTest.engine
import org.mmadt.processor.inst.TestSetUtil.{IGNORING, _}
import org.mmadt.storage
import org.mmadt.storage.StorageFactory.{rec, _}


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class pgTest extends BaseInstTest(
  testSet("property graph #1", storage.model('pg_1),
    comment("vertex"),
    testing((str("id") -> int(1)), 'vertex, 'vertex(str("id") -> int(1)), "('id'->1) => vertex"),
    comment("edge"),
    // excepting(lst(g=(Tokens.`;`,List(rec(str("id") -> int(1)), rec(str("id") -> int(2))))), ('vertex `;` 'vertex) `=>` 'edge, LanguageException.typingError(lst(g=(Tokens.`;`,List((str("id") -> int(1)), rec(str("id") -> int(2))))), 'edge), "(('id'->1);('id'->2))=>(vertex;vertex)=>edge"),
    excepting(lst(g = (Tokens.`;`, List(rec(str("id") -> int(1)), rec(str("id") -> int(2))))), 'edge, LanguageException.typingError(rec(str("id") -> int(1)) `;` rec(str("id") -> int(2)), 'edge), "(('id'->1);('id'->2))=>edge"),
    comment("exceptions"),
    excepting(6, 'vertex, LanguageException.typingError(6, 'vertex), "6 => vertex"),
    excepting(7, 'edge, LanguageException.typingError(7, 'edge), "7 => edge"),
    excepting((8 `;` 9), 'edge, LanguageException.typingError((8 `;` 9), 'edge), "(8;9) => edge"),
    // excepting((1 `;` 2), ('vertex `;` 'vertex), LanguageException.typingError(1 `;` 2, asType('vertex `;` 'vertex)), "(1;2) => (int;int) => (vertex;vertex)"),
  ), testSet("property graph #2", storage.model('pg_2),
    comment("int=>vertex"),
    testing(5, 'vertex, 'vertex(str("id") -> int(5)), "5 => vertex"),
    testing(5, int.-<('vertex `;` 'vertex), lst(g = (Tokens.`;`, List('vertex(str("id") -> int(5)), 'vertex(str("id") -> int(5))))), "5-<(vertex;vertex)"),
    excepting("6", 'vertex, LanguageException.typingError("6", 'vertex), "'6' => vertex"),
    comment("(int;int)=>(vertex;vertex)=>edge"),
    testing((1 `;` 2), ('vertex `;` 'vertex), ('vertex(str("id") -> int(1)) `;` 'vertex(str("id") -> int(2))), "(1;2) => (vertex;vertex)"),
    testing((3 `;` 4), ('vertex `;` 'vertex) `=>>` 'edge, 'edge(str("outV") -> 'vertex(str("id") -> int(3)) `_,` str("inV") -> 'vertex(str("id") -> int(4))), "(3;4) => (vertex;vertex) => edge"),
    testing((5 `;` 6), 'edge, 'edge(str("outV") -> 'vertex(str("id") -> int(5)) `_,` str("inV") -> 'vertex(str("id") -> int(6))), "(5;6) => edge"),
    //IGNORING("eval-[2-5]")(5, (int.-<(__`;`__.plus(1)))`=>`('vertex`;`'vertex)`=>`'edge, 'edge(str("outV") -> 'vertex(str("id") -> int(5)) `_,` str("inV") -> 'vertex(str("id") -> int(6))), "5=>int=>-<(_;+1)=>(vertex;vertex)=>edge"),
  ), testSet("int=>(vertex;vertex)", storage.model('pg_2).defining(('vertex `;` 'vertex) <= (int.-<('vertex `;` 'vertex))),
    // IGNORING(".*")(1, ('vertex `;` 'vertex), ('vertex(str("id") -> int(1)) `;` 'vertex(str("id") -> int(1))), "1 => (vertex;vertex)"),
    // testing(int(1, 2), int.q(2) `=>` ('vertex `;` 'vertex), strm[Obj](('vertex(str("id") -> int(1)) `;` 'vertex(str("id") -> int(1))), ('vertex(str("id") -> int(2)) `;` 'vertex(str("id") -> int(2)))), "[1,2] => (vertex;vertex)"),
  ), testSet("property graph #4", storage.model('pg_4).define(int <= str.as(int)),
    // IGNORING("eval-.", "query-2")(int, 'vertex, 'vertex<=int.as('nat<=int.is(__.gt(0)).as('vertex<='nat.split((str("id") -> __('nat)) `_,` (str("label") -> str("vertex"))))), "int => vertex"),
    testing(8, 'vertex, 'vertex(str("id") -> 'nat(8) `_,` str("label") -> str("vertex")), "8 => vertex"),
    testing(8.q(5), 'vertex.q(5), 'vertex(str("id") -> 'nat(8) `_,` str("label") -> str("vertex")).q(5), "8{5} => vertex{5}"),
    IGNORING("eval-[3-5]")(5, int.-<(int `,` int.plus(10)).as('vertex `;` 'vertex), lst(g = (Tokens.`;`, List(('vertex(str("id") -> 'nat(5) `_,` str("label") -> str("vertex"))), ('vertex(str("id") -> 'nat(15) `_,` str("label") -> str("vertex")))))),
     // "(5,'15')=>(int;str)=(int;int)=>(vertex;vertex)",
      "(5,'15')=>(int;str)=(int;int)=(nat;nat)=>(vertex;vertex)"),
    IGNORING("eval-[3-5]", "query-2")((9 `;` "person" `;` "name" `;` "marko"), 'vertex,
      'vertex(str("id") -> 'nat(9) `_,` str("label") -> str("person") `_,` str("props") -> 'props(str("name") -> str("marko"))),
      "(9;'person';'name';'marko') => vertex"),
  )) {

  test("testing") {
    engine.eval(":[model,digraph]")
    //println(engine.eval("5 => -<(vertex,+2)=>(vertex;vertex)"))
    println(engine.eval("[1,2,3] => int{3}[as,nat{3}][plus,2]"))
    println(engine.eval("int -<(_;_) => (int;int) => edge"))
    //   println(engine.eval("[(1;2),(2;3),(3;4)] => edge{3} =| graph"))
  }

}