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
import org.mmadt.language.obj.op.trace.{ExplainOp, ModelOp}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{eqQ, _}
import org.mmadt.language.{LanguageFactory, Tokens}
import org.mmadt.storage.StorageFactory.{qOne, qZero, zeroObj}

import scala.collection.mutable.ListBuffer

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Type[+T <: Obj] extends Obj with ExplainOp {
  // type signature properties and functions
  //def value: Any = throw LanguageException.typesNoValue(this)
  override lazy val range: this.type = this.isolate

  // pattern matching methods
  override def test(other: Obj): Boolean = other match {
    case _: Obj if !other.alive => !this.alive
    case _: __ if __.isAnon(other) => true
    case _: __ if __.isTokenRoot(other) =>
      val temp = Inst.resolveToken(this, other)
      if (temp == other) true else this.test(temp)
    case _: Type[_] => (sameBase(this, other.domain) || __.isAnon(this) || __.isAnonObj(other.domain)) && withinQ(this, other)
    case _ => false
  }

  // standard Java implementations
  override def toString: String = LanguageFactory.printType(this)

  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.q.hashCode() ^ this.trace.hashCode()

  override def equals(other: Any): Boolean = other match {
    case obj: Obj if !this.alive => !obj.alive
    case atype: Type[_] => atype.name.equals(this.name) && eqQ(atype, this) && // sameBase(this, other.domain)
      this.trace.filter(x => !ModelOp.isMetaModel(x._2)) == atype.trace.filter(x => !ModelOp.isMetaModel(x._2))
    case _ => false
  }
}
object Type {

  def mergeObjs(objs: List[Obj]): List[Obj] = {
    var newList: ListBuffer[Obj] = ListBuffer.empty[Obj]
    objs.foreach(x =>
      newList += newList.find(y => unity(x).equals(unity(y))).map(y => {
        newList = newList -= y
        merge(x, y)
      }).getOrElse(x))
    newList.toList
  }
  def merge[A <: Obj](objA: A, objB: A): A = {
    if (qZero == plusQ(objA.q, objB.q))
      zeroObj.asInstanceOf[A]
    else
      unity(objA).q(plusQ(pureQ(objA), pureQ(objB)))
  }

  def pureQ(obj: Obj): IntQ = {
    obj.trace.foldLeft(qOne)((a, b) => multQ(a, b._2.q))
  }

  def unity[A <: Obj](obj: A): A = {
    if (obj.trace.isEmpty) obj.domainObj.hardQ(qOne).id().asInstanceOf[A]
    else obj.trace.foldLeft(obj.domainObj.hardQ(qOne).asInstanceOf[A])((a, b) => b._2.hardQ(qOne).asInstanceOf[Inst[A, A]].exec(a))
  }

  def isIdentity(obj: Obj): Boolean = {
    if (obj.isInstanceOf[Value[_]]) return true
    if (obj.root) return true
    !obj.trace.filter(x => !ModelOp.isMetaModel(x._2)).exists(x => !(x._2.op == Tokens.id) && !(x._2.op == Tokens.id))
  }

  def tryCtype[A <: Obj](obj: A): A = if (obj.isInstanceOf[Type[_]] && isIdentity(obj) && obj.domain.q == qOne && pureQ(obj)==qOne) obj.isolate else obj

}
