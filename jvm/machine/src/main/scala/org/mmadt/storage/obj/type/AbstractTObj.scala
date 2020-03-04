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
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, IntQ, Obj, _}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.OObj

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
abstract class AbstractTObj(name:String,insts:List[(Type[Obj],Inst)],quantifier:IntQ) extends OObj(name,quantifier) with Type[Obj] {

  def this() = this(Tokens.obj,Nil,qOne)
  def insts():List[(Type[Obj],Inst)] = insts

  // utility method
  private def typeName(op:String,nextType:(String,List[Obj])):String =
    if (op.equals(Tokens.as))
      nextType._2.head.asInstanceOf[StrValue].value()
    else if (Tokens.named(name)) name else nextType._1

}