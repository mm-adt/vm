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
import org.mmadt.language.obj.Lst.LstTuple
import org.mmadt.language.obj.Obj.IntQ
import org.mmadt.language.obj.Rec.RecTuple
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.OpInstResolver
import org.mmadt.language.obj.op.branch._
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
class mmlangParser extends JavaTokenParsers {

  override val whiteSpace: Regex = """[\s]*(?://.*[\s]*)?(/\*.*?\*/)?[\s]*""".r // includes support for single line // and /**/ comments
  override def decimalNumber: Parser[String] = """-?\d+\.\d+""".r

  // all mm-ADT languages must be able to accept a string representation of an expression in the language and return an Obj
  private def parse[O <: Obj](input: String, prefix: Option[Type[Obj]] = None): O = {
    this.parseAll(expr(prefix) | emptySpace, input.trim) match {
      case Success(result, _) => Some(result.asInstanceOf[O]).filter(_.alive).getOrElse(estrm[O])
      case NoSuccess(y) => throw LanguageException.parseError(
        y._1,
        y._2.source.toString,
        y._2.pos.line.asInstanceOf[java.lang.Integer],
        y._2.pos.column.asInstanceOf[java.lang.Integer])
    }
  }

  private def emptySpace[O <: Obj]: Parser[O] = (Tokens.blank | whiteSpace) ^^ (_ => estrm[O])

  // specific to mmlang execution (auto-compiling)
  def expr(prefix: Option[Type[Obj]] = None): Parser[Obj] = opt(startValue | objValue) ~ opt(Tokens.:=>) ~ opt(obj) ~ (opt(Tokens.:=>) ~> repsep(obj, Tokens.:=>)) ^^ {
    case Some(source) ~ _ ~ Some(target: Type[Obj]) ~ aobjs =>
      aobjs.foldLeft(compile(prefix, asType(source), target) match {
        case avalue: Value[Obj] => avalue
        case atype: Type[Obj] => source ==>[Obj] atype
      })((a, b) => a `=>` b)
    case Some(source) ~ _ ~ Some(target: Value[Obj]) ~ aobjs => aobjs.foldLeft((source ==> target).asInstanceOf[Obj])((a, b) => a `=>` b)
    case None ~ None ~ Some(target) ~ aobjs => target.domainObj ==> compile(prefix, target.domain, aobjs.foldLeft(target)((a, b) => a `=>` b))
    case Some(source) ~ None ~ None ~ _ => source.asInstanceOf[Obj]
    case None ~ None ~ None ~ _ => zeroObj
  }
  def compile(prefix: Option[Type[Obj]], source: Obj, target: Obj): Obj =
    if (target.isInstanceOf[Value[Obj]]) target
    else prefix.map(pre => source ==> pre).getOrElse(source) ==> target
  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////////////

  // mmlang's language structure
  lazy val obj: Parser[Obj] = objValue | objType | anonQuant

  // variable parsing
  lazy val symbolName: Regex = "[a-zA-Z]+[a-zA-Z_0-9]*".r
  lazy val varName: Parser[String] = ("^(?!(" + reservedTokens + s"))(${symbolName})").r <~ not(":")
  lazy val reservedTokens: String = List(Tokens.reservedTypes, Tokens.reservedOps).flatten.foldLeft(EMPTY)((a, b) => a + PIPE + b).drop(1)
  lazy val objName: Parser[String] = symbolName <~ ":"

  // poly parsing
  lazy val polySep: Parser[String] = Tokens.| | Tokens.`;` | Tokens.`,` | Tokens.juxt_op
  lazy val lstObj: Parser[Lst[Obj]] = lstValue | lstType
  lazy val recObj: Parser[Rec[Obj, Obj]] = recValue | recType
  lazy val polyObj: Parser[Poly[Obj]] = lstObj | recObj

  def lstStruct(parser: Parser[Obj]): Parser[LstTuple[Obj]] =
    (opt(parser) ~ polySep) ~ repsep(opt(parser), polySep) ^^ (x => (Some(x._1._2).map(y => if (y == juxt_op) Tokens.`;` else y).get, x._1._1.getOrElse(zeroObj) +: x._2.map(y => y.getOrElse(zeroObj)))) |
      parser ^^ (x => (Tokens.`,`, List(x))) |
      Tokens.blank ^^ (_ => (Tokens.`,`, List.empty[Obj]))

  def recStruct(parser: Parser[Obj]): Parser[RecTuple[Obj, Obj]] =
    ((opt((parser <~ Tokens.->) ~ parser) ~ polySep) ~ rep1sep(opt((parser <~ Tokens.->) ~ parser), polySep)) ^^
      (x => (Some(x._1._2).map(y => if (y == juxt_op) Tokens.`;` else y).get, x._1._1.map(a => List(a._1 -> a._2)).getOrElse(List.empty) ++ x._2.map(y => y.map(z => z._1 -> z._2).getOrElse(zeroObj -> zeroObj)).toMap[Obj, Obj])) |
      Tokens.-> ^^ (_ => (Tokens.`,`, List.empty)) |
      (parser <~ Tokens.->) ~ parser ^^ (x => (Tokens.`,`, List(x._1 -> x._2)))

  // type parsing
  lazy val objType: Parser[Obj] = aType | anonTypeSugar | objZero | objOne
  lazy val objZero: Parser[Obj] = LCURL ~> "0" <~ RCURL ^^ (_ => zeroObj)
  lazy val objOne: Parser[Obj] = LCURL ~> "1" <~ RCURL ^^ (_ => tobj())
  lazy val tobjType: Parser[Type[Obj]] = Tokens.obj ^^ (_ => StorageFactory.obj)
  lazy val anonType: Parser[__] = Tokens.anon ^^ (_ => __)
  lazy val boolType: Parser[BoolType] = Tokens.bool ^^ (_ => bool)
  lazy val intType: Parser[IntType] = Tokens.int ^^ (_ => int)
  lazy val realType: Parser[RealType] = Tokens.real ^^ (_ => real)
  lazy val strType: Parser[StrType] = Tokens.str ^^ (_ => str)
  lazy val lstType: Parser[Lst[Obj]] = opt(objName) ~ (LROUND ~> lstStruct(obj)) <~ RROUND ^^ (x => lst(name = x._1.getOrElse(Tokens.lst), g = x._2)) | Tokens.lst ^^ (_ => lst)
  lazy val recType: Parser[Rec[Obj, Obj]] = opt(objName) ~ (LROUND ~> recStruct(obj)) <~ RROUND ^^ (x => rec(name = x._1.getOrElse(Tokens.rec), g = x._2)) | Tokens.rec ^^ (_ => rec)
  lazy val tokenType: Parser[__] = varName ^^ (x => __(x))

  lazy val cType: Parser[Obj] = (anonType | tobjType | boolType | realType | intType | strType | (not(inst) ~> (lstType | recType)) | tokenType) ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q)).getOrElse(x._1))
  lazy val dtype: Parser[Obj] = cType ~ rep[Inst[Obj, Obj]](inst) ^^ (x => x._2.foldLeft(x._1.asInstanceOf[Obj])((x, y) => y.exec(x))) | anonTypeSugar
  lazy val aType: Parser[Obj] = opt(cType <~ Tokens.:<=) ~ dtype ^^ {
    case Some(range) ~ domain => range <= domain
    case None ~ domain => domain
  }
  lazy val anonQuant: Parser[__] = quantifier ^^ (x => new __().q(x))
  lazy val anonTypeSugar: Parser[__] = rep1[Inst[Obj, Obj]](inst) ^^ (x => x.foldLeft(new __())((a, b) => a.clone(via = (a, b))))

  // value parsing
  lazy val objValue: Parser[Obj] = (boolValue | realValue | intValue | strValue | lstValue | recValue) ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q)).getOrElse(x._1))
  lazy val boolValue: Parser[BoolValue] = opt(objName) ~ (Tokens.btrue | Tokens.bfalse) ^^ (x => bool(x._2.toBoolean, x._1.getOrElse(Tokens.bool), qOne))
  lazy val intValue: Parser[IntValue] = opt(objName) ~ wholeNumber ^^ (x => int(x._2.toLong, x._1.getOrElse(Tokens.int), qOne))
  lazy val realValue: Parser[RealValue] = opt(objName) ~ decimalNumber ^^ (x => real(x._2.toDouble, x._1.getOrElse(Tokens.real), qOne))
  lazy val strValue: Parser[StrValue] = opt(objName) ~ """'([^'\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*'""".r ^^ (x => str(g = x._2.subSequence(1, x._2.length - 1).toString, name = x._1.getOrElse(Tokens.str), qOne))
  lazy val lstValue: Parser[Lst[Obj]] = (opt(objName) ~ (LROUND ~> lstStruct(objValue) <~ RROUND) ^^ (x => lst(name = x._1.getOrElse(Tokens.lst), g = (x._2._1, x._2._2))))
  lazy val recValue: Parser[Rec[Obj, Obj]] = (opt(objName) ~ (LROUND ~> recStruct(objValue) <~ RROUND) ^^ (x => rec(name = x._1.getOrElse(Tokens.rec), g = (x._2._1, x._2._2))))
  lazy val startValue: Parser[Obj] = ((LBRACKET ~> rep1sep(objValue, COMMA)) <~ RBRACKET) ~ opt(quantifier) ^^ (x => estrm(x._1.filter(_.alive).map(y => y.q(q => multQ(q, x._2.getOrElse(qOne)))): _*))

  // instruction parsing
  lazy val inst: Parser[Inst[Obj, Obj]] = (sugarlessInst | fromSugar | toSugar | splitSugar | combineSugar | repeatSugar | mergeSugar | infixSugar | getStrSugar | getIntSugar | branchSugar) ~ opt(quantifier) ^^
    (x => x._2.map(q => x._1.q(q)).getOrElse(x._1).asInstanceOf[Inst[Obj, Obj]])
  lazy val infixSugar: Parser[Inst[Obj, Obj]] = not(Tokens.:<=) ~> (
    Tokens.as_op | Tokens.plus_op | Tokens.mult_op | Tokens.gte_op | Tokens.lte_op | Tokens.gt_op |
      Tokens.lt_op | Tokens.eqs_op | Tokens.and_op | Tokens.or_op | Tokens.product_op | Tokens.sum_op |
      Tokens.is_a_op | Tokens.is | Tokens.not_op) ~ opt(quantifier) ~ obj ^^ (x => x._1._2.map(q => OpInstResolver.resolve[Obj, Obj](x._1._1, List(x._2)).hardQ(q)).getOrElse(OpInstResolver.resolve(x._1._1, List(x._2))))
  lazy val combineSugar: Parser[Inst[Obj, Obj]] = Tokens.combine_op ~> polyObj ~ opt(quantifier) ^^ (x => x._2.map(q => CombineOp(x._1.q(q))).getOrElse(CombineOp(x._1)))
  lazy val splitSugar: Parser[Inst[Obj, Obj]] = Tokens.split_op ~> polyObj ~ opt(quantifier) ^^ (x => x._2.map(q => SplitOp(x._1.q(q))).getOrElse(SplitOp(x._1)).asInstanceOf[Inst[Obj, Obj]])
  lazy val mergeSugar: Parser[Inst[Obj, Obj]] = Tokens.merge_op ^^ (_ => MergeOp().asInstanceOf[Inst[Obj, Obj]])
  lazy val getStrSugar: Parser[Inst[Obj, Obj]] = Tokens.get_op ~> symbolName ^^ (x => GetOp[Obj, Obj](str(x)))
  lazy val getIntSugar: Parser[Inst[Obj, Obj]] = Tokens.get_op ~> wholeNumber ^^ (x => GetOp[Obj, Obj](int(java.lang.Long.valueOf(x))))
  lazy val toSugar: Parser[Inst[Obj, Obj]] = LANGLE ~> symbolName <~ RANGLE ^^ (x => ToOp(x))
  lazy val fromSugar: Parser[Inst[Obj, Obj]] = LANGLE ~> PERIOD ~ symbolName <~ RANGLE ^^ (x => FromOp(x._2))
  lazy val repeatSugar: Parser[Inst[Obj, Obj]] = (LROUND ~> obj <~ RROUND) ~ (Tokens.pow_op ~> LROUND ~> obj <~ RROUND) ^^ (x => RepeatOp(x._1, x._2))
  lazy val sugarlessInst: Parser[Inst[Obj, Obj]] = LBRACKET ~> ((LROUND + Tokens.reservedOps.foldLeft(EMPTY)((a, b) => a + PIPE + b).drop(1) + RROUND + "|(=[a-zA-Z]+)").r <~ opt(COMMA)) ~ repsep(obj, COMMA) <~ RBRACKET ^^ (x => OpInstResolver.resolve(x._1, x._2))
  lazy val branchSugar: Parser[Inst[Obj, Obj]] = ((LBRACKET ~> lstStruct(obj)) <~ RBRACKET) ^^ (x => BranchOp(lst(g = (x._1, x._2)))) | ((LBRACKET ~> recStruct(obj)) <~ RBRACKET) ^^ (x => BranchOp(rec(g = (x._1, x._2))))
  // quantifier parsing
  lazy val quantifier: Parser[IntQ] = (LCURL ~> quantifierType <~ RCURL) | (LCURL ~> intValue ~ opt(COMMA ~> intValue) <~ RCURL) ^^ (x => (x._1, x._2.getOrElse(x._1)))
  lazy val quantifierType: Parser[IntQ] = (Tokens.q_star | Tokens.q_mark | Tokens.q_plus) ^^ {
    case Tokens.q_star => qStar
    case Tokens.q_mark => qMark
    case Tokens.q_plus => qPlus
  }
}

object mmlangParser {
  def parse[O <: Obj](script: String, prefix: Option[Type[Obj]] = None): O = try {
    new mmlangParser().parse[O](script, prefix)
  } catch {
    case e: VmException => throw e
    case e: Exception =>
      e.printStackTrace()
      throw new LanguageException(e.getMessage)
  }
}