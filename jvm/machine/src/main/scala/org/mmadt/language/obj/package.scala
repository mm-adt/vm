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

package org.mmadt.language

import org.mmadt.language.obj.`type`.{RecType,Type}
import org.mmadt.language.obj.value.strm.RecStrm
import org.mmadt.language.obj.value.{IntValue,RecValue,Value}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object obj {
  type IntQ = (IntValue,IntValue)
  type InstTuple = (String,List[Obj])
  type State = Map[String,Obj]
  type InstList = List[(Type[Obj],Inst)]

  // less typing
  type OType[+O <: Obj] = O with Type[O]
  type OValue[+O <: Obj] = O with Value[O]
  type ORecType = RecType[Obj,Obj]
  type ORecValue = RecValue[Value[Obj],Value[Obj]]
  type ORecStrm = RecStrm[Value[Obj],Value[Obj]]

  // quantifier utilities
  private lazy val zero:IntValue = int(0)
  def minZero(quantifier:IntQ):IntQ = (zero,quantifier._2)
  def multQ(obj:Obj,atype:Type[_]):IntQ = atype.q() match {
    case _ if equals(qOne) => obj.q()
    case typeQuantifier:IntQ => (obj.q()._1 * typeQuantifier._1,obj.q()._2 * typeQuantifier._2)
  }
}


