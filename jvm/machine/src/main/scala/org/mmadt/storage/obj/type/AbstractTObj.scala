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

import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`._
import org.mmadt.storage.obj.OObj

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
abstract class AbstractTObj[T <: Obj](name:String,quantifier:IntQ, _via:ViaTuple) extends OObj(name,quantifier) with Type[Obj] {
  override val via:ViaTuple = _via
  override def q(_quantifier:IntQ):this.type = this.clone(name,null,multQ(via._1.asInstanceOf[this.type],_quantifier),if(root) base() else (via._1,via._2.q(_quantifier)).asInstanceOf[ViaTuple])
}
