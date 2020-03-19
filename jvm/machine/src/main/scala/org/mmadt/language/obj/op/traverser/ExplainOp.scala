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
import org.mmadt.language.obj.`type`.{RecType, StrType, Type}
import org.mmadt.language.obj.op.TraverserInstruction
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, OValue, Obj, Str}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ExplainOp {
  this:Type[Obj] =>

  def explain():StrType = vstr(name = Tokens.str,value = ExplainOp.printableTable(asType(this)),q = qOne).start()
}

object ExplainOp {
  def apply():Inst[Obj,Str] = new ExplainInst

  class ExplainInst extends VInst[Obj,Str]((Tokens.explain,Nil)) {
    override def apply(trav:Traverser[Obj]):Traverser[Str] ={
      trav.split(trav.obj().asInstanceOf[Type[Obj]].explain())
    }
  }

  private type Row = (Int,Inst[Obj,Obj],Type[Obj],Type[Obj],mutable.LinkedHashMap[String,Obj])

  private def explain(atype:Type[Obj],state:mutable.LinkedHashMap[String,Obj],depth:Int = 0):List[Row] ={
    val report = atype.insts.foldLeft(List[Row]())((a,b) => {
      if (b._2.isInstanceOf[TraverserInstruction]) state += (b._2.arg0[StrValue]().value -> b._2.apply(Traverser.standard(b._1)).obj().asInstanceOf[Type[Obj]].range)
      val temp  = if (b._2.isInstanceOf[TraverserInstruction]) a else a :+ (depth,b._2,lastRange(b._1),b._2.apply(Traverser.standard(b._1)).obj().asInstanceOf[Type[Obj]].range,mutable.LinkedHashMap(state.toSeq:_*))
      val inner = b._2.args().foldLeft(List[Row]())((x,y) => x ++ (y match {
        case branches:RecType[Obj,Obj] if b._2.op() == Tokens.choose => branches.value().flatMap(x => List(x._1,x._2)).map{
          case btype:Type[Obj] => btype
          case bvalue:OValue[Obj] => bvalue.start().asInstanceOf[Type[Obj]]
        }.flatMap(x => explain(x,mutable.LinkedHashMap(state.toSeq:_*),depth + 1))
        case btype:Type[Obj] => explain(btype,mutable.LinkedHashMap(state.toSeq:_*),depth + 1)
        case _ => Nil
      }))
      temp ++ inner
    })
    report
  }

  private def lastRange(atype:Type[Obj]):Type[Obj] = if (atype.insts.isEmpty) atype else atype.linvert().range
  private val MAX_LENGTH_STRING = 40
  private def instMax(inst:Inst[Obj,Obj]):String ={
    val instString = inst.toString.substring(0,Math.min(MAX_LENGTH_STRING,inst.toString.length))
    if (instString.length == MAX_LENGTH_STRING) instString + "..." else instString
  }

  def printableTable(atype:Type[Obj]):String ={
    val report                = explain(atype,mutable.LinkedHashMap.empty[String,Obj])
    val c1                    = report.map(x => instMax(x._2).length).max + 4
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
      .append(instMax(b._2)).append(" ".repeat(Math.abs(c1 - (instMax(b._2).length))))
      .append(b._3).append(" ".repeat(Math.abs(c2 - (b._3.toString.length) - (b._1))))
      .append("=>").append(" ".repeat(3)).append(" ".repeat(b._1))
      .append(b._4).append(" ".repeat(Math.abs(c3 - (b._4.toString.length) - (b._1))))
      .append(b._5.foldLeft("")((x,y) => x + (y._2 + "<" + y._1 + "> "))).append("\n"))
    "\n" + atype.toString + "\n\n" + builder.toString()
  }
}
