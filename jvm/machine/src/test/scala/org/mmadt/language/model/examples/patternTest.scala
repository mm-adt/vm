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
import org.mmadt.language.obj.Obj.{intToInt, stringToStr, symbolToToken}
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__.symbolToRichToken
import org.mmadt.language.obj.op.sideeffect.LoadOp
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{testing, _}
import org.mmadt.storage
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object patternTest {
  val PATTERN:Model = storage.model(LoadOp.loadObj[Model](getClass.getResource("/test/pattern.mm").getPath))
}
class patternTest extends BaseInstTest(
  testSet("basic patterns", PATTERN,
    comment("ipair"),
    IGNORING("eval-.", "query-2")(int, 'ipair, 'ipair(int `;` int) <= (int `=>`('ipair(int `;` int) <= int.split(int `;` int))), "int => ipair"),
    IGNORING("eval-.", "query-2")(int, 'ipair `=>` 'isnd, 'isnd(int) <= ((int `=>`('ipair(int `;` int) <= int.split(int `;` int))) `=>`('isnd(int) <= 'ipair(int `;` int).get(1))), "int => ipair => isnd"),
    testing(5, 'ipair, 'ipair(5 `;` 5), "5 => ipair"),
    testing('ipair(5 `;` 5), (int `;` int), (5 `;` 5), "ipair:(5;5) => (int;int)"),
    IGNORING("eval-.", "query-2")(int, 'ipair ==> (int `;` int), (int `;` int) <= (int.`=>`('ipair <= int.split(int `;` int))), "int => ipair => (int;int)"),
    //IGNORING("eval-.")(5, 'ipair ~> (int `;` int), (5 `;` 5), "5 => ipair => (int;int)"),
    excepting("4", 'ipair, LanguageException.typingError("4", 'ipair), "'4' => ipair"),
    excepting("five", 'ipair, LanguageException.typingError("five", 'ipair), "'five' => ipair"),
    IGNORING("eval-.", "query-2")(int, 'ipair, int `=>`('ipair(int `;` int) <= int -< (int `;` int)), "int => ipair"),
    comment("pair"),
    testing(5, 'pair, 'pair(5 `;` 5), "5 => pair"),
    testing("4", 'pair, 'pair("4" `;` "4"), "'4' => pair"),
    testing("five", 'pair, 'pair("five" `;` "five"), "'five' => pair"),
    // IGNORING("eval-.", "query-2")(int, 'pair, int ~> ('pair <= int -< (int `;` int)), "int => pair"),
    IGNORING("eval-.")(6, int ==> int.split(__ `;` __) `=>`(int `;`(int `=>` 'ipair)) `=>` 'pair, 'pair(6 `;` 'ipair(6 `;` 6)),
      "6 =>-<(_;_)=>(int;int=>ipair)=>pair",
      "6 => int-<(int;int)=>(int;int=>ipair)=>pair",
      "6 =>-<(_;_)=>pair:(int;int=>ipair)",
      "6 => int-<(int;ipair)=>pair",
      "6 => int-<pair:(int;ipair)",
    ),
    comment("fst and snd"),
    IGNORING("eval-.", "query-2")('ipair, 'ifst, 'ifst <= ('ipair(int `;` int) `=>`('ifst <= 'ipair(int `;` int).get(0))), "ipair => ifst"),
    testing('ipair(1 `;` 2), 'ifst, 'ifst(1), "ipair:(1;2) => ifst"),
    testing((1 `;` 2), 'ifst, 'ifst(1), "(1;2) => ifst"),
    excepting(("one" `;` "two"), 'ifst, LanguageException.typingError("one" `;` "two", 'ifst), "('one';'two') => ifst"),
    testing(("one" `;` "two"), 'fst, 'fst("one"), "('one';'two') => fst"),
    testing((1 `;` 2), 'ipair.combine(int.plus(2) `;` int.plus(3)), 'ipair(3 `;` 5), "(1;2)=>ipair=(+2;+3)"),
    testing((1 `;` 2), 'pair.combine('ipair `;` int.plus(3)), lst(name = "pair", g = (Tokens.`;`, List('ipair(1 `;` 1), int(5)))),
      "(1;2)=>pair=>=(ipair;+3)=>pair",
      "(1;2)=>(int;int)=>(ipair;int+3)=>pair",
      "(1;2)=>(int;int)=>(ipair;int)=>pair=>=(_;+3)",
      "(1;2)=>(int;int)=>(ipair;int+3)=>(_;int)=>pair",
      "(1;2)=>(int;int)=>(ipair;int+3)=>pair=>pair=>pair",
      "(1;2)=>(int;int)=>(ipair;int+3)=>pair=>=(ipair;int)=>pair",
      "(1;2)=>(int;int)=>(ipair;int+3)=>pair=>=(_;_)=>pair",
      "(1;2)=>(int;int)=>(ipair;int+3)=>pair=>(_;_)=>pair",
      "(1;2)=>(int;int)=>pair:(ipair;int+3)",
      "(1;2)=>pair=>=(int;int)=>=(ipair;+3)",
      "(1;2)=>pair=>=(int;int)=>=(ipair;+3)=>pair",
      "(1;2)=>pair=>=(int;int)=>=(ipair;+3)=>=(ipair;int)=>pair",
      "(1;2)=>pair=>=(int;int)=>=(ipair;+2)=>=(ipair;int+1)=>pair",
      "(1;2)=>pair=>(int;int)=>(ipair;+3)=>pair"
    )
  ),
  testSet("custom instructions", PATTERN,
    IGNORING("eval-[4-5]", "query-2")(2 `;` 3, (int `;` int).branch(lst(__('aplus))), 5,
      "(2;3)[aplus]",
      "(2;3)=>aplus",
      "(2;3)=>(int;int)=>aplus=>int",
      "(2;3)=>(dble;dble)=>(int+-2;int+-3)=>aplus=>int",
      "(2;3)=>(dble+-2;dble+-3)=>aplus=>int"
    ),
  ))
