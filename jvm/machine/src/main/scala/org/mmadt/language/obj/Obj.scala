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

package org.mmadt.language.obj

import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.branch.ChooseOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.model.AsOp
import org.mmadt.language.obj.op.reduce.{CountOp, FoldOp}
import org.mmadt.language.obj.op.sideeffect.ErrorOp
import org.mmadt.language.obj.op.traverser.FromOp
import org.mmadt.language.obj.value.IntValue
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.processor.{Processor, Traverser}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Obj
  extends AOp
    with AsOp
    with CountOp[Int]
    with ChooseOp
    with IdOp
    with FoldOp
    with MapOp
    with FromOp
    with QOp
    with ErrorOp
    with EvalOp {

  // quantifier methods
  val q:IntQ
  def q(quantifier:IntQ):this.type
  def q(single:IntValue):this.type = this.q(single.q(qOne),single.q(qOne))
  def alive():Boolean = this.q != qZero

  // utility methods
  def toStrm:Strm[this.type] = strm[this.type](Iterator[this.type](this))
  def toList:List[this.type] = toStrm.value.toList
  def toSet:Set[this.type] = toStrm.value.toSet
  def next():this.type = toStrm.value.next()
  def ==>[E <: Obj](rangeType:Type[E]):E = Processor.iterator().apply(this,Type.resolveAnonymous(this,rangeType)).toStrm.value.next()
  def ===>[E <: Obj](rangeType:E):E = Processor.iterator().apply(this,Type.resolveAnonymous(this,rangeType.asInstanceOf[Type[E]])) // TODO: spec'd to R cause of FoldOp

  // pattern matching methods
  val name:String
  def test(other:Obj):Boolean

}
