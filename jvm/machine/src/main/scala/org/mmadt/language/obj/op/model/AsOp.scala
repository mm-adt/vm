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

import java.lang.{Double => JDouble}

import org.mmadt.language.Tokens
import org.mmadt.language.model.Model
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.ORec
import org.mmadt.storage.obj.value.VInst

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait AsOp {
  this: Obj =>
  def as[O <: Obj](obj: O): O = this match {
    case _: Type[_] => obj.via(this, AsOp(obj))
    case _ => AsOp(obj).exec(obj)
  }
}

object AsOp {
  def apply[O <: Obj](obj: O): AsInst[O] = new AsInst[O](obj)

  class AsInst[O <: Obj](obj: O, q: IntQ = qOne) extends VInst[Obj, O](g = (Tokens.as, List(obj)), q = q) {
    override def q(quantifier: IntQ): this.type = new AsInst[O](obj, quantifier).asInstanceOf[this.type]
    override def exec(start: Obj): O = {
      testAlive(obj match {
        case atype: Type[Obj] if start.isInstanceOf[Value[_]] => atype match {
          case rectype: Rec[Obj, Obj] =>
            start match {
              case arec: Rec[Obj,Obj] => new ORec(name = rectype.name, g = (Tokens.`;`,makeMap(Model.id, arec.gmap, rectype.gmap)))
              case avalue: Value[Obj] => rec(Tokens.`,`,rectype.gmap.map(x =>
                (x._1 match {
                  case kvalue: Value[Obj] => kvalue
                  case ktype: Type[Obj] =>
                    TypeChecker.matchesVT(avalue, ktype)
                    start.compute(ktype).asInstanceOf[Value[Obj]]
                }) -> (x._2 match {
                  case vvalue: Value[Obj] => vvalue
                  case vtype: Type[Obj] =>
                    TypeChecker.matchesVT(avalue, vtype)
                    start.compute(vtype).asInstanceOf[Value[Obj]]
                })).toMap).named(rectype.name)
            }
          case atype: StrType => vstr(name = atype.name, g = start.asInstanceOf[Value[Obj]].g.toString).compute(atype)
          case atype: IntType => vint(name = atype.name, g = Integer.valueOf(start.asInstanceOf[Value[Obj]].g.toString).longValue()).compute(atype)
          case atype: RealType => vreal(name = atype.name, g = JDouble.valueOf(start.asInstanceOf[Value[Obj]].g.toString).doubleValue()).compute(atype)
          case xtype: Type[Obj] => start.named(xtype.name).asInstanceOf[O]
        }
        case avalue: Value[Obj] => avalue
        case btype: Type[Obj] if start.isInstanceOf[Type[_]] => btype.via(start.asInstanceOf[Type[Obj]], AsOp(btype))
      }).via(start, this).asInstanceOf[O]
    }

    private def testAlive[X <: Obj](trav: X): X = {
      assert(trav.alive)
      trav
    }

    private def makeMap(model: Model, leftMap: collection.Map[Obj, Obj], rightMap: collection.Map[Obj, Obj]): collection.Map[Obj, Obj] = {
      if (leftMap.equals(rightMap)) return leftMap
      val typeMap: mutable.Map[Obj, Obj] = mutable.Map() ++ rightMap
      var valueMap: mutable.Map[Obj, Obj] = mutable.Map()
      leftMap.map(a => typeMap.find(k =>
        model(a._1).test(k._1) && model(a._2).test(k._2)).map(z => {
        valueMap = valueMap + (a._1 -> a._2.as(z._2))
        typeMap.remove(z._1)
      }))
      assert(typeMap.isEmpty || !typeMap.values.exists(x => x.alive))
      valueMap.toMap
    }
  }


}
