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

package org.mmadt.language.obj.`type`

import org.mmadt.language.obj.{O, ORecType}
import org.mmadt.storage.obj.`type`.{TBool, TInt, TRec, TStr}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait TypeFactory {
  def tbool():BoolType
  def tint():IntType
  def tstr():StrType
  def trec():ORecType
}

object TypeFactory {
  def tbool()(implicit f:TypeFactory):BoolType = f.tbool()
  def tint()(implicit f:TypeFactory):IntType = f.tint()
  def tstr()(implicit f:TypeFactory):StrType = f.tstr()
  def trec()(implicit f:TypeFactory):ORecType = f.trec()

  implicit val tTypeFactory:TypeFactory = new TypeFactory {
    override def tbool():BoolType = new TBool()
    override def tint():IntType = new TInt()
    override def tstr():StrType = new TStr()
    override def trec():ORecType = new TRec[O,O]()
  }
}
