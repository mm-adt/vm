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

package org.mmadt.processor

import java.util.Objects

import org.mmadt.language.LanguageFactory
import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.value.{IntValue, Value}
import org.mmadt.language.obj.{Obj, State, _}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Traverser[+S <: Obj] {


  def obj():S // the obj location of the traverser
  val state:State // the local variables of the traverser
  val model:Model // the model containing model-ADTs

  def split[E <: Obj](obj:E,state:State = this.state):Traverser[E] // clone the traverser with a new obj location
  def apply[E <: Obj](rangeType:Type[E]):Traverser[E] // embed the traverser's obj into the provided type

  // helper utilities
  lazy val avalue:Boolean = this.obj().isInstanceOf[Value[Obj]]
  lazy val atype :Boolean = this.obj().isInstanceOf[Type[Obj]]

  // standard Java implementations
  override def toString:String = LanguageFactory.printTraverser(this)
  override def hashCode():scala.Int = this.obj().hashCode() ^ state.hashCode()
  override def equals(other:Any):Boolean = other match {
    case traverser:Traverser[S] => Objects.equals(traverser.obj(),this.obj()) &&
                                   Objects.equals(traverser.state,this.state)
    case _ => false
  }

}

object Traverser {
  // traverser utility methods
  def stateSplit[S <: Obj](label:String,obj:Obj)(traverser:Traverser[S]):Traverser[S] = traverser.split(traverser.obj(),traverser.state + (label -> obj))
  def qSplit[S <: Obj](traverser:Traverser[S]):Traverser[IntValue] = traverser.split(int(traverser.obj().q._1.value))
  def resolveArg[S <: Obj,E <: Obj](traverser:Traverser[S],arg:E):E ={
    (arg match {
      case anon:__ => anon(traverser.obj().asInstanceOf[Type[_]].range)
      case valueArg:Value[_] => valueArg
      case typeArg:Type[_] => traverser.split(traverser.obj() match {
        case atype:Type[_] => atype.range
        case avalue:Value[_] => avalue
      }).apply(typeArg).obj()
    }).asInstanceOf[E]
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // traverser construction methods
  def standard[S <: Obj](obj:S,state:State = Map.empty,model:Model = Model.id):Traverser[S] = new StandardTraverser[S](obj,state,model)

  class StandardTraverser[S <: Obj](val obj:S,val state:State = Map.empty,val model:Model = Model.id) extends Traverser[S] {
    def this(obj:S) = this(obj,Map.empty)
    override def split[E <: Obj](obj:E,state:State = this.state):Traverser[E] =
      new StandardTraverser[E](model.resolve(obj),state,this.model)
    override def apply[E <: Obj](rangeType:Type[E]):Traverser[E] ={
      (Type.nextInst(rangeType) match {
        case None => return this.asInstanceOf[Traverser[E]]
        case Some(inst:Inst[Obj,E]) => inst.apply(this)
      }).apply(rangeType.linvert())
    }
  }

}
