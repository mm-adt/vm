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

package org.mmadt.language.obj

import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.map.{AppendOp, HeadOp, TailOp}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory._

trait Lst[A <: Obj] extends Obj
  with AppendOp[A]
  with HeadOp[A]
  with TailOp[A]
  with Type[Lst[A]] {
  def value(): Lst[A]
}

object Lst {
  @scala.annotation.tailrec
  def lstRoot[A <: Obj](alst: Lst[A]): A = if (alst.root) throw new LanguageException("no head") else if(alst.via._1.root) alst.via._2.arg0[A]() else lstRoot[A](alst.via._1.asInstanceOf[Lst[A]])
  @scala.annotation.tailrec
  def lstTail[A <: Obj](alst: Lst[A],build:Lst[A]=lst[A]): Lst[A] =
    if (alst.root)
      alst
    else if (alst.via._1.root)
      build
    else
      lstTail[A](alst.via._1.asInstanceOf[Lst[A]],build.append(alst.via._2.arg0[A]()))

  def encode[A <: Obj](seq: Seq[A]): Lst[A] = seq.foldLeft(lst[A])((b, a) => b.append(a))
  def decode[A <: Obj](alist: Lst[A]): List[A] = {
    alist.lineage.foldLeft(List.empty[A])((b, a) => {
      a._2.op() match {
        case Tokens.append => b :+ a._2.arg0[A]()
        case Tokens.tail => b.tail
      }
    })
  }
}