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

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.value.Value

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object TypeChecker {

  def checkType[O <: Obj](obj:O,atype:Type[Obj]):O ={
    if (obj.isInstanceOf[Rec[O,O]] || atype.isInstanceOf[ORecType] || (obj.isInstanceOf[Type[Obj]] || ((obj match {
      case avalue:Value[Obj] => avalue.start().getClass.isAssignableFrom(atype.getClass) || atype.name.equals(Tokens.obj)
      case atype:Type[Obj] => atype.getClass.isAssignableFrom(atype.getClass) || atype.name.equals(Tokens.obj)
    }) && obj.q()._1.value() >= atype.q()._1.value() && obj.q()._2.value() <= atype.q()._2.value())))
      obj
    else
      throw new IllegalArgumentException("The obj " + obj + " does not match the type " + atype)
  }

  def matchesVT[O <: Obj](obj:Value[O],pattern:Type[O]):Boolean = (obj ==> pattern).alive()
  def matchesVV[O <: Obj](obj:Value[O],pattern:Value[O]):Boolean = obj.value().equals(pattern.value())
  def matchesTT[O <: Obj](obj:Type[O],pattern:Type[O]):Boolean ={
    obj.insts().toString().equals(pattern.insts().toString()) ||
    (obj.domain[Obj]().equals(pattern.domain[Obj]()) && (obj.domain[Type[O]]() ===> pattern).filter(x => x.equals(obj)).hasNext)
  }
  def matchesTV[O <: Obj](obj:Type[O],pattern:Value[O]):Boolean = false
}
