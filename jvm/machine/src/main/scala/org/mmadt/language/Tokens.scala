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

package org.mmadt.language

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object Tokens {

  val obj  = "obj"
  val bool = "bool"
  val str  = "str"
  val rec  = "rec"
  val int  = "int"
  val inst = "inst"

  val and     = "and"
  val as      = "as"
  val choose  = "choose"
  val count   = "count"
  val eqs     = "eq"
  val explain = "explain"
  val get     = "get"
  val id      = "id"
  val is      = "is"
  val fold    = "fold"
  val plus    = "plus"
  val map     = "map"
  val mult    = "mult"
  val neg     = "neg"
  val one     = "one"
  val gt      = "gt"
  val or      = "or"
  val put     = "put"
  val to      = "to"
  val from    = "from"
  val start   = "start"
  val model   = "model"
  val q       = "q"
  val zero    = "zero"

  val empty  = ""
  val btrue  = "true"
  val bfalse = "false"

  val q_mark         = "?"
  val plus_op,q_plus = "+"
  val mult_op,q_star = "*"
  val gt_op          = ">"
  val eqs_op         = "=="

  val ::  = ":"
  val :-> = "->"
  val :|  = "|"
  val :=> = "=>"
  val :<= = "<="

  def named(name:String):Boolean = !Set(bool,str,rec,int,inst).contains(name) // TODO: global immutable set

  def symbol(obj:Obj):String = obj match {
    case _:BoolType => Tokens.bool
    case _:IntType => Tokens.int
    case _:StrType => Tokens.str
    case _:RecType[_,_] => Tokens.rec
    case _:Type[Obj] => Tokens.obj
    case _ => throw new Exception("error: " + obj)
  }
}
