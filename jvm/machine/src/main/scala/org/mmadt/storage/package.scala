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
import org.mmadt.language.{LanguageFactory, LanguageProvider, Tokens}

import scala.io.Source

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object storage {

  val TP3: String = "tp3"

  private lazy val mmlang: LanguageProvider = LanguageFactory.getLanguage("mmlang")
  val tp3: String = getClass.getResource("/model/tp3.mm").getPath


  def model(name: String): Type[Obj] = {
    val source = name match {
      case TP3 => Source.fromFile(tp3)
      case _ => throw new StorageException("Unknown predefined model: " + name)
    }
    try mmlang.parse(source.getLines().filter(x => !x.startsWith("//")).foldLeft(Tokens.empty)((x, y) => x + "\n" + y))
    finally source.close();
  }
}
