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

package org.mmadt.language.obj.value

import org.mmadt.language.LanguageFactory
import org.mmadt.language.mmlang.mmlangParser
import org.mmadt.language.obj.`type`.{Type, TypeChecker}
import org.mmadt.language.obj.{Int, OType, Obj, Rec, _}
import org.mmadt.storage.StorageFactory._

import scala.io.Source

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Value[+V <: Obj] extends Obj {
  def value():Any
  def start():OType[V]

  override def quant():Int = this.q()._1.q(qOne)
  override def count():IntValue = this.q()._1.q(qOne)
  override def id():this.type = this
  override def fold[O <: Obj](seed:(String,O))(atype:Type[O]):O = this ==> atype
  override def from[O <: Obj](label:StrValue):O = this.start().from(label)
  override def from[O <: Obj](label:StrValue,default:Obj):O = this.start().from(label,default)
  override def map[O <: Obj](other:O):O = other match {
    case _:Value[_] => other
    case atype:Type[O] => this ==> atype
  }

  // pattern matching methods
  override def test(other:Obj):Boolean = other match {
    case argValue:Value[_] => TypeChecker.matchesVV(this,argValue)
    case argType:Type[_] => TypeChecker.matchesVT(this,argType)
  }

  // standard Java implementations

  override def toString:String = LanguageFactory.printValue(this)
  override def hashCode():scala.Int = this.name.hashCode ^ this.value().hashCode()
  override def equals(other:Any):Boolean = other match {
    case avalue:Value[V] => avalue.value() == this.value() && eqQ(this,avalue)
    case _ => false
  }

  ////////
  override def mmkv(file:StrValue):Rec[StrValue,Obj] = vrec(Source.fromFile(file.value()).getLines().drop(1).flatMap(k => mmlangParser.parse(k).asInstanceOf[Iterator[RecValue[StrValue,ObjValue]]])).asInstanceOf[Rec[StrValue,Obj]]
}
