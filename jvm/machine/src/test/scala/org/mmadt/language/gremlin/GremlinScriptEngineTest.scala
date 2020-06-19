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

package org.mmadt.language.gremlin
import org.mmadt.language.LanguageFactory
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.obj.`type`.__
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

class GremlinScriptEngineTest extends FunSuite {

  lazy val engine: mmADTScriptEngine = LanguageFactory.getLanguage("gremlin").getEngine.get()

  test("empty space parsing") {
    assertResult(__.get("V").is(__.get("id").eqs(int(1))).get("outE").is(__.get("label").eqs("knows")) `,`)(engine.eval("V(1).outE('knows')"))
    assertResult(__.get("outE").is(__.get("label").eqs("knows")) `,`)(engine.eval("outE('knows')"))
    assertResult(__.get("outE").is(__.get("label").eqs("knows")).get("inV") `,`)(engine.eval("outE('knows').inV()"))
    assertResult(__.get("outE").is(__.get("label").eqs("knows")).get("inV") `,`)(engine.eval("out('knows')"))
  }
}