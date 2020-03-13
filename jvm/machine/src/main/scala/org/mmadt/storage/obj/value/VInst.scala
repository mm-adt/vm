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

package org.mmadt.storage.obj.value

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.IntValue
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VInst[S <: Obj,E <: Obj](java:InstTuple,quantifier:IntQ = qOne) extends AbstractVObj(Tokens.inst,java,quantifier) with Inst[S,E] {
  override def as[O <: Obj](obj:O):O = this.asInstanceOf[O]
  def this(java:InstTuple) = this(java,qOne)
  override def value():InstTuple = java
  override def q(quantifier:IntQ):this.type = new VInst(java,quantifier).asInstanceOf[this.type]
  override def id():this.type = this
  override def apply(trav:Traverser[S]):Traverser[E] = trav.asInstanceOf[Traverser[E]]
  override def count():IntValue = this.q._2
  override val q:IntQ = quantifier
  override def quant():IntValue = this.q._2
  override def =:[O <: Obj](op:String)(args:Obj*):O = args.head.asInstanceOf[O]
  override def error(message:String):this.type = throw new RuntimeException("error: " + message)

  // pattern matching methods TODO: GUT WHEN VINST JOINS HEIRARCHY
  def test(other:Obj):Boolean = false

  def composeInstruction(obj:E):E ={
    obj match {
      case atype:Type[Obj] => atype.compose(this).asInstanceOf[E]
      case _ => obj
    }
  }
}
