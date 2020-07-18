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

package org.mmadt.language.gremlin
import org.mmadt.language.LanguageException
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.map.GetOp
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.StorageFactory._

import scala.collection.JavaConverters

object TraversalMonoid {
  def resolve(op: String, args: List[Obj]): List[Inst[Obj, Obj]] = {
    (op match {
      case "out" => GetOp(str("outE")) +: args.map(x => IsOp(__.get(str("label")).eqs(x))) :+ GetOp(str("inV"))
      case "outE" => GetOp(str("outE")) +: args.map(x => IsOp(__.get(str("label")).eqs(x)))
      case "inV" => List(GetOp(str("inV")))
      case "outV" => List(GetOp(str("outV")))
      case "V" => GetOp(str("V")) +: args.map(x => IsOp(__.get(str("id")).eqs(x)))
      case _ => throw LanguageException.unknownInstruction(op, JavaConverters.seqAsJavaList(args))
    }).asInstanceOf[List[Inst[Obj, Obj]]]
  }
}
