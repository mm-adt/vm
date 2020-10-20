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

package org.mmadt.processor.inst.sideeffect

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.Obj.{intToInt, symbolToToken, tupleToRecYES}
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp.{MM, MMX, NONE}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{IGNORING, testSet, testing}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class PutInstTest extends BaseInstTest(
  testSet("[put] ,-lst test", List(NONE, MM, MMX),
    testing(1 `,` 2 `,` 3,
      put(0, 0),
      0 `,` 1 `,` 2 `,` 3,
      "(1,2,3)[put,0,0]"),
  ),
  testSet("[put] ;-lst test", List(NONE, MM, MMX),
    testing(1 `;` 2 `;` 3,
      put(0, 0),
      0 `;` 1 `;` 2 `;` 3,
      "(1;2;3)[put,0,0]"),
    testing(1,
      int.-<(__ `;` plus(1) `;` plus(2)).put(0, 0),
      0 `;` 1 `;` 2 `;` 4,
      "1 => int-<(_;+1;+2)[put,0,0]"),
    IGNORING("eval-[5-6]")(1,
      int.to('x).-<(__ `;` plus(1) `;` plus(2) `;` 'x).put(0, 0),
      0 `;` 1 `;` 2 `;` 4 `;` 1,
      "1 => int<x>-<(_;+1;+2;x)[put,0,0]"),
    IGNORING("eval-[5-6]")(1,
      int.to('x).-<(__ `;` plus(1) `;` plus(2) `;` from('x)).put(0, 0),
      0 `;` 1 `;` 2 `;` 4 `;` 1,
      "1 => int<x>-<(_;+1;+2;<.x>)[put,0,0]"),
    IGNORING("eval-[5-6]")(1,
      int.to('x).-<(__ `;` plus(1) `;` plus(2) `;` 'x).put(0, 'x.plus(2)),
      3 `;` 1 `;` 2 `;` 4 `;` 1,
      "1 => int<x>-<(_;+1;+2;x)[put,0,x+2]"),
    IGNORING("eval-[5-6]")(1,
      int.to('x).-<(__ `;` plus(1) `;` plus(2) `;` from('x)).put(0, 'x.plus(2)),
      3 `;` 1 `;` 2 `;` 4 `;` 1,
      "1 => int<x>-<(_;+1;+2;<.x>)[put,0,x+2]"),
    IGNORING("eval-[5-6]")(1,
      int.to('x).-<(__ `;` plus(1) `;` plus(2) `;` from('x)).put(0, 'x.plus(2)).merge,
      1,
      "1 => int<x>-<(_;+1;+2;<.x>)[put,0,x+2]>-"),
    IGNORING("eval-[5-6]")(1,
      int.to('x).-<(__ `;` plus(1) `;` plus(2) `;` from('x)).to('y).put(0, 'x.plus(2)).to('z).merge.-<('y `;` 'z).put(0, 6),
      lst[Obj](g = (Tokens.`;`, List(int(6), (1 `;` 2 `;` 4 `;` 1), (3 `;` 1 `;` 2 `;` 4 `;` 1)))),
      "1 => int<x>-<(_;+1;+2;<.x>)<y>[put,0,x+2]<z>>--<(y;z)[put,0,6]"),
  ),
  testSet("[put] ,-rec test", List(NONE, MM, MMX),
    testing(str("name") -> str("marko") `_,` str("age") -> int(29),
      id,
      str("name") -> str("marko") `_,` str("age") -> int(29),
      "('name'->'marko','age'->29)"),
    testing(str("name") -> str("marko") `_,` str("age") -> int(29),
      put("name", "marko"),
      str("name") -> str("marko") `_,` str("age") -> int(29),
      "('name'->'marko','age'->29)[put,'name','marko']"),
    testing(str("name") -> str("marko") `_,` str("age") -> int(29),
      put("name", "kuppitz"),
      str("name") -> str("kuppitz") `_,` str("age") -> int(29),
      "('name'->'marko','age'->29)[put,'name','kuppitz']"),
    testing(str("name") -> str("marko") `_,` str("age") -> int(28),
      put("age", 29),
      str("name") -> str("marko") `_,` str("age") -> int(29),
      "('name'->'marko','age'->28)[put,'age',29]"),
  ))
