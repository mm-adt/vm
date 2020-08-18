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

package org.mmadt.processor.inst.rewrite


import org.mmadt.language.obj.`type`.__.{id, plus}
import org.mmadt.language.obj.op.RewriteInstruction._
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{testSet, testing}
import org.mmadt.storage.StorageFactory.int

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class IdRewriteTest extends BaseInstTest(
  testSet("[rule:id] rewrite",
    testing(int, id.id.rule(rule_id), int),
    testing(int, id.q(2).id.q(3).rule(rule_id), int.id.q(6)),
    testing(int, id.q(3).id.q(-3).rule(rule_id), int.id.q(-9)),
    testing(int, id.plus(1).id.id.rule(rule_id), int.plus(1)),
    testing(int, id.id.plus(1).id.rule(rule_id), int.plus(1)),
    testing(int, id.id.q(5).plus(1).q(2).id.rule(rule_id), int.plus(1).q(10)),
    testing(int, plus(1).id.plus(id.plus(2)).rule(rule_id), int.plus(1).plus(int.plus(2))),
    testing(int, plus(id.mult(id.id.plus(id).id).id).id.rule(rule_id), int.plus(int.mult(int.plus(int)))),
    testing(int, id.q(2).plus(id.mult(id.q(5).id.plus(id).id).id).q(6).id.rule(rule_id), int.plus(int.mult(int.plus(int).q(5))).q(12)),
  ))
