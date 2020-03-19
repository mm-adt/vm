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
import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.op.OpInstResolver
import org.mmadt.language.obj.op.model.{AsOp, NoOp}
import org.mmadt.language.obj.value.Value
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Model {
  def apply[A <: Obj,B <: Value[Obj]](ctype:Type[A])(avalue:B with Value[Obj]):B ={
    AsOp(this.get(ctype.name).get).apply(Traverser.standard(avalue,model = this)).obj().asInstanceOf[B]
  }
  def put(model:Model):Model
  def put(left:Type[Obj],right:Type[Obj]):Model
  def get(left:Type[Obj]):Option[Type[Obj]]
  def get(left:String):Option[Type[Obj]]
  def resolve[E <: Obj](obj:E):E ={
    if (obj.name.equals(Tokens.model)) return recType.asInstanceOf[E]
    obj match {
      case atype:Type[Obj] => this.get(atype.name) match {
        case Some(btype) => atype.compose(btype,NoOp()).asInstanceOf[E]
        case None => obj
      }
      case _ => obj
    }
  }

  def define[O <: Obj](name:String)(definition:O with Type[Obj]):O ={
    this.put(tobj(name),definition.named(name))
    definition.named(name).range
  }

  def recType:RecType[Type[Obj],Type[Obj]]
}

object Model {
  def from(args:(Type[Obj],Type[Obj])*):Model = args.foldRight(this.simple())((a,b) => b.put(a._1,a._2))
  def from(arg:RecType[Type[Obj],Type[Obj]]):Model = arg.value().iterator.foldRight(this.simple())((a,b) => b.put(a._1,a._2))

  val id:Model = new Model {
    override def put(left:Type[Obj],right:Type[Obj]):Model = this
    override def put(model:Model):Model = this
    override def get(left:Type[Obj]):Option[Type[Obj]] = None
    override def get(left:String):Option[Type[Obj]] = None
    override def resolve[E <: Obj](obj:E):E = obj
    override def recType:RecType[Type[Obj],Type[Obj]] = rec
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
      if (left.name.equals(Tokens.model)) return Option(recType)
      val x:mutable.Map[Type[Obj],Type[Obj]] = typeMap.get(left.name) match {
        case None => return None
        case Some(m) => m
      }
      x.get(left) match {
        case Some(m) => Some(m)
        case None => x.iterator.find(a => left.test(a._1)).map(a => {
          val state = bindLeftValuesToRightVariables(left,a._1).map(x => Traverser.standard(x._1)(x._2)).flatMap(x => x.state).toMap
          a._2.insts.map(x =>
            OpInstResolver.resolve[Obj,Obj](
              x._2.op(),
              x._2.args().map(i => Traverser.resolveArg[Obj,Obj](Traverser.standard(x._1,state),i))))
            .foldRight(a._2.domain())((x,z) => z.compose(x))
        })
      }
    }

    // generate traverser state
    private def bindLeftValuesToRightVariables(left:Type[Obj],right:Type[Obj]):List[(Obj,Type[Obj])] ={
      left.insts.map(_._2).zip(right.insts.map(_._2))
        .flatMap(x => x._1.args().zip(x._2.args()))
        .filter(x => x._2.isInstanceOf[Type[Obj]])
        .flatMap(x => {
          x._1 match {
            case left1:Type[Obj] => bindLeftValuesToRightVariables(left1,x._2.asInstanceOf[Type[Obj]])
            case _ => List(x)
          }
        })
        .map(x => (x._1,x._2.asInstanceOf[Type[Obj]]))
    }
    override def get(left:String):Option[Type[Obj]] ={
      if (left.equals(Tokens.model)) return Option(recType)
      typeMap.get(left) match {
        case None => None
        case Some(m) => m.iterator.find(a => tobj(left).test(a._1)).map(_._2)
      }
    }
    override def recType:RecType[Type[Obj],Type[Obj]] ={
      trec[Type[Obj],Type[Obj]](value = this.typeMap.values.foldRight(mutable.Map[Type[Obj],Type[Obj]]())((a,b) => b ++ a).toMap)
    }
  }
}
