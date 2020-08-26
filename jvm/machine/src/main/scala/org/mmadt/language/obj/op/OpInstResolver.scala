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

package org.mmadt.language.obj.op

import java.util.ServiceLoader

import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.branch._
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.initial.StartOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.reduce.{CountOp, FoldOp, SumOp}
import org.mmadt.language.obj.op.rewrite.{BranchRewrite, IdRewrite}
import org.mmadt.language.obj.op.sideeffect.{ErrorOp, LoadOp, PutOp}
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.op.trace._
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, Lst, Obj}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageProvider

import scala.collection.JavaConverters
import scala.collection.JavaConverters.asScalaIterator

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object OpInstResolver {
  private val providers: List[StorageProvider] = asScalaIterator(ServiceLoader.load(classOf[StorageProvider]).iterator()).toList

  private def service(op: String, args: List[Obj]): Option[Inst[Obj, Obj]] = providers.iterator
    .map(_.resolveInstruction(op, JavaConverters.seqAsJavaList(args)))
    .find(_.isPresent)
    .map(_.get())

  def rewrites: List[Inst[Obj, Obj]] = providers.flatMap(x => asScalaIterator(x.rewrites().iterator()))

  def applyRewrites[A <: Obj](obj: A): A = {
    if (obj.trace.map(x => x._2).exists(x => providers.map(y => "=" + y.name()).contains(x.op)))
      this.rewrites.foldLeft(obj.domainObj)((x, y) => y.exec(x)).compute(obj)
    else
      obj
  }

  def resolve[S <: Obj, E <: Obj](op: String, args: List[Obj]): Inst[S, E] = {
    (op match {
      case Tokens.rule_id => IdRewrite()
      case Tokens.rule_unity => BranchRewrite()

      case Tokens.head => HeadOp()
      case Tokens.last => LastOp()
      case Tokens.tail => TailOp()
      case Tokens.empty => EmptyOp()
      case Tokens.split | Tokens.split_op => SplitOp(args.head)
      case Tokens.combine | Tokens.combine_op => CombineOp(args.head)
      case Tokens.merge | Tokens.merge_op => MergeOp()
      case Tokens.repeat => RepeatOp(args.head, args.tail.head)
      case Tokens.path => args.headOption.map(x => PathOp(x.asInstanceOf[Lst[Obj]])).getOrElse(PathOp())
      //
      case Tokens.model => args.head match {
        case model: Model => ModelOp(model)
        case file: StrValue => ModelOp(file)
      }
      case Tokens.load => LoadOp(args.head)
      case Tokens.noop => NoOp()
      case Tokens.a => AOp(args.head)
      case Tokens.is_a_op => IsOp(__.a(args.head))
      case Tokens.as | Tokens.as_op => AsOp(args.head)
      case Tokens.not | Tokens.not_op => NotOp(args.head)
      case Tokens.and | Tokens.and_op => AndOp(args: _*)
      case Tokens.or | Tokens.or_op => OrOp(args: _*)
      case Tokens.plus | Tokens.plus_op | Tokens.sum_op => PlusOp(args.head)
      case Tokens.mult | Tokens.mult_op | Tokens.product_op => MultOp(args.head)
      case Tokens.gt | Tokens.gt_op => GtOp(args.head)
      case Tokens.gte | Tokens.gte_op => GteOp(args.head)
      case Tokens.lt | Tokens.lt_op => LtOp(args.head)
      case Tokens.lte | Tokens.lte_op => LteOp(args.head)
      case Tokens.eqs | Tokens.eqs_op => EqsOp(args.head)
      case Tokens.is => IsOp(args.head)
      case Tokens.get => args match {
        case List(key: Obj, typeHint: Type[Obj]) => GetOp(key, typeHint)
        case List(key: Obj) => GetOp(key)
      }
      case Tokens.juxt | Tokens.juxt_op => JuxtOp(args.head)
      case Tokens.branch => BranchOp(args.head)
      case Tokens.map => MapOp(args.head)
      case Tokens.neg => NegOp()
      case Tokens.count => CountOp()
      case Tokens.sum => SumOp()
      case Tokens.explain => ExplainOp()

      case Tokens.put => PutOp(args.head, args.tail.head)
      case Tokens.`type` => TypeOp()
      case Tokens.from =>
        val label = args.head.asInstanceOf[StrValue]
        args.tail match {
          case Nil => FromOp(label)
          case list: List[Obj] => FromOp(label, list.head)
          case _ => throw new IllegalStateException
        }
      case Tokens.fold => if (args.tail.isEmpty) FoldOp(args.head) else FoldOp(args.head, args.tail.head)
      case Tokens.error => ErrorOp(args.head.asInstanceOf[StrValue].g)
      case Tokens.defs => DefsOp()
      case Tokens.define => DefineOp(args: _*)
      case Tokens.rewrite => RewriteOp(args.head)
      case Tokens.to => ToOp(args.head.asInstanceOf[StrValue])
      case Tokens.id => IdOp()
      case Tokens.q => QOp()
      case Tokens.zero => ZeroOp()
      case Tokens.one => OneOp()
      case Tokens.start => StartOp(args.head)
      //////////////////////////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////////////////////////
      case _ => service(op, args) match {
        case Some(inst) => inst
        case None => throw LanguageException.unknownInstruction(op, JavaConverters.seqAsJavaList(args))
      }
    }).asInstanceOf[Inst[S, E]]
  }


}
