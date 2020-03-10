/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.model.rewrite

import org.mmadt.language.model.Algebra
import org.mmadt.processor.obj.`type`.CompilingProcessor
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class BranchRewriteTest extends FunSuite {

  test("branch rewrites"){
    // TODO: equality on choose branches
    val processor = new CompilingProcessor(Algebra.ring(int))
    //   assertResult(int.plus(10).choose(int.is(int.gt(10)) -> int,int -> int.zero()))(processor(int.plus(10).choose(int.is(int.gt(10)) -> int.plus(int.zero()),int -> int.plus(int.neg()))))
    //   assertResult(int.plus(10).choose(int.is(int.gt(10)) -> int,int.is(int.eqs(1)) -> int.zero()))(processor(int.plus(10).choose(int.is(int.gt(10)) -> int.plus(int.zero()),int.is(int.eqs(1)) -> int.plus(int.neg()))))
  }

}
