package org.mmadt.processor.inst.map

import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.scalatest.FunSuite
import org.scalatest.prop.TableDrivenPropertyChecks

class TracerInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("trace test") {
    println(new mmlangScriptEngineFactory().getScriptEngine.eval("3,4,5,7[plus,2][plus,3][trace,(_;_)]"))
  }

}
