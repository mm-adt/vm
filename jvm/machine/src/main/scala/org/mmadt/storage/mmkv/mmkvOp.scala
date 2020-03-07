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

package org.mmadt.storage.mmkv

import org.mmadt.language.mmlang.mmlangParser
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.{ObjValue, RecValue, StrValue, Value}
import org.mmadt.language.obj.{Inst, Obj, Rec, Str}
import org.mmadt.storage.StorageFactory.{qOne, _}
import org.mmadt.storage.obj.value.VInst

import scala.io.Source

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait mmkvOp {

  def mmkv(file:StrValue):Rec[Str,Obj]

}


object mmkvOp {
  def apply(file:StrValue):Inst = new VInst(("=mmkv",List(file)),qOne,((a:Obj,b:List[Obj]) => (a match {
    case atype:Type[Obj] => atype.mmkv(b.head.asInstanceOf[StrValue])
    case avalue:Value[Obj] => vrec(Source.fromFile(file.value()).getLines().flatMap[RecValue[StrValue,ObjValue]](k => mmlangParser.parse(k).asInstanceOf[Iterator[RecValue[StrValue,ObjValue]]]))
  })))

}