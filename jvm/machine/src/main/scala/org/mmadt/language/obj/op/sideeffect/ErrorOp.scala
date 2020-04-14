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

package org.mmadt.language.obj.op.sideeffect

import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.{Inst, IntQ, Obj}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ErrorOp {
  this: Obj =>
  def error(message: String): this.type = this match {
    case _: Type[_] => this.via(this, ErrorOp(message))
    case _ => throw LanguageException.typeError(this, message)
  }
}

object ErrorOp {
  def apply(message: String): Inst[Obj, Obj] = new ErrorInst(message)

  class ErrorInst(message: String, q: IntQ = qOne) extends VInst[Obj, Obj]((Tokens.error, List(str(message))), q) {
    override def q(quantifier: IntQ): this.type = new ErrorInst(message, quantifier).asInstanceOf[this.type]
    override def exec(start: Obj): Obj = throw LanguageException.typeError(this, message)
    //trav.split(trav.obj().error(message)) TODO make a distinction between compile-time and runtime errors (right now they are all compile time errors)
  }

}