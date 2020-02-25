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

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.StrType
import org.mmadt.language.obj.op.TraverserInstruction
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, OType, Obj}
import org.mmadt.storage.obj.value.VInst
import org.mmadt.storage.obj.{asType, qOne, str => vstr}

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ExplainOp {
  this:OType =>

  def explain():StrType = vstr(ExplainOp.printableTable(asType(this))).start()
}

object ExplainOp {
  def apply():Inst = new VInst((Tokens.explain,Nil),qOne,((a:OType,b:List[Obj]) => a.explain()).asInstanceOf[(Obj,List[Obj]) => Obj]) with TraverserInstruction

  private type Row = (Int,Inst,OType,OType,mutable.LinkedHashMap[String,Obj])

  private def explain(atype:OType,state:mutable.LinkedHashMap[String,Obj],depth:Int = 0):List[Row] ={
    val report = atype.insts().foldLeft(List[Row]())((a,b) => {
      if (b._2.isInstanceOf[TraverserInstruction]) state += (b._2.arg().asInstanceOf[StrValue].value() -> b._2.apply(b._1).asInstanceOf[OType].range())
      val temp  = if (b._2.isInstanceOf[TraverserInstruction]) a else a :+ (depth,b._2,lastRange(b._1),b._2.apply(b._1).asInstanceOf[OType].range(),mutable.LinkedHashMap(state.toSeq:_*))
      val inner = b._2.args().foldLeft(List[Row]())((x,y) => x ++ (y match {
        case btype:OType => explain(btype,mutable.LinkedHashMap(state.toSeq:_*),depth + 1)
        case _ => Nil
      }))
      temp ++ inner
    })
    report
  }

  private def lastRange(atype:OType):OType = if (atype.insts().isEmpty) atype else atype.linvert().range()
  private def instStr(inst:Inst,length:scala.Int):String = if (inst.isInstanceOf[TraverserInstruction]) " ".repeat(length) else inst.toString

  def printableTable(atype:OType):String ={
    val report                = explain(atype,mutable.LinkedHashMap.empty[String,Obj])
    val c1                    = report.map(x => x._2.toString.length).max + 4
    val c2                    = report.map(x => x._3.toString.length).max + 4
    val c3                    = report.map(x => x._4.toString.length).max + 4
    val builder:StringBuilder = new StringBuilder()
    builder
      .append("instruction").append(" ".repeat(Math.abs(11 - c1)))
      .append("domain").append(" ".repeat(Math.abs(6 - c2)))
      .append(" ".repeat(5))
      .append("range").append(" ".repeat(Math.abs(6 - c3)))
      .append("state").append("\n")
    builder.append("-".repeat(builder.length)).append("\n")
    report.foldLeft(builder)((a,b) => a
      .append(" ".repeat(b._1))
      .append(instStr(b._2,b._2.toString.length)).append(" ".repeat(Math.abs(c1 - (b._2.toString.length))))
      .append(b._3).append(" ".repeat(Math.abs(c2 - (b._3.toString.length) - (b._1))))
      .append("=>").append(" ".repeat(3)).append(" ".repeat(b._1))
      .append(b._4).append(" ".repeat(Math.abs(c3 - (b._4.toString.length) - (b._1))))
      .append(b._5.foldLeft("")((x,y) => x + (y._2 + "<" + y._1 + "> "))).append("\n"))
    "\n" + atype.toString + "\n\n" + builder.toString()
  }
}
