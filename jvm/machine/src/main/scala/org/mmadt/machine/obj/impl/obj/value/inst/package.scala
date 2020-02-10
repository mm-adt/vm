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

package org.mmadt.machine.obj.impl.obj.value

import org.mmadt.language.Tokens
import org.mmadt.machine.obj.impl.obj.qOne
import org.mmadt.machine.obj.theory.obj.`type`.Type
import org.mmadt.machine.obj.theory.obj.value.inst._
import org.mmadt.machine.obj.theory.obj.value.{RecValue, StrValue, Value}
import org.mmadt.machine.obj.theory.obj.{Bool, Obj}
import org.mmadt.machine.obj.theory.operator.{MultOp, _}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object inst {

  class VAndInst(arg: Bool) extends VInst((Tokens.and, List(arg)), qOne) with AndInst[Bool]

  class VOrInst(arg: Bool) extends VInst((Tokens.or, List(arg)), qOne) with OrInst[Bool]

  class VChooseInst[O <: Obj with ChooseOp, IT <: Type[IT], OT <: Obj](arg: RecValue[Type[_], Type[_]]) extends VInst((Tokens.choose, List(arg)), qOne) with ChooseInst[O, IT, OT]

  class VFromInst(arg: StrValue) extends VInst((Tokens.from, List(arg)), qOne) with FromInst

  class VGetInst[A <: Obj, B <: Obj](arg: A) extends VInst((Tokens.get, List(arg)), qOne) with GetInst[A, B]

  class VGtInst[O <: Obj with GtOp[O, V, T], V <: Value[V] with O, T <: Type[T] with O](arg: Obj) extends VInst((Tokens.gt, List(arg)), qOne) with GtInst[O, V, T]

  class VIsInst[O <: Obj with IsOp[O, V, T], V <: Value[V] with O, T <: Type[T] with O](arg: Obj) extends VInst((Tokens.is, List(arg)), qOne) with IsInst[O, V, T]

  class VMapInst[O <: Obj with MapOp](arg: Obj) extends VInst((Tokens.map, List(arg)), qOne) with MapInst[O]

  class VModelInst(arg: Obj) extends VInst((Tokens.model, List(arg)), qOne) with ModelInst

  class VMultInst[O <: Obj with MultOp[O, V, T], V <: Value[V] with O, T <: Type[T] with O](arg: Obj) extends VInst((Tokens.mult, List(arg)), qOne) with MultInst[O, V, T]

  class VPlusInst[O <: Obj with PlusOp[O, V, T], V <: Value[V] with O, T <: Type[T] with O](arg: Obj) extends VInst((Tokens.plus, List(arg)), qOne) with PlusInst[O, V, T]

  class VToInst(arg: StrValue) extends VInst((Tokens.to, List(arg)), qOne) with ToInst

}
