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
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait AsOp {
  this:Obj =>
  def as[O <: Obj](obj:O):O = this match {
    case atype:Type[_] => atype.compose(obj,AsOp(obj))
    case _ => AsOp(obj).apply(Traverser.standard(this)).obj()
  }
}

object AsOp {
  def apply[O <: Obj](obj:O):Inst[Obj,O] = new AsInst[O](obj)

  class AsInst[O <: Obj](obj:O,q:IntQ = qOne) extends VInst[Obj,O]((Tokens.as,List(obj)),q) {
    override def q(quantifier:IntQ):this.type = new AsInst[O](obj,quantifier).asInstanceOf[this.type]
    override def apply(trav:Traverser[Obj]):Traverser[O] ={
      trav.split(testAlive(obj match {
        case atype:Type[Obj] if trav.avalue => atype match {
          case rectype:RecType[Obj,Obj] =>
            trav.obj() match {
              case recvalue:ORecValue => vrec(name = rectype.name,value = makeMap(trav.model,recvalue.value,rectype.value()))
              case avalue:Value[Obj] => vrec(rectype.name,rectype.value().map(x =>
                (x._1 match {
                  case kvalue:Value[Obj] => kvalue
                  case ktype:Type[Obj] =>
                    TypeChecker.matchesVT(avalue,ktype)
                    trav.apply(Type.resolve(trav.obj(),ktype)).obj().asInstanceOf[Value[Obj]]
                }) -> (x._2 match {
                  case vvalue:Value[Obj] => vvalue
                  case vtype:Type[Obj] =>
                    TypeChecker.matchesVT(avalue,vtype)
                    trav.apply(Type.resolve(trav.obj(),vtype)).obj().asInstanceOf[Value[Obj]]
                })))
            }
          case atype:StrType => trav.split(vstr(name = atype.name,value = trav.obj().asInstanceOf[Value[Obj]].value.toString)).apply(atype).obj()
          case atype:IntType => trav.split(vint(name = atype.name,value = Integer.valueOf(trav.obj().asInstanceOf[Value[Obj]].value.toString).longValue())).apply(atype).obj()
          case atype:RealType => trav.split(vreal(name = atype.name,value = JDouble.valueOf(trav.obj().asInstanceOf[Value[Obj]].value.toString).doubleValue())).apply(atype).obj()
          case xtype:Type[Obj] => trav.obj().named(xtype.name).asInstanceOf[O]
        }
        case avalue:Value[Obj] => avalue
        case btype:Type[Obj] if trav.atype =>
          if (btype.isInstanceOf[RecType[Obj,Obj]]) {
            trav.obj().asInstanceOf[Type[Obj]].compose(btype,AsOp(Type.resolve(trav.obj(),btype)))
          } else {
            trav.obj().asInstanceOf[Type[Obj]].compose(btype,AsOp(btype))
          }
      }).q(trav.obj().q)).asInstanceOf[Traverser[O]]
    }

    private def testAlive[X <: Obj](trav:X):X ={
      assert(trav.alive())
      trav
    }

    private def makeMap(model:Model,leftMap:Map[Value[Obj],Value[Obj]],rightMap:Map[Obj,Obj]):Map[Value[Obj],Value[Obj]] ={
      if (leftMap.equals(rightMap)) return leftMap
      val typeMap :mutable.Map[Obj,Obj]               = mutable.Map() ++ rightMap
      var valueMap:mutable.Map[Value[Obj],Value[Obj]] = mutable.Map()
      leftMap.map(a => typeMap.find(k =>
        model(a._1).test(Type.resolve(a._1,k._1)) &&
        model(a._2).test(Type.resolve(a._2,k._2))).map(z => {
        valueMap = valueMap + (a._1 -> a._2.as(z._2).asInstanceOf[Value[Obj]])
        typeMap.remove(z._1)
      }))
      assert(typeMap.isEmpty || !typeMap.values.exists(x => x.q._1.value != 0))
      valueMap.toMap
    }
  }


}
