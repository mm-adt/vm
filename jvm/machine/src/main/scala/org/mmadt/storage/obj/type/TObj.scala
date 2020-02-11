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

package org.mmadt.storage.obj.`type`

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.{Inst, Obj, TQ}
import org.mmadt.storage.obj.{OObj, _}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
abstract class TObj[T <: Type[T]](name: String, insts: List[(Type[_], Inst)], quantifier: TQ) extends OObj(name,quantifier) with Type[T] {

  def this() = this(Tokens.obj, Nil, qOne) //
  def insts(): List[(Type[_], Inst)] = insts //
  override def as(name: String): this.type = this

  override def int(inst: Inst, q: TQ): IntType = new TInt(Tokens.int, this.insts() ::: List((this, inst)), q)

  override def bool(inst: Inst, q: TQ): BoolType = new TBool(Tokens.bool, this.insts() ::: List((this, inst)), q)

  override def str(inst: Inst, q: TQ): StrType = new TStr(Tokens.str, this.insts() ::: List((this, inst)), q)

  override def rec[K <: Obj, V <: Obj](tvalue: Map[K, V], inst: Inst, q: TQ): RecType[K, V] = new TRec(Tokens.rec, tvalue, this.insts() ::: List((this, inst)), q)

}