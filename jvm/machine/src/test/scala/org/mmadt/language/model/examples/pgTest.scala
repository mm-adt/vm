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
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.{Obj, asType}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil._
import org.mmadt.storage
import org.mmadt.storage.StorageFactory._


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class pgTest extends BaseInstTest(
  testSet("property graph #1", storage.model('pg_1),
    comment("vertex"),
    testing((str("id") -> int(1)), 'vertex, 'vertex(str("id") -> int(1)), "('id'->1) => vertex"),
    comment("edge"),
    // excepting(lst(g=(Tokens.`;`,List(rec(str("id") -> int(1)), rec(str("id") -> int(2))))), ('vertex `;` 'vertex) `=>` 'edge, LanguageException.typingError(lst(g=(Tokens.`;`,List(rec(str("id") -> int(1)), rec(str("id") -> int(2))))), 'edge), "(('id'->1);('id'->2))=>(vertex;vertex)=>edge"),
    excepting(lst(g = (Tokens.`;`, List(rec(str("id") -> int(1)), rec(str("id") -> int(2))))), 'edge, LanguageException.typingError(rec(str("id") -> int(1)) `;` rec(str("id") -> int(2)), 'edge), "(('id'->1);('id'->2))=>edge"),
    comment("exceptions"),
    excepting(6, 'vertex, LanguageException.typingError(6, 'vertex), "6 => vertex"),
    excepting(7, 'edge, LanguageException.typingError(7, 'edge), "7 => edge"),
    excepting((8 `;` 9), 'edge, LanguageException.typingError((8 `;` 9), 'edge), "(8;9) => edge"),
    excepting((1 `;` 2), ('vertex `;` 'vertex), LanguageException.typingError(1 `;` 2, asType('vertex `;` 'vertex)), "(1;2) => (vertex;vertex)"),
  ), testSet("property graph #2", storage.model('pg_2),
    comment("int=>vertex"),
    testing(5, 'vertex, 'vertex(str("id") -> int(5)), "5 => vertex"),
    testing(5, int.-<('vertex `;` 'vertex), lst(g = (Tokens.`;`, List('vertex(str("id") -> int(5)), 'vertex(str("id") -> int(5))))), "5-<(vertex;vertex)"),
    excepting("6", 'vertex, LanguageException.typingError("6", 'vertex), "'6' => vertex"),
    comment("(int;int)=>(vertex;vertex)=>edge"),
    testing((1 `;` 2), ('vertex `;` 'vertex), ('vertex(str("id") -> int(1)) `;` 'vertex(str("id") -> int(2))), "(1;2) => (vertex;vertex)"),
    testing((3 `;` 4), ('vertex `;` 'vertex) `=>` 'edge, 'edge(str("outV") -> 'vertex(str("id") -> int(3)) `_,` str("inV") -> 'vertex(str("id") -> int(4))), "(3;4) => (vertex;vertex) => edge"),
    testing((5 `;` 6), 'edge, 'edge(str("outV") -> 'vertex(str("id") -> int(5)) `_,` str("inV") -> 'vertex(str("id") -> int(6))), "(5;6) => edge"),
    testing(5, int.-<(__`;`plus(1)).as('vertex`;`'vertex).as('edge), 'edge(str("outV") -> 'vertex(str("id") -> int(5)) `_,` str("inV") -> 'vertex(str("id") -> int(6))), "5-<(_;+1)=>(vertex;vertex)=>edge"),
  ), testSet("int=>(vertex;vertex)", storage.model('pg_2).defining(('vertex `;` 'vertex) <= (int.-<('vertex `;` 'vertex))),
    testing(1, ('vertex `;` 'vertex), ('vertex(str("id") -> int(1)) `;` 'vertex(str("id") -> int(1))), "1 => (vertex;vertex)"),
    testing(int(1, 2), ('vertex `;` 'vertex), strm[Obj](('vertex(str("id") -> int(1)) `;` 'vertex(str("id") -> int(1))), ('vertex(str("id") -> int(2)) `;` 'vertex(str("id") -> int(2)))), "[1,2] => (vertex;vertex)"),
  ), testSet("property graph #4", storage.model('pg_4),
    testing(8, 'vertex, 'vertex(str("id") -> 'nat(8) `_,` str("label") -> str("vertex")), "8 => vertex"),
    IGNORING("eval-.","query-2")((9 `;` "person"`;`"name"`;`"marko"), 'vertex,
      'vertex(str("id") -> 'nat(9) `_,` str("label") -> str("person"))/*`_,`str("props")->(str("name")->str("marko")))*/, "" +
        "(9;'person';'name';'marko') => vertex"),
  ))