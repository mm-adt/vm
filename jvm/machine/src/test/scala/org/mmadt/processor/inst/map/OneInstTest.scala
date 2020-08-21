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
import org.mmadt.language.obj.Obj.{doubleToReal, intToInt}
import org.mmadt.language.obj.`type`.__.{id, one}
import org.mmadt.language.obj.op.map.OneOp
import org.mmadt.language.obj.op.trace.ModelOp.MM
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class OneInstTest extends BaseInstTest(
  testSet("[one] table test",
    comment("int"),
    testing(2, one, 1),
    testing(2, one.q(10), 1.q(10)),
    testing(2.q(10), one, 1.q(10)),
    testing(2.q(10), one.q(20), 1.q(200)),
    testing(-2, one, 1),
    testing(int, one, int.one),
    testing(int, one.q(10), int.one.q(10)),
    testing(int.q(10), one, int.q(10).one),
    testing(int.q(10), one.q(20), int.q(10).one.q(20)),
    testing(int(1, 2, 3), one, 1.q(3)),
    comment("real"),
    testing(2.0, one, 1.0),
    testing(-2.0, one, 1.0),
    testing(real, one, real.one),
    testing(real(-1.0, -2.0, -3.0), one, 1.0.q(3)),
    testing(real(-1.0, -2.0, -3.0), id.q(10).one, 1.0.q(30)),
    testing(real(-1.0, -2.0, -3.0), id.q(10).one, 1.0.q(30)),
    testing(real(-1.0, -2.0, -3.0), real.q(3).id.q(10).one.q(3), 1.0.q(90)),
    comment("exceptions"),
    testing("a", one, LanguageException.unsupportedInstType(str("a"), OneOp()), "'a'[one]")
  ),
  testSet("[one] table test w/ mm", MM,
    comment("int"),
    testing(int, one, 1),
    testing(int, one.q(5), 1.q(5)),
    testing(int.q(3), id.q(2).one.q(5).id.q(10), 1.q(300)),
    testing(int.q(2), int.q(2).one.q(5), 1.q(10)),
  ))