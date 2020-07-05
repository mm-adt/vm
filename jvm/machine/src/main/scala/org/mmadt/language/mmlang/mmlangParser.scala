/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.mmlang

import org.mmadt.VmException
import org.mmadt.language.Tokens.{bool => _, int => _, lst => _, real => _, rec => _, str => _, _}
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.OpInstResolver
import org.mmadt.language.obj.op.branch.{MergeOp, RepeatOp, SplitOp}
import org.mmadt.language.obj.op.map.GetOp
import org.mmadt.language.obj.op.trace.{FromOp, ToOp}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{strm => _, _}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory
import org.mmadt.storage.StorageFactory.{strm => estrm, _}

import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmlangParser extends JavaTokenParsers {

  override val whiteSpace: Regex = """[\s]+(?://.*[\s]*)?""".r // includes support for single line "//" comments
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
  lazy val expr: Parser[Obj] = obj ~ opt(objType) ^^ (x => {
    x._2 match {
      case None => x._1 match {
        case _: Value[_] => x._1 // left hand value only, return it
        case _: Type[_] => x._1.domain ===> x._1 // left hand type only, compile it with it's domain
      }
      case Some(y) =>
        x._1 match {
          case _: Type[Obj] => x._1 ===> y
          case _: Value[Obj] => x._1 ===> (asType(x._1) ===> y)
        } // left and right hand, evaluate right type with left obj
    }
  })
  /*
  lazy val expr: Parser[Obj] = obj ^^ ({
  case x@(_: Value[_]) => x
  case x@(_: Type[_]) => (x.domain ===> x)
})
 */
  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////////////

  // mmlang's language structure
  lazy val obj: Parser[Obj] = objValue | objType | anonQuant

  // variable parsing
  lazy val symbolName: Regex = "[a-zA-Z]+".r
  lazy val varName: Parser[String] = ("^(?!(" + reservedTokens + s"))(${symbolName})").r <~ not(":")
  lazy val reservedTokens: String = List(Tokens.reservedTypes, Tokens.reservedOps).flatten.foldLeft(EMPTY)((a, b) => a + PIPE + b).drop(1)
  lazy val objName: Parser[String] = symbolName <~ ":"

  // poly parsing
  lazy val polySep: Parser[String] = Tokens.| | Tokens.`;` | Tokens.`,`
  lazy val lstObj: Parser[Lst[Obj]] = lstValue | lstType
  lazy val recObj: Parser[Rec[Obj, Obj]] = recValue | recType
  lazy val polyObj: Parser[Poly[Obj]] = lstObj | recObj
  def lstStruct(parser: Parser[Obj], until: String): Parser[LstTuple[Obj]] =
    (opt(parser) ~ polySep) ~ repsep(opt(parser), polySep) ^^ (x => lst(x._1._2, x._1._1.getOrElse(zeroObj) +: x._2.map(y => y.getOrElse(zeroObj)): _*).g) |
      parser <~ guard(until) ^^ (x => (Tokens.`,`, List(x))) |
      Tokens.empty ^^ (_ => (Tokens.`,`, List.empty[Obj]))
  def recStruct(parser: Parser[Obj]): Parser[RecTuple[Obj, Obj]] =
    ((opt((parser <~ Tokens.->) ~ parser) ~ polySep) ~ rep1sep(opt((parser <~ Tokens.->) ~ parser), polySep)) ^^
      (x => rec(g = (x._1._2, x._1._1.map(a => Map(a._1 -> a._2)).getOrElse(Map.empty[Obj, Obj]) ++ x._2.map(y => y.map(z => z._1 -> z._2).getOrElse(zeroObj -> zeroObj)).toMap[Obj, Obj])).g) |
      Tokens.-> ^^ (_ => (Tokens.`,`, Map.empty[Obj, Obj])) |
      (parser <~ Tokens.->) ~ parser ^^ (x => (Tokens.`,`, Map(x._1 -> x._2)))

  // type parsing
  lazy val objType: Parser[Obj] = aType | anonTypeSugar
  lazy val tobjType: Parser[Type[Obj]] = Tokens.obj ^^ (_ => StorageFactory.obj)
  lazy val anonType: Parser[__] = Tokens.anon ^^ (_ => __)
  lazy val boolType: Parser[BoolType] = Tokens.bool ^^ (_ => bool)
  lazy val intType: Parser[IntType] = Tokens.int ^^ (_ => int)
  lazy val realType: Parser[RealType] = Tokens.real ^^ (_ => real)
  lazy val strType: Parser[StrType] = Tokens.str ^^ (_ => str)
  lazy val lstType: Parser[LstType[Obj]] = (opt(objName) ~ (LROUND ~> lstStruct(obj, RROUND)) <~ RROUND ^^ (x => lst(name = x._1.getOrElse(Tokens.lst), g = x._2)) |
    Tokens.lst ^^ (_ => lst)).asInstanceOf[Parser[LstType[Obj]]]
  lazy val recType: Parser[RecType[Obj, Obj]] = (opt(objName) ~ (LROUND ~> recStruct(obj)) <~ RROUND ^^ (x => rec(name = x._1.getOrElse(Tokens.rec), g = x._2)) |
    Tokens.rec ^^ (_ => rec)).asInstanceOf[Parser[RecType[Obj, Obj]]]
  lazy val tokenType: Parser[__] = varName ^^ (x => __(x))

  lazy val cType: Parser[Type[Obj]] = (anonType | tobjType | boolType | realType | intType | strType | (not(inst) ~> (lstType | recType)) | tokenType) ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q)).getOrElse(x._1))
  lazy val dtype: Parser[Obj] = cType ~ rep[List[Inst[Obj, Obj]]](inst) ^^ (x => x._2.flatten.foldLeft(x._1.asInstanceOf[Obj])((x, y) => y.exec(x)))
  lazy val aType: Parser[Obj] = opt(cType <~ Tokens.:<=) ~ dtype ^^ {
    case Some(range) ~ domain => range <= domain
    case None ~ domain => domain
  }
  lazy val anonQuant: Parser[__] = quantifier ^^ (x => new __().q(x))
  lazy val anonTypeSugar: Parser[__] = rep1[List[Inst[Obj, Obj]]](inst) ^^ (x => x.flatten.foldLeft(new __())((a, b) => a.clone(via = (a, b))))

  // value parsing
  lazy val objValue: Parser[Value[Obj]] = (boolValue | realValue | intValue | strValue | lstValue | recValue) ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q)).getOrElse(x._1)) | strmValue
  lazy val boolValue: Parser[BoolValue] = opt(objName) ~ (Tokens.btrue | Tokens.bfalse) ^^ (x => vbool(x._1.getOrElse(Tokens.bool), x._2.toBoolean, qOne))
  lazy val intValue: Parser[IntValue] = opt(objName) ~ wholeNumber ^^ (x => vint(x._1.getOrElse(Tokens.int), x._2.toLong, qOne))
  lazy val realValue: Parser[RealValue] = opt(objName) ~ decimalNumber ^^ (x => vreal(x._1.getOrElse(Tokens.real), x._2.toDouble, qOne))
  lazy val strValue: Parser[StrValue] = opt(objName) ~ """'([^'\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*'""".r ^^ (x => vstr(x._1.getOrElse(Tokens.str), x._2.subSequence(1, x._2.length - 1).toString, qOne))
  lazy val lstValue: Parser[LstValue[Obj]] = (opt(objName) ~ (LROUND ~> lstStruct(objValue, RROUND) <~ RROUND) ^^
    (x => lst(name = x._1.getOrElse(Tokens.lst), g = (x._2._1, x._2._2)))).asInstanceOf[Parser[LstValue[Obj]]]
  lazy val recValue: Parser[RecValue[Obj, Obj]] = (opt(objName) ~ (LROUND ~> recStruct(objValue) <~ RROUND) ^^
    (x => rec(name = x._1.getOrElse(Tokens.rec), g = (x._2._1, x._2._2)))).asInstanceOf[Parser[RecValue[Obj, Obj]]]
  lazy val strmValue: Parser[Strm[Obj]] = LCURL ~> repsep(objValue, Tokens.`,`) <~ RCURL ^^ (x => estrm(x).asInstanceOf[Strm[Obj]])


  // instruction parsing
  lazy val inst: Parser[List[Inst[Obj, Obj]]] = (
    sugarlessInst | fromSugar | toSugar | splitSugar | repeatSugar | mergeSugar | infixSugar | getStrSugar | getIntSugar) ~ opt(quantifier) ^^
    (x => List(x._2.map(q => x._1.q(q)).getOrElse(x._1).asInstanceOf[Inst[Obj, Obj]])) | splitMergeSugar
  lazy val infixSugar: Parser[Inst[Obj, Obj]] = not(Tokens.:<=) ~> (
    Tokens.as_op | Tokens.plus_op | Tokens.mult_op | Tokens.gte_op | Tokens.juxt_op | Tokens.lte_op | Tokens.gt_op |
      Tokens.lt_op | Tokens.eqs_op | Tokens.and_op | Tokens.or_op | Tokens.given_op | Tokens.product_op | Tokens.sum_op |
      Tokens.combine_op | Tokens.a_op | Tokens.is | Tokens.not_op) ~ obj ^^ (x => OpInstResolver.resolve(x._1, List(x._2)))
  lazy val splitSugar: Parser[Inst[Obj, Obj]] = Tokens.split_op ~> polyObj ~ opt(quantifier) ^^ (x => x._2.map(q => SplitOp[Obj](x._1.q(q))).getOrElse(SplitOp(x._1)))
  lazy val mergeSugar: Parser[Inst[Obj, Obj]] = Tokens.merge_op ^^ (_ => MergeOp().asInstanceOf[Inst[Obj, Obj]])
  lazy val getStrSugar: Parser[Inst[Obj, Obj]] = Tokens.get_op ~> symbolName ^^ (x => GetOp[Obj, Obj](str(x)))
  lazy val getIntSugar: Parser[Inst[Obj, Obj]] = Tokens.get_op ~> wholeNumber ^^ (x => GetOp[Obj, Obj](int(java.lang.Long.valueOf(x))))
  lazy val toSugar: Parser[Inst[Obj, Obj]] = LANGLE ~> symbolName <~ RANGLE ^^ (x => ToOp(x))
  lazy val fromSugar: Parser[Inst[Obj, Obj]] = LANGLE ~> PERIOD ~ symbolName <~ RANGLE ^^ (x => FromOp(x._2))
  lazy val repeatSugar: Parser[Inst[Obj, Obj]] = (LROUND ~> obj <~ RROUND) ~ (Tokens.pow_op ~> LROUND ~> obj <~ RROUND) ^^ (x => RepeatOp(x._1, x._2))
  lazy val sugarlessInst: Parser[Inst[Obj, Obj]] = LBRACKET ~> (("(" + Tokens.reservedOps.foldLeft(EMPTY)((a, b) => a + PIPE + b).drop(1) + ")|(=[a-zA-Z]+)").r <~ opt(COMMA)) ~ repsep(obj, COMMA) <~ RBRACKET ^^ (x => OpInstResolver.resolve(x._1, x._2))
  lazy val splitMergeSugar: Parser[List[Inst[Obj, Obj]]] =
    ((LBRACKET ~> lstStruct(obj, RBRACKET)) <~ RBRACKET) ~ opt(quantifier) ^^ (x => List(SplitOp[Obj](lst(x._1._1, x._1._2.map {
      case avalue: Value[_] if avalue.root => __.is(__.eqs(avalue))
      case atype: Type[_] if atype.root => __.is(__.a(atype))
      case x => x
    }: _*)), x._2.map(q => MergeOp[Obj]().q(q)).getOrElse(MergeOp[Obj]()).asInstanceOf[Inst[Obj, Obj]])) |
      ((LBRACKET ~> recStruct(obj)) <~ RBRACKET) ~ opt(quantifier) ^^ (x => List(SplitOp(rec(g = (x._1._1, x._1._2.map(y => (y._1 match {
        case avalue: Value[_] if avalue.root => __.is(__.eqs(avalue))
        case atype: Type[_] if atype.root => __.is(__.a(atype))
        case x => x
      }) -> y._2)))), x._2.map(q => MergeOp[Obj]().q(q)).getOrElse(MergeOp[Obj]()).asInstanceOf[Inst[Obj, Obj]]))
  // quantifier parsing
  lazy val quantifier: Parser[IntQ] = (LCURL ~> quantifierType <~ RCURL) | (LCURL ~> intValue ~ opt(COMMA ~> intValue) <~ RCURL) ^^ (x => (x._1, x._2.getOrElse(x._1)))
  lazy val quantifierType: Parser[IntQ] = (Tokens.q_star | Tokens.q_mark | Tokens.q_plus) ^^ {
    case Tokens.q_star => qStar
    case Tokens.q_mark => qMark
    case Tokens.q_plus => qPlus
  }
}

object mmlangParser {
  def parse[O <: Obj](script: String): O = try {
    new mmlangParser().parse[O](script)
  } catch {
    case e: VmException => throw e
    case e: Exception =>
      e.printStackTrace()
      throw new LanguageException(e.getMessage)
  }
}