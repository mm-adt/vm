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
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.IntValue
import org.mmadt.language.obj.{Obj, State}
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

  // standard Java implementations
  override def toString:String = LanguageFactory.printTraverser(this)
  override def hashCode():Int = obj().hashCode() ^ state.hashCode()
  override def equals(other:Any):Boolean = other match {
    case traverser:Traverser[S] => Objects.equals(traverser.obj(),this.obj()) &&
                                   Objects.equals(traverser.state,this.state)
    case _ => false
  }

}

object Traverser {
  def stateSplit[S <: Obj](label:String,obj:Obj)(traverser:Traverser[S]):Traverser[S] = traverser.split(traverser.obj(),traverser.state + (label -> obj))
  def qSplit[S <: Obj](traverser:Traverser[S]):Traverser[IntValue] = traverser.split(int(traverser.obj().q()._1.value()))
}
