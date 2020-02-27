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

import org.mmadt.language.obj.op.branch.ChooseOp
import org.mmadt.language.obj.op.map.{IdOp, MapOp, QOp}
import org.mmadt.language.obj.op.model.AsOp
import org.mmadt.language.obj.op.reduce.{CountOp, FoldOp}
import org.mmadt.language.obj.op.traverser.FromOp
import org.mmadt.language.obj.value.IntValue
import org.mmadt.processor.Processor
import org.mmadt.processor.obj.`type`.util.InstUtil

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Obj
  extends AsOp
    with CountOp[Int]
    with ChooseOp
    with IdOp
    with FoldOp
    with MapOp
    with FromOp
    with QOp {

  // quantifier methods
  def q():TQ
  def q(quantifier:TQ):this.type
  def q(single:IntValue):this.type = this.q((single,single))
  def q(min:IntValue,max:IntValue):this.type = this.q((min,max))
  def alive():Boolean = this.q()._1.value() != 0 && this.q()._2.value() != 0

  // utility methods
  def ==>[R <: Obj](rangeType:TType[R]):R = Processor.iterator[this.type,R]().apply(this,InstUtil.resolveAnonymous(this,rangeType)).map(_.obj()).next()
  def ===>[R <: Obj](rangeType:TType[R]):Iterator[R] = Processor.iterator[this.type,R]().apply(this,InstUtil.resolveAnonymous(this,rangeType)).map(_.obj())
  // pattern matching methods
  val name:String
  def test(other:Obj):Boolean
}
