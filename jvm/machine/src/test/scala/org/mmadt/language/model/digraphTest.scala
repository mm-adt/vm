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
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.BaseInstTest.engine
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
  )) {
  test("mm-ADT int") {
   /* println(engine.eval(s":[model,${storage.model("digraph")}]"))
    //println(engine.eval("[map,digraph]"))
    println(engine.eval("(32;('name';'marko')) => [as,(int;attr)]"))
    println(engine.eval("(32;('name';'marko')) => (int;attr) => vertex"))
    println(engine.eval("(32;('name';'marko')) => vertex"))
    println(engine.eval("23 => vertex"))
    println(engine.eval("-23 => vertex"))
    engine.eval(":{1}")*/
  }
}