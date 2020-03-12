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

package org.mmadt.language.obj.op

import java.util.ServiceLoader

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.branch.ChooseOp
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.reduce.{CountOp, FoldOp}
import org.mmadt.language.obj.op.sideeffect.{ErrorOp, PutOp}
import org.mmadt.language.obj.op.traverser.{ExplainOp, FromOp, ToOp}
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.StorageProvider

import scala.collection.JavaConverters
import scala.collection.JavaConverters.asScalaIterator

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object OpInstResolver {
  private val providers:List[StorageProvider] = asScalaIterator(ServiceLoader.load(classOf[StorageProvider]).iterator()).toList
  private def service(op:String,args:List[Obj]):Option[Inst] = providers.iterator
    .map(_.resolveInstruction(op,JavaConverters.seqAsJavaList(args)))
    .find(_.isPresent)
    .map(_.get())

  def resolve(op:String,args:List[Obj]):Inst ={
    op match {
      case Tokens.and | Tokens.and_op => AndOp(args.head)
      case Tokens.or | Tokens.or_op => OrOp(args.head)
      case Tokens.plus | Tokens.plus_op => PlusOp(args.head)
      case Tokens.mult | Tokens.mult_op => MultOp(args.head)
      case Tokens.gt | Tokens.gt_op => GtOp(args.head)
      case Tokens.eqs | Tokens.eqs_op => EqsOp(args.head)
      case Tokens.is => IsOp(args.head)
      case Tokens.get => args match {
        case List(key:Obj,typeHint:Type[Obj]) => GetOp(key,typeHint)
        case List(key:Obj) => GetOp(key)
      }
      case Tokens.map => MapOp(args.head)
      case Tokens.neg => NegOp()
      case Tokens.count => CountOp()
      case Tokens.explain => ExplainOp()
      case Tokens.put => PutOp(args.head,args.tail.head)
      case Tokens.from =>
        val label = args.head.asInstanceOf[StrValue]
        args.tail match {
          case Nil => FromOp(label)
          case obj:Obj => FromOp(label,obj)
        }
      case Tokens.fold => args.tail.tail.head match {
        case x:__ => FoldOp((args.head.asInstanceOf[StrValue].value(),args.tail.head),x)
        case x:Type[Obj] => FoldOp((args.head.asInstanceOf[StrValue].value(),args.tail.head),x)
      }
      case Tokens.error => ErrorOp(args.head.asInstanceOf[StrValue].value())
      case Tokens.to => ToOp(args.head.asInstanceOf[StrValue])
      case Tokens.choose => ChooseOp(args.head.asInstanceOf[RecType[Obj,Obj]])
      case Tokens.id => IdOp()
      case Tokens.q => QOp()
      case Tokens.zero => ZeroOp()
      case Tokens.one => OneOp()
      //////////////////////////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////////////////////////
      case _ => service(op,args) match {
        case Some(inst) => inst
        case None => throw new IllegalArgumentException("Unknown instruction: " + op + "," + args)
      }
    }
  }


}
