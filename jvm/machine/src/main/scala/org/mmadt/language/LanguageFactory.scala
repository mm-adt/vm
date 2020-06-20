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

package org.mmadt.language

import java.util.ServiceLoader

import org.mmadt.language.mmlang.mmlangPrinter
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm

import scala.collection.JavaConverters

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait LanguageFactory {
  def printValue(value: Value[Obj]): String
  def printType(atype: Type[Obj]): String
  def printInst(inst: Inst[_, _]): String
  def printStrm(strm: Strm[Obj]): String
}

object LanguageFactory {
  ///////PROVIDERS///////
  private lazy val providers: ServiceLoader[LanguageProvider] = ServiceLoader.load(classOf[LanguageProvider])
  def getLanguage(name: String): LanguageProvider = JavaConverters.asScalaIterator(providers.iterator()).filter(x => x.name().equals(name)).next()
  ///////////////////////
  def printValue(value: Value[Obj])(implicit f: LanguageFactory): String = f.printValue(value)
  def printType(atype: Type[Obj])(implicit f: LanguageFactory): String = f.printType(atype)
  def printInst(inst: Inst[_, _])(implicit f: LanguageFactory): String = f.printInst(inst)
  def printStrm(strm: Strm[Obj])(implicit f: LanguageFactory): String = f.printStrm(strm)
  implicit val mmlangFactory: LanguageFactory = new LanguageFactory {
    override def printValue(value: Value[Obj]): String = mmlangPrinter.valueString(value)
    override def printType(atype: Type[Obj]): String = mmlangPrinter.typeString(atype)
    override def printInst(inst: Inst[_, _]): String = mmlangPrinter.instString(inst)
    override def printStrm(strm: Strm[Obj]): String = mmlangPrinter.strmString(strm)
  }
}