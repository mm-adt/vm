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

package org.mmadt.language.mmlang

import org.mmadt.language.Tokens
import org.mmadt.language.model.Model
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.branch.ChooseOp
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.reduce.{CountOp, FoldOp}
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.op.traverser.{ExplainOp, FromOp, ToOp}
import org.mmadt.language.obj.value.strm.{IntStrm, StrStrm, Strm}
import org.mmadt.language.obj.value.{BoolValue, IntValue, StrValue, Value}
import org.mmadt.storage.StorageFactory._

import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object mmlangParser extends JavaTokenParsers {

  override val whiteSpace:Regex = """[\s\n]+""".r
  var model:Model = Model.id

  def parse[T <: Obj](input:String,_model:Model = Model.id):Iterator[T] ={
    if (null != _model) this.model = _model
    this.parseAll(expr | emptySpace,input.trim).map{
      case itty:Iterator[T] => itty
      case obj:T => Iterator(obj)
    }.get
  }

  def emptySpace[T]:Parser[Iterator[T]] = (Tokens.empty | whiteSpace) ^^ (_ => Iterator.empty)
  lazy val expr:Parser[Any] = evaluation | compilation | obj

  lazy val evaluation :Parser[Iterator[Obj]] = (strm | objValue) ~ (aType | anonType) ^^ (x => x._1 ===> x._2)
  lazy val compilation:Parser[Obj]           = objType ~ opt(objType) ^^ (x => x._2 match {
    case Some(atype) => (x._1 ==> this.model) (atype)
    case None => x._1 // TODO: clip domain and send domain through
  })

  lazy val canonicalType:Parser[Type[Obj]] = (Tokens.bool | Tokens.int | Tokens.str | Tokens.rec | name) ~ opt(quantifier) ^^ {
    case atype ~ q => q.foldRight(atype match {
      //case Tokens.obj => tobj
      case Tokens.bool => bool
      case Tokens.int => int
      case Tokens.str => str
      case Tokens.rec => rec
      case name:String => this.model.get(tint(name)).get.asInstanceOf[Type[Obj]]
    })((q,t) => t.q(q))
  }

  lazy val name   :Parser[String]    = "[a-zA-Z]*".r <~ ":"
  lazy val obj    :Parser[Obj]       = objValue | objType
  lazy val objType:Parser[Type[Obj]] = recType | aType | anonType

  lazy val aType   :Parser[Type[Obj]] = (opt(canonicalType <~ Tokens.:<=)) ~ canonicalType ~ rep[Inst](inst | stateAccess ^^ (x => ToOp(str(x._2)))) ^^ {
    case Some(range) ~ domain ~ insts => (range <= insts.foldLeft(domain)((x,y) => y(x).asInstanceOf[Type[Obj]]))
    case None ~ domain ~ insts => insts.foldLeft(domain)((x,y) => y(x).asInstanceOf[Type[Obj]])
  }
  lazy val recType :Parser[ORecType]  = opt(name) ~ (LBRACKET ~> repsep((obj <~ Tokens.:->) ~ obj,(COMMA | PIPE))) <~ RBRACKET ^^ (x => trec(x._1.getOrElse(Tokens.rec),x._2.map(o => (o._1,o._2)).toMap))
  lazy val anonType:Parser[__]        = rep1[Inst](inst | stateAccess ^^ (x => ToOp(str(x._2)))) ^^ (x => new __(x)) // anonymous type (instructions only -- no domain/range)

  lazy val stateAccess:Parser[Option[Type[Obj]] ~ String] = (opt(canonicalType) <~ LANGLE) ~ "[a-zA-z]*".r <~ RANGLE

  lazy val quantifier    :Parser[IntQ] = (LCURL ~> quantifierType <~ RCURL) | (LCURL ~> intValue ~ opt(COMMA ~> intValue) <~ RCURL) ^^ (x => (x._1,x._2.getOrElse(x._1)))
  lazy val quantifierType:Parser[IntQ] = (Tokens.q_star | Tokens.q_mark | Tokens.q_plus) ^^ {
    case Tokens.q_star => qStar
    case Tokens.q_mark => qMark
    case Tokens.q_plus => qPlus
  }

  lazy val objValue :Parser[Value[Obj]] = (boolValue | intValue | strValue | recValue) ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q)).getOrElse(x._1))
  lazy val boolValue:Parser[BoolValue]  = opt(name) ~ (Tokens.btrue | Tokens.bfalse) ^^ (x => vbool(x._1.getOrElse(Tokens.bool),x._2.toBoolean,qOne))
  lazy val intValue :Parser[IntValue]   = opt(name) ~ wholeNumber ^^ (x => vint(x._1.getOrElse(Tokens.int),qOne,x._2.toLong))
  lazy val strValue :Parser[StrValue]   = opt(name) ~ ("""'([^'\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*'""").r ^^ (x => vstr(x._1.getOrElse(Tokens.str),qOne,x._2.subSequence(1,x._2.length - 1).toString))
  lazy val recValue :Parser[ORecValue]  = opt(name) ~ (LBRACKET ~> repsep((objValue <~ Tokens.:->) ~ objValue,COMMA) <~ RBRACKET) ^^ (x => vrec(x._1.getOrElse(Tokens.rec),qOne,x._2.map(o => (o._1,o._2)).toMap))
  lazy val strm     :Parser[Strm[Obj]]  = intStrm | strStrm | recStrm
  lazy val intStrm  :Parser[IntStrm]    = (intValue <~ COMMA) ~ rep1sep(intValue,COMMA) ^^ (x => int(x._1,x._2.head,x._2.tail:_*))
  lazy val strStrm  :Parser[StrStrm]    = (strValue <~ COMMA) ~ rep1sep(strValue,COMMA) ^^ (x => str(x._1,x._2.head,x._2.tail:_*))
  lazy val recStrm  :Parser[ORecStrm]   = (recValue <~ COMMA) ~ rep1sep(recValue,COMMA) ^^ (x => vrec(x._1,x._2.head,x._2.tail:_*))

  lazy val instArg      :Parser[Obj]  = (stateAccess ^^ (x => x._1.getOrElse(int).from[Obj](str(x._2)))) | obj // TODO: hardcoded int for unspecified state type
  lazy val inst         :Parser[Inst] = chooseSugar | sugarlessInst | infixSugar
  lazy val infixSugar   :Parser[Inst] = (Tokens.plus_op | Tokens.mult_op | Tokens.gt_op | Tokens.eqs_op) ~ instArg ^^ (x => instMatrix(x._1,List(x._2)))
  lazy val chooseSugar  :Parser[Inst] = recType ^^ (x => ChooseOp(x))
  lazy val sugarlessInst:Parser[Inst] = LBRACKET ~> ("""[a-zA-Z][a-zA-Z0-9]*""".r <~ opt(COMMA)) ~ repsep(instArg,COMMA) <~ RBRACKET ^^ (x => instMatrix(x._1,x._2))

  private def instMatrix(op:String,args:List[Obj]):Inst ={ // TODO: move to language.obj.op.InstUtil (should be reused by all JVM-based mm-ADT languages)
    op match {
      case Tokens.and | Tokens.and_op => args.head match {
        case arg:BoolType => AndOp(arg)
        case arg:BoolValue => AndOp(arg)
        case arg:__ => AndOp(arg)
      }
      case Tokens.or | Tokens.or_op => args.head match {
        case arg:BoolType => OrOp(arg)
        case arg:BoolValue => OrOp(arg)
        case arg:__ => OrOp(arg)
      }
      case Tokens.plus | Tokens.plus_op => args.head match {
        case arg:IntValue => PlusOp(arg)
        case arg:IntType => PlusOp(arg)
        case arg:StrValue => PlusOp(arg)
        case arg:StrType => PlusOp(arg)
        case arg:ORecValue => PlusOp(arg)
        case arg:ORecType => PlusOp(arg)
        case arg:__ => PlusOp(arg)
      }
      case Tokens.mult | Tokens.mult_op => args.head match {
        case arg:IntValue => MultOp(arg)
        case arg:IntType => MultOp(arg)
        case arg:__ => MultOp(arg)
      }
      case Tokens.gt | Tokens.gt_op => args.head match {
        case arg:IntValue => GtOp(arg)
        case arg:IntType => GtOp(arg)
        case arg:StrValue => GtOp(arg)
        case arg:StrType => GtOp(arg)
        case arg:__ => GtOp(arg)
      }
      case Tokens.eqs | Tokens.eqs_op => args.head match {
        case arg:BoolValue => EqsOp(arg)
        case arg:BoolType => EqsOp(arg)
        case arg:IntValue => EqsOp(arg)
        case arg:IntType => EqsOp(arg)
        case arg:StrValue => EqsOp(arg)
        case arg:StrType => EqsOp(arg)
        case arg:ORecValue => EqsOp(arg)
        case arg:ORecType => EqsOp(arg)
        case arg:__ => EqsOp(arg)
      }
      case Tokens.is => args.head match {
        case arg:BoolValue => IsOp(arg)
        case arg:BoolType => IsOp(arg)
        case arg:__ => IsOp(arg)
      }
      case Tokens.get => args match {
        case List(key:Obj,typeHint:Type[Obj]) => GetOp(key,typeHint)
        case List(key:Obj) => GetOp(key)
      }
      case Tokens.map => args.head match {
        case arg:__ => MapOp(arg)
        case arg:Obj => MapOp(arg)
      }
      case Tokens.neg => NegOp()
      case Tokens.count => CountOp()
      case Tokens.explain => ExplainOp()
      case Tokens.put => PutOp(args.head,args.tail.head)
      case Tokens.from =>
        val label = args.head.asInstanceOf[StrValue]
        args.tail match {
          case Nil => FromOp(label)
          case obj:Obj => FromOp(label,obj)
        }
      case Tokens.fold => args.tail.tail.head match {
        case x:__ => FoldOp((args.head.asInstanceOf[StrValue].value(),args.tail.head),x)
        case x:Type[Obj] => FoldOp((args.head.asInstanceOf[StrValue].value(),args.tail.head),x)
      }
      case Tokens.to => ToOp(args.head.asInstanceOf[StrValue])
      case Tokens.choose => ChooseOp(args.head.asInstanceOf[RecType[Obj,Obj]])
      case Tokens.id => IdOp()
      case Tokens.q => QOp()
      case Tokens.zero => ZeroOp()
      case Tokens.one => OneOp()
    }
  }
}