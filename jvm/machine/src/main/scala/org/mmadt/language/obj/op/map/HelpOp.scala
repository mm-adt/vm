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

package org.mmadt.language.obj.op.map

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.StorageFactory.lst
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait HelpOp {
  this:Obj =>
  def help[A <: Type[A]](atype:A):A = HelpOp(atype).exec(this)
}
object HelpOp extends Func[Obj, Obj] {
  override val preArgs:Boolean = false
  def apply[A <: Obj](atype:A):Inst[Obj, A] = new VInst[Obj, A](g = (Tokens.help, List(atype)), func = this)
  override def apply(start:Obj, inst:Inst[Obj, Obj]):Obj = {
    val domainT:Type[Obj] = inst.arg0[Obj].domain
    val rangeT:Type[Obj] = inst.arg0[Obj].range
    lst(g = (Tokens.`;`, start.model.dtypes
      .filter(t => __.isAnon(domainT) || domainT.name == t.domain.name)
      .filter(t => __.isAnon(rangeT) || rangeT.name == t.range.name)
    ))
  }
}