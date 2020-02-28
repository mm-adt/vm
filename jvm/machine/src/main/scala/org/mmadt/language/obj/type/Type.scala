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
import org.mmadt.language.obj.op.map.{IdOp, MapOp, QOp}
import org.mmadt.language.obj.op.model.{AsOp, ModelOp}
import org.mmadt.language.obj.op.reduce.{CountOp, FoldOp}
import org.mmadt.language.obj.op.traverser.{ExplainOp, FromOp}
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.processor.Processor
import org.mmadt.processor.obj.`type`.util.InstUtil
import org.mmadt.storage.obj._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Type[+T <: Obj] extends Obj
  with ExplainOp
  with ModelOp {
  this:T with Type[T] =>

  // type properties
  def insts():List[(OType,Inst)]
  def canonical():this.type = this.range().q(qOne)
  def range():this.type
  def domain[D <: Obj]():TypeObj[D] = (this.insts() match {
    case Nil => this
    case i:List[(OType,Inst)] => i.head._1.range()
  }).asInstanceOf[TypeObj[D]]

  // type manipulation functions
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

  // type specification and compilation
  final def <=[D <: Obj](domainType:Type[D]):this.type = domainType.compose(this).q(this.q()).asInstanceOf[this.type]
  override def ==>[R <: Obj](rangeType:Type[R]):R = Processor.compiler[Type[T],R]()(this,InstUtil.resolveAnonymous(this,rangeType)).next().obj()

  // type constructors via stream ring theory // TODO: figure out how to get this into [mult][plus] compositions
  def compose[P <: Obj](btype:TypeObj[P]):TypeObj[P] ={
    var a:Type[T] = this
    for (i <- btype.insts()) a = a.compose(i._1.asInstanceOf[TypeObj[T]],i._2)
    a.asInstanceOf[TypeObj[P]]
  }
  def compose(inst:Inst):this.type
  def compose[O <: Obj](t2:O,inst:Inst):TypeObj[O] = (t2 match {
    case _:Bool => bool(inst)
    case _:Int => int(inst)
    case _:Str => str(inst)
    case _:RecType[Obj,Obj] => rec(t2.asInstanceOf[RecType[Obj,Obj]],inst)
    case _:__ => __(this.insts().map(e => e._2) :+ inst:_*)
    case _:ObjType => obj(inst)
  }).asInstanceOf[TypeObj[O]]

  // type change during fluency // TODO: get rid of this
  def obj(inst:Inst,q:TQ = this.q()):ObjType
  def int(inst:Inst,q:TQ = this.q()):IntType
  def bool(inst:Inst,q:TQ = this.q()):BoolType
  def str(inst:Inst,q:TQ = this.q()):StrType
  def rec[A <: Obj,B <: Obj](atype:RecType[A,B],inst:Inst,q:TQ = this.q()):RecType[A,B]

  // obj-level operations
  override def as[O <: Obj](name:String):O = (InstUtil.nextInst(this) match {
    case Some(x) if x == AsOp(name) => this
    case _ => this.compose(AsOp(name))
  }).asInstanceOf[O]
  override def count():IntType = int(CountOp(),qOne)
  override def id():this.type = this.compose(IdOp())
  override def map[O <: Obj](other:O):O = this.compose(other,MapOp(other)).asInstanceOf[O]
  override def model(model:StrValue):this.type = this.compose(ModelOp(model))
  override def fold[O <: Obj](seed:(String,O))(atype:Type[O]):O = this.compose(asType(seed._2),FoldOp(seed,atype)).asInstanceOf[O]
  override def from[O <: Obj](label:StrValue):O = this.compose(FromOp(label)).asInstanceOf[O]
  override def from[O <: Obj](label:StrValue,default:Obj):O = this.compose(FromOp(label,default)).asInstanceOf[O]
  override def quant():IntType = int(QOp())

  // pattern matching methods
  override def test(other:Obj):Boolean = other match {
    case argValue:Value[_] => TypeChecker.matchesTV(this,argValue)
    case argType:Type[_] => TypeChecker.matchesTT(this,argType)
  }

  // standard Java implementations
  override def toString:String = Printable.format[OType](this)
  override def hashCode():scala.Int = this.name.hashCode ^ this.q().hashCode()
  override def equals(other:Any):Boolean = other match {
    case atype:__ => atype.toString.equals(this.toString) // TODO: get __ better aligned with Type
    case atype:Type[T] => atype.insts().map(_._2) == this.insts().map(_._2) && this.name == atype.name && this.q() == atype.q()
    case _ => false
  }
}
