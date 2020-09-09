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

import org.mmadt.language.obj.Obj.{intToInt, tupleToRecYES}
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__.{id, _}
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.BaseInstTest.model
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory._


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object IdInstTest {
  val IDRULE:Model = (ModelOp.TYPE -> (lst(__) -> lst(g = (",", List((__ `,`) <= '^(id `,`)))) `,`) `,`).asInstanceOf[Model]
}
class IdInstTest extends BaseInstTest(
  testSet("[id] table test",
    comment("int"),
    testing(int(2), int.id, int(2), "2 => int[id]"),
    testing(int(-2), int.id, int(-2), "-2 => int[id]"),
    testing(int, int.id, int.id, "int => int[id]"),
    testing(int(1, 2, 3), int.q(3).id, int(1, 2, 3), "[1,2,3] => int{3}[id]"),
    comment("real"),
    testing(real(2.0), __.id, real(2.0), "2.0 => [id]"),
    testing(real(2.0), real.id.q(10), real(2.0).q(10), "2.0 => real[id]{10}"),
    testing(real(2.0).q(5), real.q(5).id.q(10), real(2.0).q(50), "2.0{5} => real{5}[id]{10}"),
    testing(real(-2.0), real.one.id, real(1.0), "-2.0 => real[one][id]"),
    testing(real, __.id, real.id, "real => [id]"),
    testing(real(1.0, 2.0, 3.0), real.q(3).id, real(1.0, 2.0, 3.0), "[1.0,2.0,3.0] => real{3}[id]"),
    testing(real(1.0, 2.0, 3.0), __.id.q(10), real(real(1.0).q(10), real(2.0).q(10), real(3.0).q(10)), "[1.0,2.0,3.0] => [id]{10}"),
    testing(real(1.0, 2.0, 3.0), real.q(3).id.q(10).id, real(real(1.0).q(10), real(2.0).q(10), real(3.0).q(10)), "[1.0,2.0,3.0] => real{3}[id]{10}[id]"),
    testing(real(1.0, 2.0, 3.0), __.id.q(10).id.q(5), real(real(1.0).q(50), real(2.0).q(50), real(3.0).q(50)), "[1.0,2.0,3.0] => [id]{10}[id]{5}"),
    comment("str"),
    testing(str("a"), str.id, str("a"), "'a' => str[id]"),
    testing(str.id, str.id, str.id.id, "str[id] => str[id]"),
    testing(str("a", "b", "c"), str.q(3).id, str("a", "b", "c"), "['a','b','c']=>str{3}[id]"),
  ),
  testSet("[id] table test w/ id-rule", model("('type' -> (_ -> ((_)<=^:([id]))))"),
    comment("monoid"),
    testing(int, id, int, "int[id]"),
    testing(int.q(-2), int.q(-2).id, int.q(-2), "int{-2}[id]"),
    testing(int, id.id, int, "int[id][id]"),
    testing(int, id.id.id.plus(0).id.id, int.plus(0), "int[id][id][id][plus,0][id][id]"),
    comment("group"),
    // testing(int,id.branch(id`,`id`,`id).id,int.branch(int`,`int`,`int)),
    testing(int, id.plus(id).id, int.plus(int), "int[id][plus,[id]][id]"),
    testing(int, id.plus(id.id.plus(id.id.plus(id).id.id).id.id).id, int.plus(int.plus(int.plus(int))), "int[id][plus,[id][id][plus,[id][id][plus,[id]][id][id]][id][id]][id]"),
    testing(int, id.plus(id.plus(id.plus(id.plus(id)).id).id).id, int.plus(int.plus(int.plus(int.plus(int)))), "int[id][plus,[id][plus,[id][plus,[id][plus,[id]]][id]][id]][id]"),
  ))
