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