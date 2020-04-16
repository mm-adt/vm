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

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object Tokens {

  lazy val reserved: Set[String] = Set(
    Tokens.noop, Tokens.and, Tokens.branch, Tokens.choose, Tokens.count, Tokens.eqs, Tokens.explain, Tokens.get, Tokens.is, Tokens.mult,
    Tokens.plus, Tokens.gt, Tokens.path, Tokens.put, Tokens.map, Tokens.from, Tokens.to, Tokens.as, Tokens.a, // Tokens.model (shared with global name)
    Tokens.btrue, Tokens.bfalse)

  val obj = "obj"
  val bool = "bool"
  val str = "str"
  val rec = "rec"
  val int = "int"
  val real = "real"
  val inst = "inst"
  val lst = "lst"
  val __ = "__"

  val a = "a"
  val add = "add"
  val append = "append" // TODO: for demo purposes only
  val head = "head"
  val tail = "tail"
  val noop = "noop"
  val and = "and"
  val as = "as"
  val branch = "branch"
  val choose = "choose"
  val count = "count"
  val error = "error"
  val eqs = "eq"
  val explain = "explain"
  val get = "get"
  val id = "id"
  val is = "is"
  val lt = "lt"
  val lte = "lte"
  val fold = "fold"
  val plus = "plus"
  val map = "map"
  val mult = "mult"
  val neg = "neg"
  val one = "one"
  val groupCount = "groupCount"
  val gt = "gt"
  val gte = "gte"
  val or = "or"
  val path = "path"
  val put = "put"
  val to = "to"
  val from = "from"
  val start = "start"
  val model = "model"
  val q = "q"
  val zero = "zero"

  val empty = ""
  val btrue = "true"
  val bfalse = "false"

  val q_mark = "?"
  val plus_op, q_plus = "+"
  val mult_op, q_star = "*"
  val gt_op = ">"
  val gte_op = ">="
  val lt_op = "<"
  val lte_op = "=<"
  val eqs_op = "=="
  val and_op = "&&"
  val or_op = "||"
  val get_op = "."
  val a_op = "?"

  val :: = ":"
  val :-> = "->"
  val :| = "|"
  val :=> = "=>"
  val :<= = "<="

  def named(name: String): Boolean = !Set(bool, str, real, rec, int, inst, lst).contains(name) // TODO: global immutable set
}
