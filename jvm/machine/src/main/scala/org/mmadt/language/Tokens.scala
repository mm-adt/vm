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

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object Tokens {

  lazy val reservedOps: List[String] = List(
    Tokens.define, Tokens.neg,
    Tokens.noop, Tokens.and, Tokens.count, Tokens.eqs, Tokens.explain, Tokens.get, Tokens.is, Tokens.mult,
    Tokens.plus, Tokens.gte, Tokens.gt, Tokens.lte, Tokens.lt, Tokens.path, Tokens.put, Tokens.map, Tokens.from, Tokens.to, Tokens.as,
    Tokens.split, Tokens.combine, Tokens.merge, Tokens.given, Tokens.trace, Tokens.start, Tokens.`type`, Tokens.repeat, Tokens.last,
    Tokens.and, Tokens.or, Tokens.id, Tokens.zero, Tokens.one, Tokens.a, Tokens.head, Tokens.tail, Tokens.juxt, Tokens.fold)

  lazy val reservedTypes: List[String] = List(Tokens.btrue, Tokens.bfalse, Tokens.int, Tokens.bool, Tokens.lst, Tokens.rec, Tokens.real, Tokens.str, Tokens.obj, Tokens.anon)

  val anon = "_"
  val obj = "obj"
  val bool = "bool"
  val str = "str"
  val rec = "rec"
  val int = "int"
  val real = "real"
  val lst = "lst"
  val inst = "inst"

  val define = "define"
  val a = "a"
  val head = "head"
  val tail = "tail"
  val last = "last"
  val juxt = "juxt"
  val noop = "noop"
  val and = "and"
  val as = "as"
  val count = "count"
  val error = "error"
  val eqs = "eq"
  val explain = "explain"
  val get = "get"
  val given = "given"
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
  val q = "q"
  val zero = "zero"
  val trace = "trace"
  val `type` = "type"

  val combine = "combine"
  val merge = "merge"
  val split = "split"
  val repeat = "repeat"

  val empty = ""
  val space = " "
  val btrue = "true"
  val bfalse = "false"

  val q_mark = "?"
  val plus_op, q_plus = "+"
  val mult_op, q_star = "*"
  val product_op = "⨂"
  val sum_op = "⨁"
  val gt_op = ">"
  val gte_op = ">="
  val lt_op = "<"
  val lte_op = "=<"
  val eqs_op = "=="
  val and_op = "&&"
  val or_op = "||"
  val get_op = "."
  val a_op = "?"

  val split_op = "-<"
  val pow_op = "^"
  val combine_op = "="
  val merge_op = ">-"
  val given_op = "-->"
  val as_op = "~"
  val juxt_op = "=>"


  val :: = ":"
  val -> = "->"
  val | = "|"
  val `;` = ";"
  val `,` = ","
  val :=> = "=>"
  val :<= = "<="

  def named(name: String): Boolean = !Set(bool, str, real, rec, int, inst, lst, anon, obj).contains(name) // TODO: global immutable set
  def tryName[A <: Obj](fromObj: Obj, toObj: A): A = if (Tokens.named(fromObj.name)) toObj.named(fromObj.name) else toObj

  lazy val LANGLE = "<"
  lazy val RANGLE = ">"
  lazy val LCURL = "{"
  lazy val RCURL = "}"
  lazy val LROUND = "("
  lazy val RROUND = ")"
  lazy val QZERO = "{0}"
  lazy val COMMA = ","
  lazy val LBRACKET = "["
  lazy val RBRACKET = "]"
  lazy val PIPE = "|"
  lazy val AMPERSAND = "&"
  lazy val EMPTY = ""
  lazy val LDARROW = "<="
  lazy val RDARROW = "=>"
  lazy val RRDARROW = "==>"
  lazy val RSARROW = "->"
  lazy val EMPTYREC = "(->)"
  lazy val EMPTYLST = "(  )"
  lazy val COLON = ":"
  lazy val PERIOD = "."
  lazy val SEMICOLON = ";"
  lazy val SQUOTE = "'"
  lazy val CROSS = "+"
}
