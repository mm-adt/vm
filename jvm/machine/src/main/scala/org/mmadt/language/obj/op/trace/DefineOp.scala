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
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, Lst, Obj, Rec}
import org.mmadt.storage.StorageFactory.{lst, rec}
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
      case _ => inst.args.foldLeft(start)((a, b) => a.model(a.model.defining(b)))
    }
  }
  @inline implicit def modelToRichModel(ground: Model): RichModel = new RichModel(ground)
  class RichModel(val model: Model) {
    final def definitions: List[Obj] = {
      val map: collection.Map[StrValue, ModelOp.ModelMap] = Option(model.g._2).getOrElse(collection.Map.empty).asInstanceOf[collection.Map[StrValue, ModelOp.ModelMap]]
      val typesMap: collection.Map[Type[Obj], Lst[Obj]] = Option(map.getOrElse[Rec[Type[Obj], Lst[Obj]]](ModelOp.TYPE, rec[Type[Obj], Lst[Obj]]).g._2).getOrElse(collection.Map.empty)
      typesMap.flatMap(x => x._2.glist).toList
    }
    final def defining(definition: Obj): Model = {
      val map: collection.Map[StrValue, ModelOp.ModelMap] = Option(model.g._2).getOrElse(collection.Map.empty).asInstanceOf[collection.Map[StrValue, ModelOp.ModelMap]]
      val typesMap: collection.Map[Type[Obj], Lst[Obj]] = Option(map.getOrElse[Rec[Type[Obj], Lst[Obj]]](ModelOp.TYPE, rec[Type[Obj], Lst[Obj]]).g._2).getOrElse(collection.Map.empty)
      val typeList: List[Obj] = Option(typesMap.getOrElse[Lst[Obj]](definition.range, lst).g._2).getOrElse(Nil)
      if (typeList.contains(definition)) model
      else rec(g = (Tokens.`,`, map + (ModelOp.TYPE -> rec(g = (Tokens.`,`, typesMap + (definition.range -> lst(g = (Tokens.`,`, typeList :+ definition))))))))
    }
    final def merging(other: Model): Model = {
     if(other.isEmpty) return model
     else if(model.isEmpty) return other
      val x: Model = other.g._2.getOrElse[Rec[Type[Obj], Lst[Obj]]](ModelOp.TYPE, rec[Type[Obj], Lst[Obj]]).g._2.flatMap(x => x._2.g._2).foldLeft(model)((a, b) => a.defining(b))
      rec(g = (Tokens.`,`, x.g._2 + (ModelOp.PATH -> other.g._2.getOrElse(ModelOp.PATH, rec[Type[Obj], Lst[Obj]]))))
    }
  }
}