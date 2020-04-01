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
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.RecType
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TRec[A <: Obj,B <: Obj](name:String,java:Map[A,B],quantifier:IntQ,via:DomainInst[Rec[A,B]]) extends AbstractTObj[Rec[A,B]](name,quantifier,via) with RecType[A,B] {

  def this() = this(Tokens.rec,Map[A,B](),qOne,base())
  def this(java:Map[A,B]) = this(Tokens.rec,java,qOne,base())
  override def q(quantifier:IntQ):this.type = new TRec[A,B](name,java,quantifier,via).asInstanceOf[this.type]
  override def value():Map[A,B] = java
}