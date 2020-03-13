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
import org.mmadt.language.obj.op.model.AsOp
import org.mmadt.language.obj.op.reduce.{CountOp, FoldOp}
import org.mmadt.language.obj.op.sideeffect.{AddOp, ErrorOp, PutOp}
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
  private def service(op:String,args:List[Obj]):Option[Inst[Obj,Obj]] = providers.iterator
    .map(_.resolveInstruction(op,JavaConverters.seqAsJavaList(args)))
    .find(_.isPresent)
    .map(_.get())

  def resolve[S <: Obj,E <: Obj](op:String,args:List[Obj]):Inst[S,E] ={
    op match {
      case Tokens.add => AddOp(args.head).asInstanceOf[Inst[S,E]]
      case Tokens.a | Tokens.a_op => AOp(args.head.asInstanceOf[Type[Obj]]).asInstanceOf[Inst[S,E]]
      case Tokens.as => AsOp(args.head).asInstanceOf[Inst[S,E]]
      case Tokens.and | Tokens.and_op => AndOp(args.head).asInstanceOf[Inst[S,E]]
      case Tokens.or | Tokens.or_op => OrOp(args.head).asInstanceOf[Inst[S,E]]
      case Tokens.plus | Tokens.plus_op => PlusOp(args.head).asInstanceOf[Inst[S,E]]
      case Tokens.mult | Tokens.mult_op => MultOp(args.head).asInstanceOf[Inst[S,E]]
      case Tokens.gt | Tokens.gt_op => GtOp(args.head).asInstanceOf[Inst[S,E]]
      case Tokens.eqs | Tokens.eqs_op => EqsOp(args.head).asInstanceOf[Inst[S,E]]
      case Tokens.is => IsOp(args.head).asInstanceOf[Inst[S,E]]
      case Tokens.get => args match {
        case List(key:Obj,typeHint:Type[Obj]) => GetOp(key,typeHint).asInstanceOf[Inst[S,E]]
        case List(key:Obj) => GetOp(key).asInstanceOf[Inst[S,E]]
      }
      case Tokens.map => MapOp(args.head).asInstanceOf[Inst[S,E]]
      case Tokens.neg => NegOp().asInstanceOf[Inst[S,E]]
      case Tokens.count => CountOp().asInstanceOf[Inst[S,E]]
      case Tokens.explain => ExplainOp().asInstanceOf[Inst[S,E]]
      case Tokens.put => PutOp(args.head,args.tail.head).asInstanceOf[Inst[S,E]]
      case Tokens.from =>
        val label = args.head.asInstanceOf[StrValue]
        args.tail match {
          case Nil => FromOp(label).asInstanceOf[Inst[S,E]]
          case obj:Obj => FromOp(label,obj).asInstanceOf[Inst[S,E]]
        }
      case Tokens.fold => args.tail.tail.head match {
        case x:__ => FoldOp((args.head.asInstanceOf[StrValue].value,args.tail.head),x).asInstanceOf[Inst[S,E]]
        case x:Type[Obj] => FoldOp((args.head.asInstanceOf[StrValue].value,args.tail.head),x).asInstanceOf[Inst[S,E]]
      }
      case Tokens.error => ErrorOp(args.head.asInstanceOf[StrValue].value).asInstanceOf[Inst[S,E]]
      case Tokens.to => ToOp(args.head.asInstanceOf[StrValue]).asInstanceOf[Inst[S,E]]
      case Tokens.choose => ChooseOp(args.head.asInstanceOf[RecType[S,E]])
      case Tokens.id => IdOp().asInstanceOf[Inst[S,E]]
      case Tokens.q => QOp().asInstanceOf[Inst[S,E]]
      case Tokens.zero => ZeroOp().asInstanceOf[Inst[S,E]]
      case Tokens.one => OneOp().asInstanceOf[Inst[S,E]]
      //////////////////////////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////////////////////////
      case _ => service(op,args) match {
        case Some(inst) => inst.asInstanceOf[Inst[S,E]]
        case None => throw new IllegalArgumentException("Unknown instruction: " + op + "," + args)
      }
    }
  }


}
