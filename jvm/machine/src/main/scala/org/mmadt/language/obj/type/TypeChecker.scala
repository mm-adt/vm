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

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object TypeChecker {

  def checkType[O <: Obj](obj:O,atype:OType):O ={
    if (obj.isInstanceOf[Rec[O,O]] || atype.isInstanceOf[ORecType] || (obj.isInstanceOf[OType] || ((obj match {
      case avalue:OValue => avalue.start().getClass.isAssignableFrom(atype.getClass) || atype.name.equals(Tokens.obj)
      case atype:OType => atype.getClass.isAssignableFrom(atype.getClass) || atype.name.equals(Tokens.obj)
    }) && obj.q()._1.value() >= atype.q()._1.value() && obj.q()._2.value() <= atype.q()._2.value())))
      obj
    else
      throw new IllegalArgumentException("The obj " + obj + " does not match the type " + atype)
  }

  def matchesVT[O <: Obj](obj:O with OValue,pattern:Obj with OType):Boolean = (obj ==> pattern).alive()
  def matchesVV[O <: Obj](obj:O with OValue,pattern:Obj with OValue):Boolean = obj.value().equals(pattern.value())
  def matchesTT[O <: Obj](obj:O with OType,pattern:Obj with OType):Boolean ={
    obj.insts().toString().equals(pattern.insts().toString()) ||
    (obj.domain().equals(pattern.domain()) && (obj.domain[OType]() ===> pattern).filter(x => x.equals(obj)).hasNext)
  }
  def matchesTV[O <: Obj](obj:O with OType,pattern:O with OValue):Boolean = false
}
