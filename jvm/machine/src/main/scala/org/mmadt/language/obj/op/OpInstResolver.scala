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
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.op.traverser.{ExplainOp, FromOp, ToOp}
import org.mmadt.language.obj.value.{BoolValue, IntValue, StrValue}
import org.mmadt.language.obj.{Inst, ORecType, ORecValue, Obj}
import org.mmadt.storage.StorageProvider

import scala.collection.JavaConverters
import scala.collection.JavaConverters.asScalaIterator

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object OpInstResolver {
  def resolve(op:String,args:List[Obj]):Inst ={
    op match {
      case Tokens.and | Tokens.and_op => args.head match {
        case arg:BoolType => AndOp(arg)
        case arg:BoolValue => AndOp(arg)
        case arg:__ => AndOp(arg)
      }
      case Tokens.or | Tokens.or_op => args.head match {
        case arg:BoolType => OrOp(arg)
        case arg:BoolValue => OrOp(arg)
        case arg:__ => OrOp(arg)
      }
      case Tokens.plus | Tokens.plus_op => args.head match {
        case arg:IntValue => PlusOp(arg)
        case arg:IntType => PlusOp(arg)
        case arg:StrValue => PlusOp(arg)
        case arg:StrType => PlusOp(arg)
        case arg:ORecValue => PlusOp(arg)
        case arg:ORecType => PlusOp(arg)
        case arg:__ => PlusOp(arg)
      }
      case Tokens.mult | Tokens.mult_op => args.head match {
        case arg:IntValue => MultOp(arg)
        case arg:IntType => MultOp(arg)
        case arg:__ => MultOp(arg)
      }
      case Tokens.gt | Tokens.gt_op => args.head match {
        case arg:IntValue => GtOp(arg)
        case arg:IntType => GtOp(arg)
        case arg:StrValue => GtOp(arg)
        case arg:StrType => GtOp(arg)
        case arg:__ => GtOp(arg)
      }
      case Tokens.eqs | Tokens.eqs_op => args.head match {
        case arg:BoolValue => EqsOp(arg)
        case arg:BoolType => EqsOp(arg)
        case arg:IntValue => EqsOp(arg)
        case arg:IntType => EqsOp(arg)
        case arg:StrValue => EqsOp(arg)
        case arg:StrType => EqsOp(arg)
        case arg:ORecValue => EqsOp(arg)
        case arg:ORecType => EqsOp(arg)
        case arg:__ => EqsOp(arg)
      }
      case Tokens.is => args.head match {
        case arg:BoolValue => IsOp(arg)
        case arg:BoolType => IsOp(arg)
        case arg:__ => IsOp(arg)
      }
      case Tokens.get => args match {
        case List(key:Obj,typeHint:Type[Obj]) => GetOp(key,typeHint)
        case List(key:Obj) => GetOp(key)
      }
      case Tokens.map => args.head match {
        case arg:__ => MapOp(arg)
        case arg:Obj => MapOp(arg)
      }
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

  private lazy val loader:ServiceLoader[StorageProvider] = ServiceLoader.load(classOf[StorageProvider])
  private def service(op:String,args:List[Obj]):Option[Inst] = Option(asScalaIterator(loader.iterator)
    .map(s => s.resolveInstruction(op,JavaConverters.seqAsJavaList(args)))
    .filter(i => i.isPresent)
    .map(i => i.get()).next())
}
