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
  private def evaluate(start: Obj, middle: Obj, end: Obj): Unit = {
    //println(start.compute(middle).toStrm.values)
    //println(end.toStrm.values)
    List(
      engine.eval(s"${stringify(start)} => ${middle}"),
      start.compute(middle),
      start ===> middle,
      start `=>` middle
    ).foreach(example => {
      assertResult(end)(example)
    })
  }

  test("[branch] ,-lst") {
    val starts: TableFor3[Obj, Obj, Obj] =
      new TableFor3[Obj, Obj, Obj](("start", "middle", "end"),
        (int.q(10), __.plus(0).branch[Int](__.plus(1) `,` __.plus(2)).is(__.gt(10)), int.q(0, 20) <= int.q(10).plus(0).branch[Int](__.plus(1) `,` __.plus(2)).is(__.gt(10))),
        (int(1), int.plus(0).branch(__.plus(1) `,` __.plus(2)), int(2, 3)),
        (int(1), int.plus(0).branch(__.plus(1) `,` __.plus(2) `,` int.plus(3)), int(2, 3, 4)),
        (int(1), int.plus(0).branch(__.plus(1).q(2) `,` __.plus(2).q(3) `,` int.plus(3).q(4)), int(int(2).q(2), int(3).q(3), int(4).q(4))),
        (int(1), int.plus(0).branch(__.plus(1).plus(1) `,` __.plus(2)), int(3).q(2)),
        (int(1, 2), int.q(2).plus(0).branch(__.plus(1).plus(1) `,` __.plus(2)), int(int(3).q(2), int(4).q(2))),
        (int(1), int.plus(0).branch[Int](__.plus(1) `,` __.plus(2)).path(), strm(List(lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2))), lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(2), 3)))))),
        /*(int(1, 2), int.q(2).plus(0).branch[Int](__.plus(1) `,` __.plus(2)).path(), strm(List(
          lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2))),
          lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(2), int(3).q(2)))), // TODO: <-- q(2)
          // lst(g = (";", List[Obj](int(2), PlusOp(0), 2, PlusOp(1), 3))), // TODO: WHEN USING PATH, UNIQUNESS BASED ON OBJ GRAPH PATH!
          lst(g = (";", List[Obj](int(2), PlusOp(0), 2, PlusOp(2), 4)))))),*/
      )
    forEvery(starts) { (start, middle, end) => evaluate(start, middle, end)
    }
  }

  test("[branch] ;-lst") {
    val starts: TableFor3[Obj, Obj, Obj] =
      new TableFor3[Obj, Obj, Obj](("start", "middle", "end"),
        (int.q(10), __.plus(0).branch[Int](__.plus(1) `;` __.plus(2)).is(__.gt(10)), int.q(0, 10) <= int.q(10).plus(0).branch[Int](__.plus(1) `;` __.plus(2)).is(__.gt(10))),
        (int(1), int.plus(0).branch(__.plus(1) `;` __.plus(2)), int(4)),
        (int(1), int.plus(0).branch(__.plus(1) `;` __.plus(2) `;` int.plus(3)), int(7)),
        (int(1), int.plus(0).branch(__.plus(1).q(2) `;` __.plus(2).q(3) `;` int.plus(3).q(4)), int(7).q(24)),
        (int(1), int.plus(0).branch(__.plus(1).plus(1) `;` __.plus(2)), int(5)),
        (int(1, 2), int.q(2).plus(0).branch(__.plus(1).plus(1) `;` __.plus(2)), int(5, 6)),
        //(int(1), int.plus(0).branch[Int](__.plus(1) `;` __.plus(2)).path(), lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2, PlusOp(2),4)))), // TODO: TYPES IN A STRM
        (int(1, 2), int.q(2).plus(0).branch[Int](__.plus(1) `;` __.plus(2)).path(), strm(List(
          lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2, PlusOp(2), 4))),
          lst(g = (";", List[Obj](int(2), PlusOp(0), 2, PlusOp(1), 3, PlusOp(2), 5)))))),
      )
    forEvery(starts) { (start, middle, end) => evaluate(start, middle, end)
    }
  }

  test("[branch] |-lst") {
    val starts: TableFor3[Obj, Obj, Obj] =
      new TableFor3[Obj, Obj, Obj](("start", "middle", "end"),
        (int.q(10), __.plus(0).branch[Int](__.plus(1) | __.plus(2)).is(__.gt(10)), int.q(0, 10) <= int.q(10).plus(0).branch[Int](__.plus(1) | __.plus(2)).is(__.gt(10))),
        (int(1), int.plus(0).branch(__.plus(1) | __.plus(2)), int(2)),
        (int(1), int.plus(0).branch(__.plus(1).q(0) | __.plus(2) | int.plus(3)), int(3)),
        (int(1), int.plus(0).branch(__.plus(1).q(0) | __.plus(2).q(0) | int.plus(3)), int(4)),
        // (int(1), int.plus(0).branch(__.plus(1).q(2) | __.plus(2).q(3) | int.plus(3).q(4)), int(2).q(2)), // TODO: VALUE QUANTIFIERS ARE NOT RANGED
        (int(1), int.plus(0).branch(__.plus(1).plus(1) | __.plus(3)), int(3)),
        (int(1), int.plus(0).branch(__.plus(1).q(0).plus(1) | __.plus(3)), int(4)),
        (int(1), int.plus(0).branch(__.plus(1).plus(1).q(0) | __.plus(3)), int(4)),
        (int(1), int.plus(0).branch(__.plus(1).plus(1).q(0) | __.plus(3).q(0)), zeroObj),
        (int(1, 2), int.q(2).plus(0).branch(__.plus(1).plus(1) | __.plus(2)), int(3, 4)),
        //(int(1), int.plus(0).branch[Int](__.plus(1) | __.plus(2)).path(), lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2)))), // TODO: TYPES IN A STRM
        (int(1, 2), int.q(2).plus(0).branch[Int](__.plus(1) | __.plus(2)).path(), strm(List(
          lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2))),
          lst(g = (";", List[Obj](int(2), PlusOp(0), 2, PlusOp(1), 3)))))),
      )
    forEvery(starts) { (start, middle, end) => evaluate(start, middle, end)
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
