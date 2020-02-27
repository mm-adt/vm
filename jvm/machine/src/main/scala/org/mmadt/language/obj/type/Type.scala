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

package org.mmadt.language.obj.`type`

import org.mmadt.language.Printable
import org.mmadt.language.obj._
import org.mmadt.language.obj.op.map.{IdOp,MapOp,QOp}
import org.mmadt.language.obj.op.model.{AsOp,ModelOp}
import org.mmadt.language.obj.op.reduce.{CountOp,FoldOp}
import org.mmadt.language.obj.op.traverser.{ExplainOp,FromOp}
import org.mmadt.language.obj.value.{StrValue,Value}
import org.mmadt.processor.Processor
import org.mmadt.processor.obj.`type`.util.InstUtil
import org.mmadt.storage.obj._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Type[T <: Type[T]] extends Obj
  with ExplainOp
  with ModelOp {

  def canonical():this.type = this.range().q(qOne) //
  def range():this.type //

  def domain[D <: OType]():D = (this.insts() match {
    case Nil => this
    case i:List[(OType,Inst)] => i.head._1.range()
  }).asInstanceOf[D]

  def insts():List[(OType,Inst)] //

  def linvert():this.type ={
    ((this.insts().tail match {
      case Nil => this.range()
      case i => i.foldLeft[Obj](i.head._1.range())((btype,inst) => inst._2.apply(btype))
    }) match {
      case vv:Value[_] => vv.start()
      case x => x
    }).asInstanceOf[this.type]
  }

  def rinvert[TT <: OType]():TT =
    (this.insts().dropRight(1).lastOption match {
      case Some(prev) => prev._2.apply(prev._1)
      case None => this.insts().head._1
    }).asInstanceOf[TT]

  def compose[TT <: OType](btype:TT):TT ={
    var a:this.type = this
    for (i <- btype.insts()) a = a.compose(i._1,i._2)
    a.asInstanceOf[TT]
  }

  def compose(inst:Inst):this.type //
  def compose[TT <: OType](t2:Obj,inst:Inst):TT = (t2 match {
    case _:Bool => bool(inst)
    case _:Int => int(inst)
    case _:Str => str(inst)
    case _:RecType[Obj,Obj] => rec(t2.asInstanceOf[RecType[Obj,Obj]],inst)
    case _:__ => __(this.insts().map(e => e._2) :+ inst:_*)
    case _:ObjType => obj(inst)
  }).asInstanceOf[TT]

  def obj(inst:Inst,q:TQ = this.q()):ObjType
  def int(inst:Inst,q:TQ = this.q()):IntType
  def bool(inst:Inst,q:TQ = this.q()):BoolType
  def str(inst:Inst,q:TQ = this.q()):StrType
  def rec[A <: Obj,B <: Obj](atype:RecType[A,B],inst:Inst,q:TQ = this.q()):RecType[A,B]

  final def <=[D <: OType](domainType:D):this.type = domainType.compose(this).q(this.q()).asInstanceOf[this.type]
  override def ==>[R <: Obj](rangeType:TType[R]):R = Processor.compiler[Type[T],R]()(this,InstUtil.resolveAnonymous(this,rangeType)).next().obj()

  override def quant():IntType = int(QOp())
  override def count():IntType = int(CountOp(),qOne)
  override def id():this.type = this.compose(IdOp())
  override def map[O <: Obj](other:O):O = this.compose(other,MapOp(other))
  override def model(model:StrValue):this.type = this.compose(ModelOp(model))
  override def from[O <: Obj](label:StrValue):O = this.compose(FromOp(label)).asInstanceOf[O]
  override def from[O <: Obj](label:StrValue,default:Obj):O = this.compose(FromOp(label,default)).asInstanceOf[O]
  override def as[O <: Obj](name:String):O = (InstUtil.nextInst(this) match {
    case Some(x) if x == AsOp(name) => this
    case _ => this.compose(AsOp(name))
  }).asInstanceOf[O]
  // pattern matching methods
  override def test(other:Obj):Boolean = other match {
    case argValue:OValue => TypeChecker.matchesTV(this,argValue)
    case argType:OType => TypeChecker.matchesTT(this,argType)
  }

  override def toString:String = Printable.format[OType](this)
  override def equals(other:Any):Boolean = other match {
    case atype:Type[T] => atype.insts().map(_._2) == this.insts().map(_._2) && this.range().toString == atype.range().toString
    case _ => false
  }

  // standard Java implementations
  override def hashCode():scala.Int = this.range().toString.hashCode // TODO: using toString()
  override def fold[O <: Obj](seed:(String,O))(atype:TType[O]):O = this.compose(asType(seed._2),FoldOp(seed,atype.asInstanceOf[OType]))
}
