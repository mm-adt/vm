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

package org.mmadt

import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.{LanguageFactory, LanguageProvider, Tokens}

import scala.io.{BufferedSource, Source}
import scala.util.Try

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object storage {

  val DATA_DIR = "data/model/"
  val MODEL_DIR = "/model/"
  val EXAMPLES = "examples/"
  val MM_FILE = ".mm"

  private lazy val mmlang:LanguageProvider = LanguageFactory.getLanguage("mmlang")

  def model(token:Symbol):Model = this.model(token.name)
  def model(name:String):Model = {
    val source:BufferedSource = Try[BufferedSource](Source.fromFile(getClass.getResource(MODEL_DIR + name + MM_FILE).getPath))
      .orElse(Try(Source.fromFile(DATA_DIR + name + MM_FILE)))
      .orElse(Try(Source.fromFile(MODEL_DIR + EXAMPLES + name + MM_FILE)))
      .getOrElse(Source.fromFile(DATA_DIR + EXAMPLES + name + MM_FILE))
    try {
      val rangeModel:Model = mmlang.parse(source.getLines().foldLeft(Tokens.blank)((x, y) => x + y + "\n"))
      model(rangeModel)
    }
    finally source.close();
  }

  def model(rangeModel:Model):Model = {
    if (rangeModel == rangeModel.domainObj) return rangeModel
    val domainModel:Model = {
      if (__.isToken(rangeModel.domainObj)) this.model(rangeModel.domainObj.name)
      else rangeModel.domainObj.asInstanceOf[Model]
    }
    rangeModel.merging(domainModel)
  }
}
