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

package org.mmadt.language.model.rewrite

import org.mmadt.language.model.Model
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.Type

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object SinglePassRewrite {
  def rewrite[D <: Obj,R <: Obj](model:Model,startType:Type[D],endType:Type[R]):Type[R] ={
    var btype:Type[R] = endType
    var xtype:Type[R] = btype
    for (_ <- btype.insts().indices) {
      while (btype.insts() != Nil) {
        model.get(btype) match {
          case Some(rewrite) =>
            xtype = xtype.compose(rewrite.asInstanceOf[Type[R]])
            btype = rewrite.asInstanceOf[Type[R]]
          case None =>
            xtype = xtype.compose(btype.insts().head._2)
            btype = btype.linvert()
        }
      }
      btype = xtype
      xtype = btype.domain()
    }
    btype
  }
}
