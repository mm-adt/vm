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
import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.op.OpInstResolver
import org.mmadt.language.obj.op.model.AsOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{OType, Obj}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Model {

  def apply[B <: Obj](name:String):OType[B] = this.toRec.value().values.find(x => x.name == name).get.asInstanceOf[OType[B]]
  def apply[B <: Obj](obj:B):B = (obj match {
    case astrm:Strm[Obj] => strm(astrm.value.map(x => this.apply(x))) // TODO: migrate to AsOp?
    case avalue:Value[Obj] => this.fromValue(avalue).getOrElse(avalue)
    case atype:Type[Obj] => this.fromType(atype).getOrElse(atype)
  }).asInstanceOf[B]

  def put(model:Model):Model
  def put(left:Type[Obj],right:Type[Obj]):Model
  def fromType(left:Type[Obj]):Option[Type[Obj]]
  def fromValue(left:Value[Obj]):Option[Value[Obj]]
  def fromSymbol(left:String):Option[Type[Obj]]
  def toRec:RecType[Type[Obj],Type[Obj]]
}

object Model {
  def from(args:(Type[Obj],Type[Obj])*):Model = args.foldRight(this.simple())((a,b) => b.put(a._1,a._2))
  def from(arg:RecType[Type[Obj],Type[Obj]]):Model = arg.value().iterator.foldRight(this.simple())((a,b) => b.put(a._1,a._2))

  val id:Model = new Model {
    override def put(left:Type[Obj],right:Type[Obj]):Model = this
    override def put(model:Model):Model = this
    override def fromType(left:Type[Obj]):Option[Type[Obj]] = None
    override def fromSymbol(left:String):Option[Type[Obj]] = None
    override def toRec:RecType[Type[Obj],Type[Obj]] = rec
    override def fromValue(left:Value[Obj]):Option[Value[Obj]] = Some(left)
  }

  def simple():Model = new Model {
    val typeMap:mutable.Map[String,mutable.Map[Type[Obj],Type[Obj]]] = mutable.LinkedHashMap()
    override def toString:String = typeMap.map(a => a._1 + " ->\n\t" + a._2.map(b => b._1.toString + " -> " + b._2).fold(Tokens.empty)((x,y) => x + y + "\n\t")).map(x => x.trim).fold(Tokens.empty)((x,y) => x + y + "\n").trim

    override def put(model:Model):Model ={
      model.asInstanceOf[this.type].typeMap.foreach(x => x._2.foreach(y => this.put(y._1,y._2)))
      this
    }
    override def put(left:Type[Obj],right:Type[Obj]):Model ={
      if (typeMap.get(left.name).isEmpty) typeMap.put(left.name,mutable.LinkedHashMap())
      typeMap(left.name).put(left,right)
      this
    }
    override def fromType(left:Type[Obj]):Option[Type[Obj]] ={
      if (left.name.equals(Tokens.model)) return Some(toRec)
      this.typeMap.get(left.name) match {
        case None => None
        case Some(m) => m.get(left) match {
          case Some(n) => Some(n)
          case None => m.iterator.find(a => left.test(a._1)).map(a => {
            val state = bindLeftValuesToRightVariables(left,a._1).map(x => Traverser.standard(x._1)(x._2)).flatMap(x => x.state).toMap // TODO: may need to give model to traverser
            a._2.insts.map(x =>
              OpInstResolver.resolve[Obj,Obj](
                x._2.op(),
                x._2.args().map(i => Traverser.resolveArg[Obj,Obj](Traverser.standard(x._1,state),i)))) // TODO: may need to give model to traverser
              .foldRight(a._2.domain[Obj]())((x,z) => z.compose(x))
          })
        }
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
    override def toRec:RecType[Type[Obj],Type[Obj]] ={
      trec[Type[Obj],Type[Obj]](value = this.typeMap.values.foldRight(mutable.Map[Type[Obj],Type[Obj]]())((a,b) => b ++ a).toMap)
    }
    override def fromSymbol(left:String):Option[Type[Obj]] ={
      if (left.equals(Tokens.model)) return Some(toRec)
      typeMap.get(left) match {
        case None => typeMap.values.flatten.find(x => x._2.insts.isEmpty && left.equals(x._2.name)).map(_._2)
        case Some(m) => m.iterator.find(a => a._2.insts.isEmpty && left.equals(a._2.name)).map(_._2)
      }
    }
    override def fromValue(left:Value[Obj]):Option[Value[Obj]] ={
      typeMap.get(left.name) match {
        case None => typeMap.values.flatten.find(x => x._2.insts.isEmpty && left.test(x._2.name)).map(a => AsOp[Obj](a._2).apply(Traverser.standard(left,model = this)).obj().asInstanceOf[Value[Obj]])
        case Some(m) => m.iterator.find(a => a._2.insts.isEmpty && left.test(a._1)).map(a => AsOp[Obj](a._2).apply(Traverser.standard(left,model = this)).obj().asInstanceOf[Value[Obj]])
      }
    }
  }
}
