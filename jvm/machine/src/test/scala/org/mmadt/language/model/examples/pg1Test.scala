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

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.Obj.{intToInt, symbolToToken, tupleToRecYES}
import org.mmadt.language.obj.`type`.__._
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil._
import org.mmadt.storage
import org.mmadt.storage.StorageFactory._


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class pg1Test extends BaseInstTest(
  testSet("pg_1 model table test", storage.model('pg_1),
  ), testSet("pg_2 model table test", storage.model('pg_2),
    comment("int=>vertex"),
    testing(5, 'vertex, 'vertex(str("id") -> int(5)), "5 => vertex"),
    excepting("6", 'vertex, LanguageException.typingError("6", 'vertex), "'6' => vertex"),
    comment("(int;int)=>(vertex;vertex)=>edge"),
    testing((1 `;` 2), ('vertex `;` 'vertex), ('vertex(str("id") -> int(1)) `;` 'vertex(str("id") -> int(2))), "(1;2) => (vertex;vertex)"),
    testing((3 `;` 4), ('vertex `;` 'vertex) `=>` 'edge, 'edge(str("outV") -> 'vertex(str("id") -> int(3)) `_,` str("inV") -> 'vertex(str("id") -> int(4))), "(3;4) => (vertex;vertex) => edge"),
    testing((5 `;` 6), 'edge, 'edge(str("outV") -> 'vertex(str("id") -> int(5)) `_,` str("inV") -> 'vertex(str("id") -> int(6))), "(5;6) => edge"),
    comment("int=>(vertex;vertex)"),
    // testing(1, ('vertex `;` 'vertex), ('vertex(str("id") -> int(1)) `;` 'vertex(str("id") -> int(1))), "1 => (vertex;vertex)"),
  )) {

  test("play") {
    println((5 `;` 6).model('pg_2) `=>`('vertex `;` 'vertex))
  }
}
