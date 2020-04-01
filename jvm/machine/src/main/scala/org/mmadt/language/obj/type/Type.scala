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

import java.util.NoSuchElementException

import org.mmadt.language.obj.op.model.{ModelOp, NoOp}
import org.mmadt.language.obj.op.sideeffect.AddOp
import org.mmadt.language.obj.op.traverser.ExplainOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{eqQ, _}
import org.mmadt.language.{LanguageException, LanguageFactory, Tokens}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Type[+T <: Obj] extends Obj
  with AddOp
  with ModelOp
  with ExplainOp {
  this:T =>


  // slow refactor to the type data structure without List
  val via:(Type[Obj],Inst[Obj,T])
  def isCanonical:Boolean = null == via._1
  def isDerived:Boolean = !this.isCanonical

  // type properties
  lazy val insts    :List[(Type[Obj],Inst[Obj,Obj])] = if (this.isCanonical) Nil else this.via._1.insts :+ (this.via._1,this.via._2)
  lazy val canonical:this.type                       = this.range.q(qOne)
  lazy val range    :this.type                       = (this match {
    case _:BoolType => tbool(this.name,this.q)
    case _:RealType => treal(this.name,this.q)
    case _:IntType => tint(this.name,this.q)
    case _:StrType => tstr(this.name,this.q)
    case arec:RecType[_,_] => trec(this.name,arec.value(),arec.q)
    case _:__ => this
    case _:ObjType => tobj(this.name,this.q)
  }).asInstanceOf[this.type]

  def domain[D <: Obj]():Type[D] = if (this.isCanonical) this.asInstanceOf[Type[D]] else this.via._1.domain[D]()

  // type manipulation functions
  def linvert():this.type ={
    ((this.insts.tail match {
      case Nil => this.range
      case i => i.foldLeft[Traverser[Obj]](Traverser.standard(i.head._1.range))((btype,inst) => inst._2.apply(btype)).obj()
    }) match {
      case vv:Value[_] => vv.start()
      case x => x
    }).asInstanceOf[this.type]
  }
  def rinvert[R <: Type[Obj]]():R = if (this.isCanonical) throw new NoSuchElementException else this.via._1.asInstanceOf[R] // TODO: ctypes just return themselves?

  // type specification and compilation
  final def <=[D <: Obj](domainType:Type[D]):this.type ={
    LanguageException.testDomainRange(this,domainType)
    Some(domainType).filter(x => x.isCanonical).map(_.id()).getOrElse(domainType).compose(this).q(this.q).asInstanceOf[this.type]
  }

  // type constructors via stream ring theory // TODO: figure out how to get this into [mult][plus] compositions
  def compose[R <: Type[Obj]](btype:R):R = btype match {
    case anon:__ => anon(this)
    case atype:Type[Obj] => atype.insts.seq.foldLeft[Traverser[Obj]](Traverser.standard(this))((b,a) => a._2(b)).obj().asInstanceOf[R].compose(btype.range,NoOp())
  }
  def compose(inst:Inst[_,_]):this.type = this.compose(this,inst)
  def compose[R <: Obj](nextObj:R,inst:Inst[_,_]):R ={
    val newInst:DomainInst[Obj] = (if (inst.op().equals(Tokens.noop)) this.via else (this,inst.asInstanceOf[Inst[Obj,Obj]]))
    (nextObj match {
      case _:Bool => tbool(nextObj.name,multQ(this,inst),newInst.asInstanceOf[DomainInst[Bool]])
      case _:Real => treal(nextObj.name,multQ(this,inst),newInst.asInstanceOf[DomainInst[Real]])
      case _:Int => tint(nextObj.name,multQ(this,inst),newInst.asInstanceOf[DomainInst[Int]])
      case _:Str => tstr(nextObj.name,multQ(this,inst),newInst.asInstanceOf[DomainInst[Str]])
      case arec:Rec[_,_] => trec(arec.name,arec.value().asInstanceOf[Map[Obj,Obj]],multQ(this,inst),newInst.asInstanceOf[DomainInst[Rec[Obj,Obj]]])
      case _:__ => new __(if (inst.op().equals(Tokens.noop)) this.insts else this.insts ::: List((this,inst.asInstanceOf[Inst[Obj,Obj]])))
      case _ => tobj(nextObj.name,multQ(this,inst),newInst)
    }).asInstanceOf[R]
  }

  // obj-level operations
  override def add[O <: Obj](obj:O):O = this.compose(asType(obj).asInstanceOf[O],AddOp(obj))

  def named(_name:String):this.type = (this match {
    case abool:BoolType => tbool(_name,abool.q,abool.via)
    case areal:RealType => treal(_name,areal.q,areal.via)
    case aint:IntType => tint(_name,aint.q,aint.via)
    case astr:StrType => tstr(_name,astr.q,astr.via)
    case arec:RecType[Obj,Obj] => trec(_name,arec.value(),arec.q,arec.via)
    case _:__ => this
    case _:ObjType => tobj(_name,this.q,this.via)
  }).asInstanceOf[this.type]

  // pattern matching methods
  override def test(other:Obj):Boolean = other match {
    case argValue:Value[_] => TypeChecker.matchesTV(this,argValue)
    case argType:Type[_] => TypeChecker.matchesTT(this,argType)
  }

  // standard Java implementations
  override def toString:String = LanguageFactory.printType(this)
  override lazy val hashCode:scala.Int = this.name.hashCode ^ this.q.hashCode() ^ this.insts.hashCode()
  override def equals(other:Any):Boolean = other match {
    case atype:Type[_] =>
      if (this.isCanonical)
        atype.isCanonical && atype.name.equals(this.name) && eqQ(atype,this)
      else if (this.isInstanceOf[__] && atype.isInstanceOf[__]) // TODO: have it work generically with types (and make it recurssive)
      atype.isDerived && atype.name.equals(this.name) && eqQ(atype,this) && this.insts.map(x => x._2) == atype.insts.map(x => x._2)
      else
      atype.isDerived && atype.name.equals(this.name) && eqQ(atype,this) && (this.via._2 == atype.via._2 && this.via._1 == atype.via._1)
    case _ => false
  }
}

object Type {
  @scala.annotation.tailrec
  def createInstList(list:List[(Type[Obj],Inst[Obj,Obj])],atype:Type[Obj]):List[(Type[Obj],Inst[Obj,Obj])] ={
    if (atype.isCanonical) list else createInstList(List((atype.range,atype.insts.last._2)) ::: list,atype.insts.last._1)
  }

  def nextInst(atype:Type[_]):Option[Inst[Obj,Obj]] = atype.insts match {
    case Nil => None
    case x => Some(x.head._2)
  }

  def resolve[R <: Obj](objA:Obj,objB:R):R = objB match {
    case x:__ => x(objA)
    case _ => objB
  }
}
