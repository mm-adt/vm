/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.obj.`type`

import org.mmadt.language.obj.Obj.IntQ
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{eqQ, _}
import org.mmadt.language.{LanguageFactory, Tokens}
import org.mmadt.storage.StorageFactory.{qOne, qZero, zeroObj}

import scala.collection.mutable.ListBuffer

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Type[+T <: Obj] extends Obj {
  // type signature properties and functions
  override lazy val range:this.type = this.rangeObj
  // pattern matching methods
  override def test(other:Obj):Boolean = other match {
    case _:Obj if !other.alive => !this.alive
    case _ if __.isAnon(this) || __.isAnon(other) => true
    case _:__ if __.isToken(other) => Obj.resolveTokenOption(this, other).forall(x => this.test(x))
    case _:Type[_] => (sameBase(this, other.domain) || baseMapping(this, other.domain)) && this.q.within(other.q) // withinQ domain?
    case _ => false
  }
  private def baseMapping(source:Obj, target:Obj):Boolean = source.model.dtypes.exists(t =>
    !t.domain.named && !t.range.named &&
      source.compute(t.domainObj, withAs = false).alive && target.compute(t.rangeObj, withAs = false).alive)

  // standard Java implementations
  override def toString:String = LanguageFactory.printType(this)

  override lazy val hashCode:scala.Int = this.name.hashCode ^ this.q.hashCode() ^ this.trace.hashCode()

  override def equals(other:Any):Boolean = other match {
    case obj:Obj if !this.alive => !obj.alive
    case atype:Type[_] => atype.name.equals(this.name) && eqQ(atype, this) && this.trace.modeless == atype.trace.modeless
    case _ => false
  }

  def rule(rewrite:Inst[Obj, Obj]):this.type = this.via(this, rewrite)
}
object Type {
  // this is a total hack -- I'm encoding the range of the type in the via of the last instruction
  // the fix is to make it so <= doesn't rinvert and instead extends via a [noop] of some sort
  def trueRange(atype:Obj):Obj = if (__.isToken(atype) && !atype.root) if (null == atype.via._2.via._1) atype.rangeObj else asType(atype.via._2.via._1) else atype

  def isIdentity(obj:Obj):Boolean = obj.isInstanceOf[Value[_]] || obj.root || !obj.trace.modeless.exists(x => !(x._2.op == Tokens.id) && !(x._2.op == Tokens.noop))
  def mergeObjs[A <: Obj](objs:List[A]):List[A] = {
    var newList:ListBuffer[A] = ListBuffer.empty[A]
    objs.foreach(x =>
      newList += newList.find(y => unity(x).equals(unity(y))).map(y => {
        newList = newList -= y
        merge(x, y)
      }).getOrElse(x))
    newList.toList
  }
  private def merge[A <: Obj](objA:A, objB:A):A = if (qZero == plusQ(objA.q, objB.q)) zeroObj.asInstanceOf[A] else unity(objA).q(plusQ(pureQ(objA), pureQ(objB)))
  private def pureQ(obj:Obj):IntQ = if (obj.root || obj.isInstanceOf[Value[_]]) obj.q else obj.trace.foldLeft(qOne)((a, b) => multQ(a, b._2.q))
  private def unity[A <: Obj](obj:A):A = {
    if (obj.isInstanceOf[Value[_]]) obj.hardQ(qOne)
    else if (obj.trace.isEmpty && obj.isInstanceOf[Type[_]]) obj.domainObj.hardQ(qOne).id.asInstanceOf[A]
    else obj.trace.foldLeft(obj.domainObj.hardQ(qOne).asInstanceOf[A])((a, b) => b._2.hardQ(qOne).asInstanceOf[Inst[A, A]].exec(a))
  }
}
