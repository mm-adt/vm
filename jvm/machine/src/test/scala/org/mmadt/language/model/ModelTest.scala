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

package org.mmadt.language.model

import org.mmadt.language.obj.Str
import org.mmadt.language.obj.`type`.Type
import org.mmadt.processor.Processor
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ModelTest extends FunSuite {

  test("simulating model values"){
    // TODO: this needs a lot of work (seeing if I can get away with variables stored in the model as named types)
    val model  :Model     = Model(tstr("x") -> str.plus("hello"))
    val program:Type[Str] = str.plus(tstr("x"))
    println(model)
    // println(model.resolve(tstr("x")) + "!!")
    println(program)
    println(Processor.compiler(model)(program))
    println(Processor.iterator(model)(str("say "),program))
  }

}