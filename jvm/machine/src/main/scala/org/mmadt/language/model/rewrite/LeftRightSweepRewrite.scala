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

package org.mmadt.language.model.rewrite

import org.mmadt.language.Tokens
import org.mmadt.language.model.Model
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.OpInstResolver
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object LeftRightSweepRewrite {

  def rewrite[S <: Obj](model: Model, atype: Type[S], btype: Type[S], start: S): S = {
    if (!atype.root) {
      model.get(atype) match {
        case Some(right: Type[S]) => rewrite(model, right, btype, start)
        case None =>
          val inst: Inst[Obj, Obj] = OpInstResolver.resolve(atype.via._2.op(), rewriteArgs(model, atype.rinvert[Type[S]]().range, atype.via._2.asInstanceOf[Inst[Obj, Obj]], start)).q(atype.via._2.q)
          rewrite(model,
            atype.rinvert(),
            inst.exec(atype.rinvert[Type[S]]().range).asInstanceOf[Type[S]].compute(btype).asInstanceOf[Type[S]], // might need a model.resolve down the road
            start)
      }
    } else if (!btype.root) {
      rewrite(model,
        btype.linvert(),
        btype.linvert().domain(),
        btype.trace.head._2.exec(start)).asInstanceOf[S]
    }
    else start
  }

  // if no match, then apply the instruction after rewriting its arguments
  private def rewriteArgs[S <: Obj](model: Model, start: Type[S], inst: Inst[Obj, Obj], end: S): List[Obj] = {
    inst.op() match {
      case Tokens.a | Tokens.as | Tokens.map | Tokens.put | Tokens.model | Tokens.split => inst.args().map {
        case atype: Type[_] if isSymbol(atype) => model(atype)
        case other => other
      }
      case x if x == Tokens.choose || x == Tokens.branch =>
        def branching(obj: Obj): Obj = {
          obj match {
            case branchType: Type[S] => rewrite(model, branchType, start, start)
            case branchValue: Value[_] => branchValue
          }
        }
        List(trec(name = Tokens.rec, inst.arg0[ORecType]().value.map(x => (branching(x._1), branching(x._2)))))
      case _ => inst.args().map {
        case atype: Type[_] => rewrite(model, atype, start, start)
        case avalue: Value[_] => avalue
      }
    }
  }


}