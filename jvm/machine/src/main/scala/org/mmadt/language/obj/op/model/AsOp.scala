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

package org.mmadt.language.obj.op.model

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait AsOp {
  def as[O <: Obj](obj:O):O // TODO: spec to StrValue
}

object AsOp {
  def apply[O <: Obj](obj:O):Inst[Obj,O] = new AsInst[O](obj)

  class AsInst[O <: Obj](obj:O) extends VInst[Obj,O]((Tokens.as,List(obj))) {
    override def apply(trav:Traverser[Obj]):Traverser[O] ={
      trav.split((trav.obj(),obj) match {
        case (avalue:Value[Obj],atype:Type[Obj]) => atype match {
          case rectype:ORecType => vrec(rectype.name,rectype.value().map(x =>
            (x._1 match {
              case kvalue:Value[Obj] => kvalue
              case ktype:Type[Obj] => trav.apply(Type.resolveAnonymous(trav.obj(),ktype)).obj().asInstanceOf[Value[Obj]]
            }) -> (x._2 match {
              case vvalue:Value[Obj] => vvalue
              case vtype:Type[Obj] => trav.apply(Type.resolveAnonymous(trav.obj(),vtype)).obj().asInstanceOf[Value[Obj]]
            })),avalue.q)
          case _:Str => trav.split(vstr(value = avalue.value.toString)).apply(atype).obj()
          case _:Int => trav.split(vint(value = Integer.valueOf(avalue.value.toString).longValue())).apply(atype).obj()
          case _ => trav.apply(atype).obj()
        }
        case (obj:Obj,avalue:Value[Obj]) => avalue.q(multQ(avalue,obj))
        case (atype:Type[Obj],btype:Type[Obj]) => atype.compose(btype,AsOp(btype))
      }).asInstanceOf[Traverser[O]]
    }
  }

}
