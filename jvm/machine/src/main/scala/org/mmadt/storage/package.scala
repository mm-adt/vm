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

  val TP:String = "tp"
  val KV:String = "kv"
  val TPKV:String = "tpkv"

  private lazy val mmlang:LanguageProvider = LanguageFactory.getLanguage("mmlang")

  def model(name:String):Model = {
    val source = Try[BufferedSource](Source.fromFile(getClass.getResource("/model/" + name + ".mm").getPath)).getOrElse(Source.fromFile("data/model/" + name + ".mm"))
    try {
      val rangeModel:Model = mmlang.parse(source.getLines().foldLeft(Tokens.blank)((x, y) => x + "\n" + y))
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
