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

import org.mmadt.language.Printable
import org.mmadt.language.PrintableInstances._
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op._
import org.mmadt.language.obj.value.IntValue
import org.mmadt.processor.Processor

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Obj
  extends AsOp
    with ChooseOp
    with IdOp
    with MapOp
    with FromOp {

  // quantifier methods
  def q():TQ
  def q(quantifier:TQ):this.type
  def q(single:IntValue):this.type = this.q((single,single))
  def q(min:IntValue,max:IntValue):this.type = this.q((min,max))
  def alive():Boolean = this.q()._1.value() != 0 && this.q()._2.value() != 0

  // utility methods
  def ==>[R <: Obj](rangeType:TType[R]):R = Processor.iterator[this.type,R]().apply(this,rangeType match {
    case x:__ => x(this)
    case x:R => x
  }).map(_.obj()).next()
  def ===>[R <: Obj](rangeType:TType[R]):Iterator[R] = Processor.iterator[this.type,R]().apply(this,rangeType match {
    case x:__ => x(this)
    case x:R => x
  }).map(_.obj())

  // pattern matching methods
  val name:String
  def test(other:Obj):Boolean
  override def toString:String = Printable.format(this)
}
