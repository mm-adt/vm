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

package org.mmadt.processor.inst.branch

import org.mmadt.language.obj.Obj.{intToInt, stringToStr}
import org.mmadt.language.obj.`type`.__.merge
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory.{int, lst, obj, str, zeroObj}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class MergeInstTest extends BaseInstTest(
  testSet(",-lst table test",
    comment("no quantifiers"),
    testing(lst(), merge, zeroObj, "( )>-"),
    testing(1 `,`, merge, 1, "(1)>-"),
    testing(1 `,` 2, merge, int(1, 2), "(1,2)>-"),
    testing(1 `,` 2 `,` 3, merge, int(1, 2, 3), "(1,2,3)>-"),
    testing(1 `,` 2 `,` 3 `,` 3, merge, int(1, 2, 3.q(2)), "(1,2,3,3)>-"),
    testing(1 `,` "a" `,` 2 `,` "a", merge, obj(1, "a", 2, "a"), "(1,'a',2,'a')>-"),
    comment("quantifiers"),
    testing(lst().q(10), merge, zeroObj, "( ){10}>-"),
    testing((1.q(2) `,`).q(5), merge, 1.q(10), "(1{2}){5}>-"),
    testing((1.q(2) `,` 2.q(3)).q(4), merge.q(5), int(1.q(40), 2.q(60)), "(1{2},2{3}){4}>-{5}"),
    testing(1 `,` 2.q(10) `,` 3, merge.q(2), int(1.q(2), 2.q(20), 3.q(2)), "(1,2{10},3)>-{2}"),
    testing((1 `,` 2 `,` 3 `,` 3).q(2, 5), merge.q(10), int(1.q(20, 50), 2.q(20, 50), 3.q(40, 100)), "(1,2,3,3){2,5}>-{10}"),
    testing(1 `,` "a".q(10) `,` 2 `,` "a".q(2), merge.q(3), obj(1.q(3), "a".q(30), 2.q(3), "a".q(6)), "(1,'a'{10},2,'a'{2})>-{3}"),
  ), testSet(";-lst table test",
    comment("no quantifiers"),
    testing(lst(), merge, zeroObj, "()>-"),
    testing(1 `;`, merge, 1, "(1)>-"),
    testing(1 `;` 2 `;` 3, merge, 3, "(1;2;3)>-"),
    testing(1 `;` "a" `;` 4.0, merge, 4.0, "(1;'a';4.0)>-"),
    comment("quantifiers"),
    // testing(1.q(2) `;` 2.q(3) `;` 3, merge, 3.q(6), "(1{2};2{3};3)>-"), // TODO: solve [split]/[merge] scalar multiplication timing
  ), testSet("|-lst table test",
    comment("no quantifiers"),
    testing(lst(), merge, zeroObj, "()>-"),
    testing(int(1) `|`, merge, 1, "(1)>-"),
    testing(int(1) `|` 2 `|` 3, merge, 1, "(1|2|3)>-"),
    testing(str("a") `|` 2 `|` "a", merge, "a", "('a'|2|'a')>-"),
  ))