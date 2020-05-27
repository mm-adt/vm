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
    Tokens.noop, Tokens.and, Tokens.count, Tokens.eqs, Tokens.explain, Tokens.get, Tokens.is, Tokens.mult,
    Tokens.plus, Tokens.gt, Tokens.lt, Tokens.gte, Tokens.lte, Tokens.path, Tokens.put, Tokens.map, Tokens.from, Tokens.to, Tokens.as, Tokens.a, // Tokens.model (shared with global name)
    Tokens.split, Tokens.combine, Tokens.merge, Tokens.given, Tokens.trace, Tokens.start, Tokens.`type`, Tokens.repeat, Tokens.last,
    Tokens.btrue, Tokens.bfalse, Tokens.int, Tokens.bool, Tokens.lst, Tokens.rec, Tokens.real, Tokens.str, Tokens.obj, Tokens.anon)

  val -> = "->"
  val :: = ":"
  val :<= = "<="
  val :=> = "=>"
  val `,` = ","
  val `;` = ";"
  val `type` = "type"
  val a = "a"
  val a_op = "?"
  val add = "add"
  val and = "and"
  val and_op = "&&"
  val as = "as"
  val bfalse = "false"
  val btrue = "true"
  val combine = "combine"
  val combine_op = "="
  val count = "count"
  val empty = ""
  val eqs = "eq"
  val eqs_op = "=="
  val error = "error"
  val explain = "explain"
  val fold = "fold"
  val from = "from"
  val get = "get"
  val get_op = "."
  val given = "given"
  val given_op = "-->"
  val groupCount = "groupCount"
  val gt = "gt"
  val gt_op = ">"
  val gte = "gte"
  val gte_op = ">="
  val head = "head"
  val id = "id"
  val is = "is"
  val last = "last"
  val lt = "lt"
  val lt_op = "<"
  val lte = "lte"
  val lte_op = "=<"
  val map = "map"
  val merge = "merge"
  val merge_op = ">-"
  val model = "model"
  val mult = "mult"
  val mult_op, q_star = "*"
  val neg = "neg"
  val noop = "noop"
  val one = "one"
  val or = "or"
  val or_op = "||"
  val path = "path"
  val plus = "plus"
  val plus_op, q_plus = "+"
  val pow_op = "^"
  val put = "put"
  val q = "q"
  val q_mark = "?"
  val repeat = "repeat"
  val space = " "
  val split = "split"
  val split_op = "-<"
  val start = "start"
  val tail = "tail"
  val to = "to"
  val trace = "trace"
  val zero = "zero"
  val | = "|"

  def named(name: String): Boolean = !Set(bool, str, real, rec, int, inst, lst).contains(name) // TODO: global immutable set
}
