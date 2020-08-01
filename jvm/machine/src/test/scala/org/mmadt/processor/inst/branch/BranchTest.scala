package org.mmadt.processor.inst.branch

import org.mmadt.TestUtil._
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.map.PlusOp
import org.mmadt.language.obj.{Int, Obj}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class BranchTest extends FunSuite with TableDrivenPropertyChecks {

  private val engine: mmADTScriptEngine = new mmlangScriptEngineFactory().getScriptEngine

  test("[branch] ,-poly") {
    val starts: TableFor3[Obj, Obj, Obj] =
      new TableFor3[Obj, Obj, Obj](("start", "type", "end"),
        (int(1), int.plus(0).branch(__.plus(1) `,` __.plus(2)), int(2, 3)),
        (int(1), int.plus(0).branch(__.plus(1).plus(1) `,` __.plus(2)), int(3).q(2)),
        (int(1, 2), int.q(2).plus(0).branch(__.plus(1).plus(1) `,` __.plus(2)), int(int(3).q(2), int(4).q(2))),
        (int(1), int.plus(0).branch[Int](__.plus(1) `,` __.plus(2)).path(), strm(List(lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2))), lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(2), 3)))))),
      )
    forEvery(starts) { (start, atype, end) => {
      println(stringify(end))
      List(
        engine.eval(s"${stringify(start)} => ${atype}"),
        start.compute(atype),
        start ===> atype,
        start `=>` atype
      ).foreach(example => {
        assertResult(end)(example)
      })
    }
    }
  }

  test("[branch] path testing") {
    assertResult("(5;[plus,0];5;[plus,1];6;[plus,3];9)")(int(5).plus(0).branch[Int](int.plus(1) `,` int.plus(2)).plus(3).path().toStrm.values(0).toString)
    assertResult("(5;[plus,0];5;[plus,2];7;[plus,3];10)")(int(5).plus(0).branch[Int](int.plus(1) `,` int.plus(2)).plus(3).path().toStrm.values(1).toString)
    //
    assertResult("(5;[plus,1];6;[plus,11];17;[plus,3];20)")(int(5).branch[Int](int.plus(1).plus(11) `,` int.plus(2)).plus(3).path().toStrm.values(0).toString)
    assertResult("(5;[plus,2];7;[plus,3];10)")(int(5).branch[Int](int.plus(1).plus(11) `,` int.plus(2)).plus(3).path().toStrm.values(1).toString)
    //
    assertResult("(5;[plus,0];5;[plus,1];6;[plus,11];17;[plus,3];20)")(int(5).plus(0).branch[Int](int.plus(1).plus(11) `,` int.plus(2)).plus(3).path().toStrm.values(0).toString)
    assertResult("(5;[plus,0];5;[plus,2];7;[plus,3];10)")(int(5).plus(0).branch[Int](int.plus(1).plus(11) `,` int.plus(2)).plus(3).path().toStrm.values(1).toString)
  }

}
