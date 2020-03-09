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

import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.TraverserInstruction
import org.mmadt.language.obj.value.{IntValue, Value}
import org.mmadt.language.obj.{Obj, State, _}
import org.mmadt.language.{LanguageFactory, Tokens}
import org.mmadt.processor.obj.`type`.util.InstUtil
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
  def qSplit[S <: Obj](traverser:Traverser[S]):Traverser[IntValue] = traverser.split(int(traverser.obj().q()._1.value()))
  def typeCheck[S <: Obj](traverser:Traverser[S],checkType:Type[S]):Unit ={
    assert(traverser.obj() match {
      case atype:Type[S] => atype.range.test(checkType)
      case avalue:Value[S] => avalue.test(checkType)
    },traverser.obj() + " is not in " + checkType)
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
      Traverser.typeCheck(this,rangeType.domain())
      (InstUtil.nextInst(rangeType) match {
        case None =>
          assert(rangeType.domain() == rangeType.domain())
          return this.asInstanceOf[Traverser[E]]
        case Some(inst) =>
          val next:Traverser[E] = inst match {
            case traverserInst:TraverserInstruction => traverserInst.op() match {
              case Tokens.to => traverserInst.doTo(this)
              case Tokens.from => traverserInst.doFrom(this)
              case Tokens.fold => traverserInst.doFold(this)
            }
            case _ => this.split[E](InstUtil.instEval(this,inst))
          }
          next.split[E](next.obj().q(multQ(next.obj(),inst))) // TODO: avoid the double split by merging traverser instruction handling with obj instruction handling
      }).apply(rangeType.linvert())
    }
  }

}
