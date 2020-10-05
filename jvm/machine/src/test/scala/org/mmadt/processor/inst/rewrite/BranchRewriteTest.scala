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

import org.mmadt.language.obj.`type`.__.branch
import org.mmadt.language.obj.op.RewriteInstruction.rule_branch
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{testSet, testing}
import org.mmadt.storage.StorageFactory.int

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class BranchRewriteTest extends BaseInstTest(
  testSet("[rule:branch] rewrite",
    testing(int, branch(int `;` int).rule(rule_branch), int),
    testing(int, branch(int.id `;` int.id).rule(rule_branch), int),
    testing(int.q(2), int.q(2).branch(int.id `;` int.id).rule(rule_branch), int.q(2)),
    testing(int.q(2), int.q(2).branch(int.id.q(5) `;` int.id).rule(rule_branch), int.q(10) <= int.q(2).id.q(5)),
    //testing(__, branch(__ `;` __).rule(rule_unity), __),
    testing(int, branch(branch(int `;` int) `,` branch(int `;` int)).rule(rule_branch), int.branch(int `,` int)),
    //testing(int, branch(branch(int `;` int.id.q(5)) `,` branch(int `;` int)).rule(rule_unity), int.q(6) <= int.branch(int.id.q(5) `,` int)),
  ))
