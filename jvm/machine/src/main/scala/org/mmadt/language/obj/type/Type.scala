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

import org.mmadt.language.model.Model
import org.mmadt.language.obj._
import org.mmadt.language.obj.op.map.{IdOp, MapOp, QOp}
import org.mmadt.language.obj.op.model.{AsOp, ModelOp}
import org.mmadt.language.obj.op.reduce.{CountOp, FoldOp}
import org.mmadt.language.obj.op.traverser.{ExplainOp, FromOp}
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.language.{LanguageFactory, Tokens}
import org.mmadt.processor.Processor
import org.mmadt.processor.obj.`type`.util.InstUtil
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.mmkv.mmkvOp

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Type[+T <: Obj] extends Obj
  with ExplainOp
  with ModelOp {
  this:T =>

  // value constructor
  // def apply(values:Obj):Value[Obj]

  // type properties
  val insts:List[(Type[Obj],Inst)]
  lazy val canonical:this.type = this.range.q(qOne)
  lazy val range    :this.type = (this match {
    case _:BoolType => tbool(this.name,this.q(),Nil)
    case _:IntType => tint(this.name,this.q(),Nil)
    case _:StrType => tstr(this.name,this.q(),Nil)
    case arec:RecType[_,_] => trec(arec.name,arec.value(),arec.q(),Nil)
    case _:__ => this
    case _:ObjType => tobj(this.name,this.q(),Nil)
  }).asInstanceOf[this.type]

  def domain[D <: Obj]():Type[D] = (this.insts match {
    case Nil => this
    case i:List[(Type[Obj],Inst)] => i.head._1.range
  }).asInstanceOf[Type[D]]

  // type manipulation functions
  def linvert():this.type ={
    ((this.insts.tail match {
      case Nil => this.range
      case i => i.foldLeft[Obj](i.head._1.range)((btype,inst) => inst._2.apply(btype))
    }) match {
      case vv:Value[_] => vv.start()
      case x => x
    }).asInstanceOf[this.type]
  }
  def rinvert[R <: Type[Obj]]():R =
    (this.insts.dropRight(1).lastOption match {
      case Some(prev) => prev._2.apply(prev._1)
      case None => this.insts.head._1
    }).asInstanceOf[R]

  // type specification and compilation
  final def <=[D <: Obj](domainType:Type[D]):this.type = domainType.compose(this).q(this.q()).asInstanceOf[this.type]
  override def ==>[R <: Obj](rangeType:Type[R]):R = Processor.compiler()(this,InstUtil.resolveAnonymous(this,rangeType)).next().obj()
  def ==>[R <: Obj](model:Model)(rangeType:Type[R]):R = Processor.compiler(model)(this,InstUtil.resolveAnonymous(this,rangeType)).next().obj()

  // type constructors via stream ring theory // TODO: figure out how to get this into [mult][plus] compositions
  def compose[R <: Type[Obj]](btype:R):R ={
    btype match {
      case anon:__ => anon(this)
      case atype:Type[Obj] => atype.insts.seq.foldLeft(this.asInstanceOf[Type[Obj]])((b,a) => a._2(b).asInstanceOf[Type[Obj]]).asInstanceOf[R]
    }
  }
  def compose(inst:Inst):this.type = this.compose(this,inst).asInstanceOf[this.type]
  def compose[R <: Obj](nextObj:R,inst:Inst):OType[R] ={
    val newInsts = if (inst.op().equals(Tokens.noop)) this.insts else this.insts ::: List((this,inst))
    //val newInsts = this.insts ::: List((this,inst))
    (nextObj match {
      case _:Bool => tbool(nextObj.name,multQ(this,inst),newInsts)
      case _:Int => tint(nextObj.name,multQ(this,inst),newInsts)
      case _:Str => tstr(nextObj.name,multQ(this,inst),newInsts)
      case arec:Rec[_,_] => trec(arec.name,arec.value().asInstanceOf[Map[Obj,Obj]],multQ(this,inst),newInsts)
      case _:__ => new __(newInsts)
      case _ => tobj(nextObj.name,multQ(this,inst),newInsts)
    }).asInstanceOf[OType[R]]
  }

  // obj-level operations
  override def as[O <: Obj](name:String):O = (InstUtil.nextInst(this) match {
    case Some(x) if x == AsOp(name) => this
    case _ => this.compose(AsOp(name))
  }).asInstanceOf[O]
  override def count():IntType = this.compose(tint(),CountOp()).q(qOne)
  override def id():this.type = this.compose(IdOp())
  override def map[O <: Obj](other:O):O = this.compose(asType(other),MapOp(other)).asInstanceOf[O]
  override def model(model:StrValue):this.type = this.compose(ModelOp(model))
  override def fold[O <: Obj](seed:(String,O))(atype:Type[O]):O = this.compose(asType(seed._2),FoldOp(seed,atype)).asInstanceOf[O]
  override def from[O <: Obj](label:StrValue):O = this.compose(FromOp(label)).asInstanceOf[O]
  override def from[O <: Obj](label:StrValue,default:Obj):O = this.compose(FromOp(label,default)).asInstanceOf[O]
  override def quant():IntType = this.compose(tint(),QOp())

  def named(_name:String):this.type = (this match {
    case _:BoolType => tbool(_name,this.q(),Nil)
    case _:IntType => tint(_name,this.q(),Nil)
    case _:StrType => tstr(_name,this.q(),Nil)
    case arec:RecType[_,_] => trec(_name,arec.value(),arec.q(),Nil)
    case _:__ => this
    case _:ObjType => tobj(_name,this.q(),Nil)
  }).asInstanceOf[this.type]

  // pattern matching methods
  override def test(other:Obj):Boolean = other match {
    case argValue:Value[Obj] => TypeChecker.matchesTV(this,argValue)
    case argType:Type[Obj] => TypeChecker.matchesTT(this,argType)
  }

  // standard Java implementations
  override def toString:String = LanguageFactory.printType(this)
  override def hashCode():scala.Int = this.name.hashCode ^ this.q().hashCode() ^ this.insts.hashCode()
  override def equals(other:Any):Boolean = other match {
    case atype:__ => atype.toString.equals(this.toString) // TODO: get __ better aligned with Type
    case atype:Type[Obj] => this.name == atype.name && this.q() == atype.q() && atype.insts.map(x => (x._1.name,x._2)) == this.insts.map(x => (x._1.name,x._2))
    case _ => false
  }

  /////////
  override def mmkv(file:StrValue):RecType[Str,Obj] = this.compose(trec(name = "mmkv",value = Map[Str,Obj](str("k") -> int,str("v") -> str)),mmkvOp(file)).q(*)
}
