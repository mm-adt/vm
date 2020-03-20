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
import org.mmadt.language.obj.`type`.{RecType,Type}
import org.mmadt.language.obj.op.OpInstResolver
import org.mmadt.language.obj.op.model.{AsOp,NoOp}
import org.mmadt.language.obj.value.Value
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Model {
  def apply[B <: Obj](obj:B):B = (obj match {
    case atype:Type[Obj] => this.symbol(atype.name).map(x => atype.asInstanceOf[Type[Obj]].compose(x,NoOp())).getOrElse(this.get(atype).getOrElse(atype))
    case avalue:Value[Obj] => this.symbol(avalue.name).map(x => AsOp[Obj](x).apply(Traverser.standard(avalue,model = this)).obj()).getOrElse(avalue)
  }).asInstanceOf[B]

  def put(model:Model):Model
  def put(left:Type[Obj],right:Type[Obj]):Model
  def get(left:Type[Obj]):Option[Type[Obj]]
  def symbol(left:String):Option[Type[Obj]]
  def define[O <: Obj](definition:O with Type[Obj]):O ={
    this.put(tobj(definition.domain().name),definition)
    definition.range
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
    override def symbol(left:String):Option[Type[Obj]] = None
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
      if (left.name.equals(Tokens.model)) return Some(recType)
      val x:mutable.Map[Type[Obj],Type[Obj]] = typeMap.get(left.name) match {
        case None => return None
        case Some(m) => m
      }
      x.get(left) match {
        case Some(m) => Some(m)
        case None => x.iterator.find(a => left.test(a._1)).map(a => {
          val state = bindLeftValuesToRightVariables(left,a._1).map(x => Traverser.standard(x._1)(x._2)).flatMap(x => x.state).toMap // TODO: may need to give model to traverser
          a._2.insts.map(x =>
            OpInstResolver.resolve[Obj,Obj](
              x._2.op(),
              x._2.args().map(i => Traverser.resolveArg[Obj,Obj](Traverser.standard(x._1,state),i)))) // TODO: may need to give model to traverser
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
    override def symbol(left:String):Option[Type[Obj]] ={
      if (left.equals(Tokens.model)) return Some(recType)
      typeMap.get(left) match {
        case None => None
        case Some(m) => m.iterator.find(a => isSymbol(a._1) && left.equals(a._1.name)).map(_._2)
      }
    }
    override def recType:RecType[Type[Obj],Type[Obj]] ={
      trec[Type[Obj],Type[Obj]](value = this.typeMap.values.foldRight(mutable.Map[Type[Obj],Type[Obj]]())((a,b) => b ++ a).toMap)
    }
  }
}
