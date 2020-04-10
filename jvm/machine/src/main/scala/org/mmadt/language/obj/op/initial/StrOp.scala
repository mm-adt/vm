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

package org.mmadt.language.obj.op.initial

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.{StrType, __}
import org.mmadt.language.obj.op.InitialInstruction
import org.mmadt.language.obj.{Inst, Obj, Str}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait StrOp {
  this: __ =>
  def str(): StrType = this.compose(StorageFactory.str, StrOp()).hardQ(qOne)
}

object StrOp {
  def apply(): Inst[Obj, Str] = new StrInst()

  class StrInst() extends VInst[Obj, Str]((Tokens.str, Nil)) with InitialInstruction {
    override def exec(start: Obj): Str = str
  }

}