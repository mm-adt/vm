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
import org.mmadt.processor.inst.TestSetUtil.{comment, excepting, testSet, testing}
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
    comment("attr"),
    testing(("name" `;` "marko"), 'attr, 'attr(str("key") -> str("name") `_,` str("value") -> str("marko")), "('name';'marko') => attr"),
    testing(("age" `;` 29), 'attr, 'attr(str("key") -> str("age") `_,` str("value") -> int(29)), "('age';29) => attr"),
    /*testing(__(
      ("name" `;` "marko"), ("age" `;` 29)), id.map(id).as('attr).map(id),
      strm(
        'attr(str("key") -> str("name") `_,` str("value") -> str("marko")),
        'attr(str("key") -> str("age") `_,` str("value") -> int(29))),
      "[('name';'marko'),('age';29)] => [map,id][as,attr]"),*/
    excepting((20 `;` "marko"), 'attr, LanguageException.typingError((20 `;` "marko"), 'attr), "(20;'marko') => attr"),
    comment("vertex via int"),
    testing(23, 'vertex, 'vertex(str("id") -> 'nat(23) `,`), "23 => vertex"),
    testing('nat(23), 'vertex, 'vertex(str("id") -> 'nat(23) `,`), "nat:23 => vertex"),
    testing(-23, 'vertex, 'vertex(str("id") -> 'nat(23) `_,` str("attrs") -> 'attr(str("key") -> str("no") `_,` str("value") -> str("data"))), "-23 => vertex"),
    excepting(0, 'vertex, LanguageException.typingError((0 `;`("no" `;` "data")).q(qZero), (str("id") -> __("nat") `_,` str("attrs") -> 'attr.q(*)).asInstanceOf[Type[_]]), "0 => vertex"),
    comment("vertex via int/pair"),
    testing(
      (32 `;`("name" `;` "marko")),
      as(int `;` 'attr),
      (32 `;` 'attr(str("key") -> str("name") `_,` str("value") -> str("marko"))),
      "(32;('name';'marko')) => [as,(int;attr)]"), // shouldn't need [as]
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
  ))