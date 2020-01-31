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

package org.mmadt.machine.obj.impl


import org.mmadt.language.Tokens
import org.mmadt.machine.obj._
import org.mmadt.machine.obj.impl.VInst._id
import org.mmadt.machine.obj.theory.obj.{Bool, Obj}

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
class VInst(jvm: JInst, quantifier: TQ) extends VObj[JInst](jvm, quantifier) with Inst {

  def this(jvm: JInst) = this(jvm, qOne)

  override def one(): Inst = _id

}

object VInst {

  object _id extends VInst((Tokens.id, List()))

  def and(a: Obj): VInst = new VInst((Tokens.and, List(a)))

  def is(a: Bool): VInst = new VInst((Tokens.is, List(a)))

  def plus(a: Obj): VInst = new VInst((Tokens.plus, List(a)))

  def or(a: Obj): VInst = new VInst((Tokens.or, List(a)))

  def mult(a: Obj): VInst = new VInst((Tokens.mult, List(a)))

  def gt(a: Obj): VInst = new VInst((Tokens.gt, List(a)))

}
