/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.obj.op.trace

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.language.obj.{Inst, Lst, Obj, Rec}
import org.mmadt.storage.StorageFactory.{lst, rec, str}
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait DefineOp {
  this: Obj =>
  def define(objs: Obj*): this.type = DefineOp(objs: _*).exec(this)
}
object DefineOp extends Func[Obj, Obj] {
  def apply[O <: Obj](objs: Obj*): Inst[O, O] = new VInst[O, O](g = (Tokens.define, objs.toList.asInstanceOf[List[O]]), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    start match {
      case astrm: Strm[_] => astrm(x => inst.exec(x))
      case _: Value[_] => inst.args.foldLeft(start)((a, b) => a.model(a.model.defining(b))).via(start, inst)
      case _: Type[_] => inst.args.foldLeft(start)((a, b) => a.model(a.model.defining(b))).via(start, inst)
    }
  }

  @inline implicit def modelToRichModel(ground: Model): RichModel = new RichModel(ground)
  class RichModel(val model: Model) {
    final def defining(definition: Obj): Model = {
      val map: collection.Map[StrValue, ModelOp.ModelMap] = model.gmap.asInstanceOf[collection.Map[StrValue, ModelOp.ModelMap]]
      val typesMap: collection.Map[Type[Obj], Lst[Obj]] = map.getOrElse[Rec[Type[Obj], Lst[Obj]]](str("type"), rec[Type[Obj], Lst[Obj]]).gmap
      val typeList: Lst[Obj] = typesMap.getOrElse[Lst[Obj]](definition.domain, lst)
      rec(g = (Tokens.`,`, map + (str("type") -> rec(g = (Tokens.`,`, typesMap + (definition.domain -> lst(g = (Tokens.`,`, typeList.glist :+ definition))))))))
    }
    final def merging(other:Model):Model = {
      val x = other.gmap.getOrElse[Rec[Type[Obj], Lst[Obj]]](str("type"), rec[Type[Obj], Lst[Obj]]).gmap.flatMap(x=>x._2.glist).foldLeft(model)((a,b)=>a.defining(b))
      rec(g=(Tokens.`,`,x.gmap + (str("path")->other.gmap.getOrElse(str("path"),rec[Type[Obj], Lst[Obj]]))))
    }


  }
}