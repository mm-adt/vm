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

package org.mmadt.language.model

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{BoolType, IntType, RecType, Type}
import org.mmadt.storage.obj.`type`.{TBool, TInt, TRec}

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Model {
  def put(model:Model):Model
  def put(left:Type[Obj],right:Type[Obj]):Model
  def get(left:Type[Obj]):Option[Type[Obj]]
  def get(left:String):Option[Type[Obj]]

  def define[O <: Obj](name:String)(definition:O with Type[Obj]):O ={
    val namedType:O = (definition match {
      case _:BoolType => new TBool(name,definition.q(),Nil)
      case _:IntType => new TInt(name,definition.q(),Nil)
      case rec:RecType[Obj,Obj] => new TRec[Obj,Obj](name,rec.value(),definition.q(),Nil)
    }).asInstanceOf[O]
    this.put(namedType.asInstanceOf[Type[Obj]],definition)
    namedType
  }
}

object Model {
  def apply(args:(Type[Obj],Type[Obj])*):Model = args.foldRight(this.simple())((a,b) => b.put(a._1,a._2))
  def apply(arg:RecType[Type[Obj],Type[Obj]]):Model = arg.value().iterator.foldRight(this.simple())((a,b) => b.put(a._1,a._2))

  val id:Model = new Model {
    override def put(left:Type[Obj],right:Type[Obj]):Model = this
    override def put(model:Model):Model = this
    override def get(left:Type[Obj]):Option[Type[Obj]] = None
    override def get(left:String):Option[Type[Obj]] = None
  }

  def simple():Model = new Model {
    val typeMap:mutable.Map[String,mutable.Map[Type[Obj],Type[Obj]]] = mutable.Map()
    override def toString:String = typeMap.map(a => a._1 + " ->\n\t" + a._2.map(b => b._1.toString + " -> " + b._2).fold(Tokens.empty)((x,y) => x + y + "\n\t")).map(x => x.trim).fold(Tokens.empty)((x,y) => x + y + "\n").trim

    override def put(model:Model):Model ={
      model.asInstanceOf[this.type].typeMap.foreach(x => x._2.foreach(y => this.put(y._1,y._2)))
      this
    }
    override def put(left:Type[Obj],right:Type[Obj]):Model ={
      if (typeMap.get(left.name).isEmpty) typeMap.put(left.name,mutable.Map())
      typeMap(left.name).put(left,right)
      this
    }
    override def get(left:Type[Obj]):Option[Type[Obj]] ={
      val x = typeMap.get(left.name) match {
        case None => return None
        case Some(m) => m
      }
      x.get(left) match {
        case Some(m) => Some(m)
        case None => x.iterator.find(a => left.test(a._1)).map(_._2)
      }
    }
    override def get(left:String):Option[Type[Obj]] ={
      typeMap.get(left) match {
        case None => return None
        case Some(m) => m.keys.find(atype => atype.toString.equals(left))
      }
    }
  }
}
