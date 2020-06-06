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

package org.mmadt.language.obj.`type`

import org.mmadt.language.obj.op.model.ModelOp
import org.mmadt.language.obj.op.trace.ExplainOp
import org.mmadt.language.obj.{eqQ, _}
import org.mmadt.language.{LanguageFactory, Tokens}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Type[+T <: Obj] extends Obj
  with ModelOp
  with ExplainOp {
  this: T =>

  // type signature properties and functions
  //def value: Any = throw LanguageException.typesNoValue(this)
  override def range: this.type = this.isolate

  // pattern matching methods
  override def test(other: Obj): Boolean = other match {
    case aobj: Obj if !aobj.alive => !this.alive
    case atype: Type[_] =>
      (name.equals(atype.name) || atype.name.equals(Tokens.obj) || this.name.equals(Tokens.anon) || atype.name.equals(Tokens.anon)) &&
        withinQ(this, atype) &&
        this.trace.length == atype.trace.length &&
        this.trace.map(_._2).zip(atype.trace.map(_._2)).
          forall(insts => insts._1.op.equals(insts._2.op) && insts._1.args.zip(insts._2.args).forall(a => Obj.copyDefinitions(this, a._1).test(Inst.resolveToken(this, a._2))))
    case _ => false
  }
  // standard Java implementations
  override def toString: String = LanguageFactory.printType(this)
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.q.hashCode() ^ this.trace.hashCode()
  override def equals(other: Any): Boolean = other match {
    case obj: Obj if !this.alive => !obj.alive
    case atype: Type[_] => atype.name.equals(this.name) && eqQ(atype, this) && ((this.root && atype.root) || (this.via == atype.via))
    case _ => false
  }
}

object Type {
  def ctypeCheck(obj: Obj, atype: Type[Obj]): Boolean = obj.alive && atype.alive && (atype.isInstanceOf[__] || obj.range.hardQ(qOne).test(atype.domain.hardQ(qOne)))
}
