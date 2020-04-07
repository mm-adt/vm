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
class TRec[A <: Obj,B <: Obj](name:String = Tokens.rec,java:Map[A,B] = Map[A,B](),quantifier:IntQ = qOne,via:DomainInst[Rec[A,B]] = base()) extends AbstractTObj[Rec[A,B]](name,quantifier,via) with RecType[A,B] {
  override def value():Map[A,B] = java
  override def clone(name:String,quantifier:IntQ,via:DomainInst[Obj]):this.type = new TRec[A,B](name,this.java,quantifier,via.asInstanceOf[DomainInst[Rec[A,B]]]).asInstanceOf[this.type]
}