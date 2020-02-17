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

package org.mmadt.language.model

import org.mmadt.language.obj.`type`.Type

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Model {

  def put(left: Type[_], right: Type[_]): Model //
  def get(left: Type[_]): Option[Type[_]] //

}

object Model {
  val id: Model = new Model {
    override def put(left: Type[_], right: Type[_]): Model = this //
    override def get(left: Type[_]): Option[Type[_]] = None //
  }

  def simple(): Model = new Model {
    val typeMap: mutable.Map[String, mutable.Map[Type[_], Type[_]]] = mutable.Map()

    override def toString: String = "model" + (Map.empty ++ typeMap).toString()

    override def put(left: Type[_], right: Type[_]): Model = {
      if (typeMap.get(left.name).isEmpty) typeMap.put(left.name, mutable.Map())
      typeMap(left.name).put(left, right)
      this
    }

    override def get(left: Type[_]): Option[Type[_]] = if (typeMap.get(left.name).isEmpty) None else typeMap(left.name).get(left)
  }
}
