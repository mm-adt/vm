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

package org.mmadt.language.obj.op.map

import org.mmadt.language.mmlang.mmlangParser
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.{ObjValue, RecValue, StrValue, Value}
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.StorageFactory.{qOne, vrec}
import org.mmadt.storage.obj.value.VInst

import scala.io.Source

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait EOp {
  this:Obj =>
  def |=[O <: Obj](op:StrValue,result:O,arg:StrValue):O
}

object EOp {
 /* def apply[O <: Obj](op:StrValue,result:O,arg:StrValue):Inst = new VInst((s"=${op.value()}",List(arg)),qOne,(a:Obj,b:List[Obj]) => a match {
    case atype:Type[Obj] => atype.|=(op,result,b.head.asInstanceOf[StrValue])
    case avalue:Value[Obj] => op.value() match {
      case "=mmkv" => vrec(Source.fromFile(arg.value()).getLines().flatMap[RecValue[StrValue,ObjValue]](k => mmlangParser.parse(k).asInstanceOf[Iterator[RecValue[StrValue,ObjValue]]]))
    }
  })*/
}