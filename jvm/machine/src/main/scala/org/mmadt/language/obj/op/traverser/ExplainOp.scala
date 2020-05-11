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

package org.mmadt.language.obj.op.traverser

import java.util

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.{BranchInstruction, TraceInstruction}
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.language.obj.{Inst, Obj, Lst, Str}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ExplainOp {
  this: Type[Obj] =>
  def explain(): Str = ExplainOp().exec(this)
}

object ExplainOp {
  def apply(): ExplainInst = new ExplainInst

  class ExplainInst extends VInst[Obj, Str]((Tokens.explain, Nil)) {
    override def exec(start: Obj): Str = str(ExplainOp.printableTable(asType(start))).start()
  }

  private type Row = (Int, Inst[Obj, Obj], Type[Obj], Type[Obj], mutable.LinkedHashMap[String, Obj])
  private def explain(atype: Type[Obj], state: mutable.LinkedHashMap[String, Obj], depth: Int = 0): List[Row] = {
    val report = atype.trace.foldLeft(List[Row]())((a, b) => {
      if (b._2.isInstanceOf[TraceInstruction]) state += (b._2.arg0[StrValue]().ground -> b._2.exec(b._1).asInstanceOf[Type[Obj]].range)
      val temp = if (b._2.isInstanceOf[TraceInstruction]) a else a :+ (depth, b._2, lastRange(b._1.asInstanceOf[Type[Obj]]), b._2.exec(b._1).asInstanceOf[Type[Obj]].range, mutable.LinkedHashMap(state.toSeq: _*))
      val inner = b._2.args().foldLeft(List[Row]())((x, y) => x ++ (y match {
        case branches: Lst[_] if b._2.isInstanceOf[BranchInstruction] => branches.ground._2.flatMap(x => List(x)).map {
          case btype: Type[_] => btype
          case bvalue: Value[_] => bvalue.start()
        }.flatMap(x => explain(x, mutable.LinkedHashMap(state.toSeq: _*), depth + 1))
        case btype: Type[Obj] => explain(btype, mutable.LinkedHashMap(state.toSeq: _*), depth + 1)
        case _ => Nil
      }))
      temp ++ inner
    })
    report
  }
  private def lastRange(atype: Type[Obj]): Type[Obj] = if (atype.root) atype else atype.linvert().range
  private val MAX_LENGTH_STRING = 40
  private def objStringClip(inst: Obj): String = {
    val instString = inst.toString.substring(0, Math.min(MAX_LENGTH_STRING, inst.toString.length))
    if (instString.length == MAX_LENGTH_STRING) instString + "..." else instString
  }
  def printableTable(atype: Type[Obj]): String = {
    val report = explain(atype, mutable.LinkedHashMap.empty[String, Obj])
    val c1 = report.map(x => objStringClip(x._2).length).max + 4
    val c2 = report.map(x => objStringClip(x._3).length).max + 4
    val c3 = report.map(x => objStringClip(x._4).length).max + 4
    val builder: StringBuilder = new StringBuilder()

    builder
      .append("instruction").append(stolenRepeat(" ", Math.abs(11 - c1)))
      .append("domain").append(stolenRepeat(" ", Math.abs(6 - c2)))
      .append(stolenRepeat(" ", 5))
      .append("range").append(stolenRepeat(" ", Math.abs(6 - c3)))
      .append("state").append("\n")
    builder.append(stolenRepeat("-", builder.length)).append("\n")
    report.foldLeft(builder)((a, b) => a
      .append(stolenRepeat(" ", b._1))
      .append(objStringClip(b._2)).append(stolenRepeat(" ", Math.abs(c1 - (objStringClip(b._2).length))))
      .append(objStringClip(b._3)).append(stolenRepeat(" ", Math.abs(c2 - (objStringClip(b._3).length) - (b._1))))
      .append("=>").append(stolenRepeat(" ", 3)).append(stolenRepeat(" ", b._1))
      .append(objStringClip(b._4)).append(stolenRepeat(" ", Math.abs(c3 - (objStringClip(b._4).length) - (b._1))))
      .append(b._5.foldLeft("")((x, y) => x + (y._2 + "<" + y._1 + "> "))).append("\n"))
    "\n" + atype.toString + "\n\n" + builder.toString()
  }
  // stolen from Scala distribution as this method doesn't exist in some distributions of Scala
  private def stolenRepeat(string: String, count: Int): String = {
    if (count < 0) throw new IllegalArgumentException("count is negative: " + count)
    if (count == 1) return string
    val len = string.length
    if (len == 0 || count == 0) return ""
    if (len == 1) {
      val single = new Array[Byte](count)
      util.Arrays.fill(single, string.charAt(0).asInstanceOf[Byte])
      return new String(single)
    }
    if (Integer.MAX_VALUE / count < len) throw new OutOfMemoryError("Repeating " + len + " bytes String " + count + " times will produce a String exceeding maximum size.")
    val limit = len * count
    val multiple = new Array[Byte](limit)
    System.arraycopy(string.getBytes, 0, multiple, 0, len)
    var copied = len
    while ( {
      copied < limit - copied
    }) {
      System.arraycopy(multiple, 0, multiple, copied, copied)
      copied <<= 1
    }
    System.arraycopy(multiple, 0, multiple, copied, limit - copied)
    new String(multiple)
  }
}
