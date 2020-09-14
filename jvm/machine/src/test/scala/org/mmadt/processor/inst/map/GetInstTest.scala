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

package org.mmadt.processor.inst.map

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.Obj.{intToInt, stringToStr, tupleToRecYES}
import org.mmadt.language.obj.`type`.__._
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, excepting, testSet, testing}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class GetInstTest extends BaseInstTest(
  testSet("[get] ,-lst test",
    comment(",-lst type index"),
  ),
  testSet("[get] ;-lst test",
    comment(";-lst int index"),
    testing(lst, get(0, int), lst.get(0, int), "lst => [get,0,int]"),
    testing(1 `;` 2 `;` 3, get(0), 1, "(1;2;3)[get,0]"),
    testing(1 `;` 2 `;` 3, get(1), 2, "(1;2;3)[get,1]"),
    testing(1 `;` 2 `;` 3, get(2, int), 3, "(1;2;3)[get,2,int]"),
    testing(1 `;` 2 `;` 3.q(5), get(2, int), 3.q(5), "(1;2;3{5})[get,2,int]"),
    testing(1 `;` (2 `;` 3) `;` 4, get(1), (2 `;` 3), "(1;(2;3);4).1"),
    testing(1 `;` (2 `;` 3).q(10) `;` 4, get(1).q(2), (2 `;` 3).q(20), "(1;(2;3){10};4).1{2}"),
    testing((1 `;` (2 `;` 3).q(10) `;` 4).q(5), get(1).q(2), (2 `;` 3).q(100), "(1;(2;3){10};4){5}.1{2}"),
    testing((int `;` (int `;` int).q(10) `;` int).q(5), get(1).q(2), (int `;` int).q(100) <= (int `;` (int `;` int).q(10) `;` int).q(5).get(1).q(2), "(int;(int;int){10};int){5}.1{2}"),
    // testing(1 `;` 2 `;` 3, get(2, str), "3", "(1;2;3)[get,2,str]"),
    comment(";-lst type index"),
    testing(1 `;` 2 `;` 3, lst.get(int.is(gt(0))), int(2, 3), "(1;2;3) => lst[get,int[is>0]]"),
    // testing(1 `;` 2 `;` 3, lst.get(is(gt(0))), int(2, 3), "(1;2;3) => lst[get,[is>0]]"),
    testing(1 `;` 2.q(10) `;` 3, get(int.is(gt(0))), int(2.q(10), 3), "(1;2{10};3)[get,int[is>0]]"),
    testing(1 `;` 2.q(10) `;` 2.q(20), get(int.is(gt(0))), 2.q(30), "(1;2{10};2{20})[get,int[is>0]]"),
    testing(1 `;` 2.q(10) `;` 2.q(20), get(int.is(gt(0))).q(100), 2.q(3000), "(1;2{10};2{20})[get,int[is>0]]{100}"),
    testing(1 `;` 2 `;` 3, get(get(0)), 2, "(1;2;3) => [get,.0]"),
    testing(1 `;` 2 `;` 3, lst.get(get(0, int)), 2, "(1;2;3) => lst[get,.0,int]"),
    testing(1 `;` 2 `;` 3, (int `;` int `;` int).get(get(0).plus(1)), 3, "(1;2;3) => (int;int;int)[get,.0+1]"),
    comment(";-lst exceptions"),
    excepting(lst(), get(0), LanguageException.Poly.noIndexValue(lst(), 0), "()[get,0]"),
    excepting(lst(), get(0), LanguageException.Poly.noIndexValue(lst(), 0), "().0"),
    excepting(1 `;` 2 `;` 3, get(10), LanguageException.Poly.noIndexValue(1 `;` 2 `;` 3, 10), "(1;2;3).10"),
    excepting(1 `;` 2 `;` 3, get(-1), LanguageException.Poly.noIndexValue(1 `;` 2 `;` 3, -1), "(1;2;3).-1"),
    excepting(1 `;` 2 `;` 3, get(-1), LanguageException.Poly.noIndexValue(1 `;` 2 `;` 3, -1), "(1;2;3)[get,-1]"),
  ),
  testSet("[get] |-lst test",
    comment("|-lst int index"),
    testing("a" `|` "b" `|` "c", get(0), "a", "('a'|'b'|'c').0"),
    testing(("a".q(2) `|` "b" `|` "c").q(5), get(0).q(10), "a".q(100), "('a'{2}|'b'|'c'){5}.0{10}"),
    testing("a".q(10) `|` "b".q(0) `|` "c", get(0).q(4), "a".q(40), "('a'{10}|'b'{0}|'c').0{4}"), // TODO: should we follow the infix convention of .{4}0 ?
    testing("a".q(0) `|` "b" `|` "c", get(0), "b", "('a'{0}|'b'|'c').0"),
    comment("|-lst exceptions"),
    excepting("a" `|` "b".q(0) `|` "c".q(0), get(1), LanguageException.Poly.noIndexValue("a" `|`, 1), "('a'|'b'{0}|'c'{0})[get,1]"),
    excepting("a".q(0) `|` "b".q(0), get(0), LanguageException.Poly.noIndexValue("a".q(0) `|` "b".q(0), 0), "('a'{0}|'b'{0}).0"),
  ),
  testSet("[get] ,-rec test",
    comment(",-rec value index"),
    testing(str("a") -> int(1) `_,` str("a") -> int(1) `_,` str("b") -> int(3), get("a"), 1.q(2), "('a'->1,'a'->1,'b'->3).a"),
    testing(str("a") -> int(1) `_,` str("a") -> 1.q(5) `_,` str("b") -> int(3), get("a").q(10), 1.q(60), "('a'->1,'a'->1{5},'b'->3).a{10}"),
    testing(str("a") -> int(1) `_,` str("a") -> 2.q(5) `_,` str("b") -> int(3), get("a"), int(1, 2.q(5)), "('a'->1,'a'->2{5},'b'->3).a"),
    testing(str("a") -> int(1) `_,` str("a") -> 2.q(5) `_,` str("b") -> int(3), get("a").q(10), int(1.q(10), 2.q(50)), "('a'->1,'a'->2{5},'b'->3).a{10}"),
    testing(str("a") -> int(1) `_,` str("a") -> int(2) `_,` str("b") -> int(3), get("a").q(10), int(1.q(10), 2.q(10)), "('a'->1,'a'->2,'b'->3).a{10}"),
    testing(str("a") -> 1.q(2, 3) `_,` str("a") -> 2.q(7) `_,` str("b") -> 3.q(2), get("a").q(10), int(1.q(20, 30), 2.q(70)), "('a'->1{2,3},'a'->2{7},'b'->3{2}).a{10}"),
    testing(str("a") -> 1.q(2, 3) `_,` str("a") -> 1.q(7) `_,` str("b") -> 3.q(2), get("a").q(10), 1.q(90, 100), "('a'->1{2,3},'a'->1{7},'b'->3{2}).a{10}"),
    testing((str("a") -> 1.q(2, 3) `_,` str("a") -> 1.q(7) `_,` str("b") -> 3.q(2)).q(20), get("a").q(10), 1.q(1800, 2000), "('a'->1{2,3},'a'->1{7},'b'->3{2}){20}.a{10}"),
    testing((str("a") -> int.q(2, 3) `_,` str("a") -> int.q(7) `_,` str("b") -> int.q(2)).q(20), get("a").q(10), int.q(1800, 2000) <= (str("a") -> int.q(2, 3) `_,` str("a") -> int.q(7) `_,` str("b") -> int.q(2)).q(20).get("a").q(10), "('a'->int{2,3},'a'->int{7},'b'->int{2}){20}.a{10}"),
    comment(",-rec type index"),
    testing(int(1) -> int(1) `_,` int(100) -> int(2) `_,` int(200) -> int(3), get(int.is(gt(50))), int(2, 3), "(1->1,100->2,200->3)[get,int[is>50]]"),
    testing(int(1) -> int(1) `_,` int(100) -> 2.q(10) `_,` int(200) -> 2.q(20), get(int.is(gt(50))).q(100), 2.q(3000), "(1->1,100->2{10},200->2{20})[get,int[is>50]]{100}"),
    comment(",-rec exceptions"),
    // testing(str("a") -> int(1) `_,` str("a") -> int(1) `_,` str("b") -> int(3), get("c"), LanguageException.Poly.noKeyValue(str("a") -> int(1) `_,` str("a") -> int(1) `_,` str("b") -> int(3), "c"), "('a'->1,'a'->1,'b'->3).c"),
  ),
  testSet("[get] |-rec test",
    comment("|-rec value index"),
    testing(str("a") -> int(1) `|` str("a") -> int(1) `|` str("b") -> int(3), get("a"), 1, "('a'->1|'a'->1|'b'->3).a"),
    testing(str("a") -> 1.q(5) `|` str("a") -> 1.q(5) `|` str("b") -> int(3), get("a").q(10), 1.q(50), "('a'->1{5}|'a'->1{5}|'b'->3).a{10}"),
    testing((str("a") -> 1.q(5) `|` str("a") -> 1.q(5) `|` str("b") -> int(3)).q(20), get("a").q(10), 1.q(1000), "('a'->1{5}|'a'->1{5}|'b'->3){20}.a{10}"),
  ))