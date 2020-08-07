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
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.{LanguageFactory, LanguageProvider, Tokens}

import scala.io.Source

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object storage {

  val TP3: String = "tp3"
  val KV: String = "kv"

  private lazy val mmlang: LanguageProvider = LanguageFactory.getLanguage("mmlang")

  def model(name: String): Model = {
    val source = Source.fromFile(getClass.getResource("/model/" + name + ".mm").getPath)
    try mmlang.parse(source.getLines().filter(x => !x.startsWith("//")).foldLeft(Tokens.empty)((x, y) => x + "\n" + y))
    finally source.close();
  }

  def functor(from: String, to: String): Type[Obj] = {
    val source = Source.fromFile(getClass.getResource("/model/functor/" + (to + "_" + from) + ".mm").getFile)
    try mmlang.parse(source.getLines().filter(x => !x.startsWith("//")).foldLeft(Tokens.empty)((x, y) => x + "\n" + y))
    finally source.close();
  }
}
