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

package org.mmadt.language.model

import org.mmadt.language.LanguageException
import org.mmadt.language.model.digraphTest.DIGRAPH
import org.mmadt.language.obj.Obj.{intToInt, stringToStr, symbolToToken, tupleToRecYES}
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.BaseInstTest.engine
import org.mmadt.processor.inst.TestSetUtil._
import org.mmadt.storage
import org.mmadt.storage.StorageFactory.{str, _}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object digraphTest {
  val DIGRAPH:Model = storage.model("digraph")
}
class digraphTest extends BaseInstTest(
  testSet("digraph model table test", DIGRAPH,
    comment("int"),
    testing('nat(53), int, 53, "nat:53 => int"),
    testing(53, 'nat, 'nat(53), "53 => nat"),
    excepting(-51, 'nat, LanguageException.typingError(-51, 'nat), "-51 => nat"),
    // testing('vertex(str("id") -> 'nat(45)),  'nat, 'nat(45), "vertex:('id'->nat:45) => nat"),
  ), testSet("digraph model attr test", DIGRAPH,
    testing(("name" `;` "marko"), 'attr, 'attr(str("key") -> str("name") `_,` str("value") -> str("marko")), "('name';'marko') => attr"),
    testing(("age" `;` 29), 'attr, 'attr(str("key") -> str("age") `_,` str("value") -> int(29)), "('age';29) => attr"),
    testing(__(
      ("name" `;` "marko"), ("age" `;` 29)), 'attr.q(2),
      strm(
        'attr(str("key") -> str("name") `_,` str("value") -> str("marko")),
        'attr(str("key") -> str("age") `_,` str("value") -> int(29))),
      "[('name';'marko'),('age';29)] => attr{2}"),
    excepting((20 `;` "marko"), 'attr, LanguageException.typingError((20 `;` "marko"), 'attr), "(20;'marko') => attr"),
  ), testSet("digraph model vertex test", DIGRAPH,
    comment("vertex directly"),
    testing('vertex(str("id") -> int(12)), __, 'vertex(str("id") -> int(12)), "vertex:('id'->12)"),
    excepting('vertex(str("bad_id") -> int(12)), 'vertex, LanguageException.typingError(str("bad_id") -> int(12), 'vertex), "vertex:('bad_id'->12) => vertex"),
    excepting((str("bad_id") -> int(12)), 'vertex, LanguageException.typingError(str("bad_id") -> int(12), 'vertex), "('bad_id'->12) => vertex"),
    comment("vertex via int"),
    testing(23, 'vertex, 'vertex(str("id") -> 'nat(23)), "23 => vertex"),
    testing('nat(23), 'vertex, 'vertex(str("id") -> 'nat(23)), "nat:23 => vertex"),
    testing(-23, 'vertex, 'vertex(str("id") -> 'nat(23) `_,` str("attrs") -> 'attr(str("key") -> str("no") `_,` str("value") -> str("data"))), "-23 => vertex"),
    excepting(0, 'vertex, LanguageException.typingError((0 `;`("no" `;` "data")).q(qZero), (str("id") -> __("nat") `_,` str("attrs") -> 'attr.q(*)).asInstanceOf[Type[_]]), "0 => vertex"),
    comment("vertex via int/pair"),
    testing((1 `;` 2), ('vertex `;` 'vertex), 'vertex(str("id") -> 'nat(1)) `;` 'vertex(str("id") -> 'nat(2)), "(1;2)=>(vertex;vertex)"),
    testing(
      (32 `;`("name" `;` "marko")),
      as(int `;` 'attr),
      (32 `;` 'attr(str("key") -> str("name") `_,` str("value") -> str("marko"))),
      "(32;('name';'marko')) => (int;attr)"),
    testing(
      (32 `;`("name" `;` "marko")),
      (int `;` 'attr) `=>` 'vertex,
      'vertex(str("id") -> 'nat(32) `_,` str("attrs") -> 'attr(str("key") -> str("name") `_,` str("value") -> str("marko"))),
      "(32;('name';'marko')) => (int;attr) => vertex"),
    testing(
      (32 `;`("name" `;` "marko")),
      'vertex,
      'vertex(str("id") -> 'nat(32) `_,` str("attrs") -> 'attr(str("key") -> str("name") `_,` str("value") -> str("marko"))),
      "(32;('name';'marko')) => vertex"),
    testing(
      ("40" `;`("name" `;` "marko")),
      'vertex,
      'vertex(str("id") -> 'nat(40) `_,` str("attrs") -> 'attr(str("key") -> str("namex") `_,` str("value") -> str("markoxx"))),
      "('40';('name';'marko')) => vertex"),
  ), testSet("digraph model edge test", DIGRAPH,
    testing((1 `;` 2), 'edge, 'edge(str("outV") -> 'vertex(str("id") -> 'nat(1)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(2))), "(1;2)=>edge"),
    testing((1 `;` 2), ('vertex `;` 'vertex) `=>` 'edge, 'edge(str("outV") -> 'vertex(str("id") -> 'nat(1)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(2))), "(1;2)=>(vertex;vertex)=>edge"),
  )) {


  test("play examples") {
    println(engine.eval("1 => edge<=int[model,mm][define,vertex:('id'->int),edge:('outV'->vertex,'inV'->vertex)]-<('outV'->-<('id'->_),'inV'->-<('id'->+1))"))
  }
}