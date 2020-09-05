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

package org.mmadt.processor.inst.trace

import org.mmadt.language.obj.Obj.tupleToRecYES
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory.{?, int, str}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TypeInstTest extends BaseInstTest(
  testSet("[type] table",
    comment("[noop]"),
    testing(__, __.`type`, __.`type`, "[type]"),
    comment("[plus][mult]"),
    testing(2, `type`, int, "2[type]"),
    testing(2, plus(10).mult(plus(int)).`type`, int.plus(10).mult(int.plus(int)), "2+10[mult,+int][type]"),
    comment("[is]"),
    testing(5, plus(1).is(gt(3)).`type`, int.q(?) <= int.plus(1).is(gt(3)), "5[plus,1][is>3][type]"),
    testing(5, int.plus(1).is(gt(3)).`type`, int.q(?) <= int.plus(1).is(gt(3)), "5 => int[plus,1][is>3][type]"),
    comment("[type]"),
    testing(int, int.plus(1).`type`, int.plus(1).`type`, "int => int[plus,1][type]"),
    testing(int, plus(1).`type`, int.plus(1).`type`, "int => int[plus,1][type]"),
    testing(__, int.plus(1).`type`, int.plus(1).`type`, "int => int[plus,1][type]"),
    testing(__, plus(1).`type`, plus(1).`type`, "_ => [plus,1][type]"),
    testing(6, int.plus(1).`type`.`type`, int.plus(1).`type`, "6 => int[plus,1][type][type]"),
    comment("[swap]"),
    testing("a", swap(plus("b")).`type`, str.swap(plus("b")), "'a'[swap,+'b'][type]"),
    testing("a", plus(str).swap(plus("b")).`type`, str.plus(str).swap(plus("b")), "'a'[plus,str][swap,+'b'][type]"),
    comment("[split]"),
    testing(5, int.split(plus(1) `,` plus(2)).`type`, int.split(plus(1) `,` plus(2)), "5 => int-<(+1,+2)[type]"),
    testing(5, int.split(plus(1) `;` plus(2)).`type`, int.split(plus(1) `;` plus(2)), "5 => int-<(+1;+2)[type]"),
    testing(5, int.split(int -> plus(1) `_,` is(gt(10)) -> plus(2)).`type`, int.split(int -> int.plus(1)), "5 => int-<(int -> +1, [is>10] -> +2)[type]"), // you lose the >10 branch
  ))