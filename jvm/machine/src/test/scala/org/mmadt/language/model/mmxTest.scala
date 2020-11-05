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

package org.mmadt.language.model

import org.mmadt.language.obj.`type`.__.as
import org.mmadt.language.obj.op.trace.ModelOp.{MM, MMX}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory.str

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmxTest extends BaseInstTest(
  testSet("mm model table test", MM,
    comment("int"),
    //excepting(5,str,LanguageException.typingError(int,str), "5 => str"),
  ),
  testSet("mmx model table test", MMX,
    comment("int"),
    testing(5, as(str), "5", "5[as,str]"),
  ))
