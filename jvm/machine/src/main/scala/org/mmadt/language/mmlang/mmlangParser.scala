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

import org.mmadt.VmException
import org.mmadt.language.model.Model
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.OpInstResolver
import org.mmadt.language.obj.op.branch.{MergeOp, RepeatOp, SplitOp}
import org.mmadt.language.obj.op.map.GetOp
import org.mmadt.language.obj.op.trace.{FromOp, ToOp}
import org.mmadt.language.obj.value.{strm => _, _}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory
import org.mmadt.storage.StorageFactory.{strm => estrm, _}

import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmlangParser(val model: Model) extends JavaTokenParsers {

  override val whiteSpace: Regex = """[\s\n]+""".r
  override def decimalNumber: Parser[String] = """-?\d+\.\d+""".r

  // all mm-ADT languages must be able to accept a string representation of an expression in the language and return an Obj
  private def parse[O <: Obj](input: String): O = {
    this.parseAll(expr | emptySpace, input.trim) match {
      case Success(result, _) => result.asInstanceOf[O]
      case NoSuccess(y) => throw LanguageException.parseError(
        y._1,
        y._2.source.toString,
        y._2.pos.line.asInstanceOf[java.lang.Integer],
        y._2.pos.column.asInstanceOf[java.lang.Integer])
    }
  }
  private def emptySpace[O <: Obj]: Parser[O] = (Tokens.empty | whiteSpace) ^^ (_ => estrm[O])

  // specific to mmlang execution
  lazy val expr: Parser[Obj] = (strm | obj) ~ opt(objType) ^^ (x => {
    x._2 match {
      case None => x._1 match {
        case _: Value[_] => x._1 // left hand value only, return it
        case _: Type[_] => x._1.domain ===> x._1 // left hand type only, compile it with it's domain
      }
      case Some(y) => x._1 ===> y // left and right hand, evaluate right type with left obj
    }
  })
  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////////////

  // mmlang's language structure
  lazy val obj: Parser[Obj] = varGet | objValue | objType | polyObj

  // variable parsing
  lazy val symbolName: Regex = "[a-zA-Z]+".r
  lazy val varName: Parser[String] = ("^(?!(" + instOp + s"))(${symbolName})").r <~ not(":")
  lazy val varGet: Parser[Type[Obj]] = varName ~ rep(inst) ^^
    (x => this.model.get(tobj(x._1)).getOrElse(__.apply(List[Inst[_, _]](FromOp(x._1)) ++ x._2.flatten)))

  // poly parsing
  lazy val polyObj: Parser[Lst[Obj]] = lstObj ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q).asInstanceOf[Lst[Obj]]).getOrElse(x._1))
  lazy val polySep: Parser[String] = Tokens.| | Tokens.`;` | Tokens.`,`
  lazy val lstObj: Parser[Lst[Obj]] = (LBRACKET ~> lstStruct <~ RBRACKET) ^^ (x => lst(x._1, x._2: _*))
  lazy val lstStruct: Parser[LstTuple[Obj]] = (opt(obj) ~ polySep) ~ rep1sep(opt(obj), polySep) ^^ (x => lst(x._1._2, x._1._1.getOrElse(zeroObj) +: x._2.map(y => y.getOrElse(zeroObj)): _*).g)
  //lazy val polyRecObj: Parser[Poly[Obj]] = (LBRACKET ~> ((symbolName <~ Tokens.:->) ~ obj ~ polySep) ~ rep1sep((symbolName <~ Tokens.:->) ~ obj, polySep) <~ RBRACKET) ^^
  //  (x => poly[Obj](x._1._2).clone(ground = (x._1._2, x._1._1._2 +: x._2.map(y => y._2), x._1._1._1 +: x._2.map(y => y._1))))

  // type parsing
  lazy val objType: Parser[Obj] = dType | anonTypeSugar
  lazy val tobjType: Parser[Type[Obj]] = Tokens.obj ^^ (_ => StorageFactory.obj)
  lazy val anonType: Parser[__] = Tokens.anon ^^ (_ => __)
  lazy val boolType: Parser[BoolType] = Tokens.bool ^^ (_ => bool)
  lazy val intType: Parser[IntType] = Tokens.int ^^ (_ => int)
  lazy val realType: Parser[RealType] = Tokens.real ^^ (_ => real)
  lazy val strType: Parser[StrType] = Tokens.str ^^ (_ => str)
  lazy val lstType: Parser[Lst[Obj]] = Tokens.lst ~> opt(lstObj) ^^ (x => x.getOrElse(lst(Tokens.`;`)))
  lazy val recType: Parser[ORecType] = (Tokens.rec ~> opt(COLON ~> recStruct)) ^^ (x => trec(gmap = x.map(_._2).getOrElse(Map.empty[Obj, Obj])))
  lazy val recTypeNoPrefix: Parser[ORecType] = recStruct ^^ (x => trec(gmap = x._2))
  lazy val recStruct: Parser[RecTuple[Obj, Obj]] = (LBRACKET ~> repsep((obj <~ Tokens.->) ~ obj, polySep) <~ RBRACKET) ^^ (x => (Tokens.`;`, x.map(o => (o._1, o._2)).toMap))


  lazy val cType: Parser[Type[Obj]] = (anonType | tobjType | boolType | realType | intType | strType | recType | lstType) ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q)).getOrElse(x._1))
  lazy val dType: Parser[Obj] = opt(cType <~ Tokens.:<=) ~ cType ~ rep[List[Inst[Obj, Obj]]](inst) ^^ {
    case Some(range) ~ domain ~ insts => (range <= insts.flatten.foldLeft(domain.asInstanceOf[Obj])((x, y) => y.exec(x)))
    case None ~ domain ~ insts => insts.flatten.foldLeft(domain.asInstanceOf[Obj])((x, y) => y.exec(x))
  }

  lazy val anonTypeSugar: Parser[__] = rep1[List[Inst[Obj, Obj]]](inst) ^^ (x => __(x.flatten))
  lazy val instOp: String = Tokens.reserved.foldRight(EMPTY)((a, b) => b + PIPE + a).drop(1)

  // value parsing
  lazy val valueType: Parser[String] = symbolName <~ ":"
  lazy val objValue: Parser[Value[Obj]] = (boolValue | realValue | intValue | strValue | recValue | polyObj) ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q)).getOrElse(x._1))
  lazy val boolValue: Parser[BoolValue] = opt(valueType) ~ (Tokens.btrue | Tokens.bfalse) ^^ (x => vbool(x._1.getOrElse(Tokens.bool), x._2.toBoolean, qOne))
  lazy val intValue: Parser[IntValue] = opt(valueType) ~ wholeNumber ^^ (x => vint(x._1.getOrElse(Tokens.int), x._2.toLong, qOne))
  lazy val realValue: Parser[RealValue] = opt(valueType) ~ decimalNumber ^^ (x => vreal(x._1.getOrElse(Tokens.real), x._2.toDouble, qOne))
  lazy val strValue: Parser[StrValue] = opt(valueType) ~ """'([^'\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*'""".r ^^ (x => vstr(x._1.getOrElse(Tokens.str), x._2.subSequence(1, x._2.length - 1).toString, qOne))
  lazy val recValue: Parser[ORecValue] = opt(valueType) ~ (LBRACKET ~> repsep((objValue <~ Tokens.->) ~ objValue, polySep) <~ RBRACKET) ^^ (x => vrec(x._1.getOrElse(Tokens.rec), x._2.map(o => (o._1, o._2)).toMap, qOne))
  lazy val strm: Parser[Obj] = (objValue <~ COMMA) ~ rep1sep(objValue, COMMA) ^^ (x => estrm((List(x._1) :+ x._2.head) ++ x._2.tail))

  // instruction parsing
  lazy val inst: Parser[List[Inst[Obj, Obj]]] = manyInstSugar | (
    sugarlessInst | fromSugar | toSugar | splitSugar | repeatSugar |
      mergeSugar | infixSugar | getStrSugar | getIntSugar) ~ opt(quantifier) ^^
    (x => List(x._2.map(q => x._1.q(q)).getOrElse(x._1).asInstanceOf[Inst[Obj, Obj]]))
  lazy val manyInstSugar: Parser[List[Inst[Obj, Obj]]] = splitMergeSugar | splitMergeSuperSugar ~ opt(quantifier) ^^ (x => x._2.map(q => List(x._1.head, x._1.tail.head.q(q))).getOrElse(x._1))
  lazy val infixSugar: Parser[Inst[Obj, Obj]] = (
    Tokens.plus_op | Tokens.mult_op | Tokens.gte_op |
      Tokens.lte_op | Tokens.gt_op | Tokens.lt_op | Tokens.eqs_op |
      Tokens.and_op | /*Tokens.or_op |*/ Tokens.given_op |
      Tokens.combine_op | Tokens.a_op | Tokens.is | Tokens.append_op) ~ obj ^^
    (x => OpInstResolver.resolve(x._1, List(x._2)))
  lazy val splitMergeSugar: Parser[List[Inst[Obj, Obj]]] = LANGLE ~> polyObj <~ RANGLE ^^ (x => List(SplitOp(x), MergeOp[Obj]().asInstanceOf[Inst[Obj, Obj]]))
  lazy val splitMergeSuperSugar: Parser[List[Inst[Obj, Obj]]] = LANGLE ~> lstStruct <~ RANGLE ^^ (x => List(SplitOp(lst(x._1, x._2: _*)), MergeOp[Obj]().asInstanceOf[Inst[Obj, Obj]]))
  lazy val splitSugar: Parser[Inst[Obj, Obj]] = Tokens.split_op ~> (polyObj | recTypeNoPrefix | recType | recValue) ^^ (x => SplitOp(x.asInstanceOf[Poly[Obj]]))
  lazy val mergeSugar: Parser[Inst[Obj, Obj]] = Tokens.merge_op ^^ (_ => MergeOp[Obj]().asInstanceOf[Inst[Obj, Obj]])
  lazy val getStrSugar: Parser[Inst[Obj, Obj]] = Tokens.get_op ~> symbolName ^^ (x => GetOp[Obj, Obj](str(x)))
  lazy val getIntSugar: Parser[Inst[Obj, Obj]] = Tokens.get_op ~> wholeNumber ^^ (x => GetOp[Obj, Obj](int(java.lang.Long.valueOf(x))))
  lazy val toSugar: Parser[Inst[Obj, Obj]] = LANGLE ~> symbolName <~ RANGLE ^^ (x => ToOp(x))
  // lazy val traceSugar: Parser[Inst[Obj, Obj]] = LROUND ~> lstObj <~ RROUND ^^ (x => TraceOp(x))
  lazy val fromSugar: Parser[Inst[Obj, Obj]] = LANGLE ~> PERIOD ~ symbolName <~ RANGLE ^^ (x => FromOp(x._2))
  lazy val repeatSugar: Parser[Inst[Obj, Obj]] = (LROUND ~> obj <~ RROUND) ~ (Tokens.pow_op ~> intValue) ^^ (x => RepeatOp(x._1, x._2))
  lazy val sugarlessInst: Parser[Inst[Obj, Obj]] = LBRACKET ~> ("""=?[a-z]+""".r <~ opt(COMMA)) ~ repsep(obj, COMMA) <~ RBRACKET ^^ (x => OpInstResolver.resolve(x._1, x._2))

  // quantifier parsing
  lazy val quantifier: Parser[IntQ] = (LCURL ~> quantifierType <~ RCURL) | (LCURL ~> intValue ~ opt(COMMA ~> intValue) <~ RCURL) ^^ (x => (x._1, x._2.getOrElse(x._1)))
  lazy val quantifierType: Parser[IntQ] = (Tokens.q_star | Tokens.q_mark | Tokens.q_plus) ^^ {
    case Tokens.q_star => qStar
    case Tokens.q_mark => qMark
    case Tokens.q_plus => qPlus
  }
}

object mmlangParser {
  def parse[O <: Obj](script: String, model: Model): O = try {
    new mmlangParser(model).parse[O](script)
  } catch {
    case e: VmException => throw e
    case e: Exception => {
      e.printStackTrace()
      throw new LanguageException(e.getMessage)
    }
  }
}