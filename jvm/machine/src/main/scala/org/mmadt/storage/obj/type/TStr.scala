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

package org.mmadt.storage.obj.`type`

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.{StrType, Type}
import org.mmadt.language.obj.{Inst, IntQ, Obj}
import org.mmadt.storage.obj._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TStr(name:String,insts:List[(Type[Obj],Inst)],quantifier:IntQ) extends AbstractTObj(name,insts,quantifier) with StrType {
  def this() = this(Tokens.str,Nil,qOne)
  def this(name:String) = this(name,Nil,qOne)
  override def compose(inst:Inst):this.type = str(inst,quantifier).asInstanceOf[this.type]
  override def range():this.type = new TStr(name,Nil,quantifier).asInstanceOf[this.type]
  override def q(quantifier:IntQ):this.type = new TStr(name,insts,quantifier).asInstanceOf[this.type]
}

