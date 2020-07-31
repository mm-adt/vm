package org.mmadt.processor.inst.branch

import org.mmadt.language.obj.Int
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.TableDrivenPropertyChecks

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class BranchTest extends FunSuite with TableDrivenPropertyChecks {

  test("toString") {
    assertResult("(5;[plus,0];5;-<(int[plus,1],int[plus,2]);5;[plus,1];6;[plus,3])")(int(5).plus(0).branch[Int](int.plus(1) `,` int.plus(2)).plus(3).tracer().toStrm.values(0).toString)
    assertResult("(5;[plus,0];5;-<(int[plus,1],int[plus,2]);5;[plus,2];7;[plus,3])")(int(5).plus(0).branch[Int](int.plus(1) `,` int.plus(2)).plus(3).tracer().toStrm.values(1).toString)
    //
    assertResult("(5;-<(int[plus,1][plus,11],int[plus,2]);5;[plus,1];6;[plus,11];17;[plus,3])")(int(5).branch[Int](int.plus(1).plus(11) `,` int.plus(2)).plus(3).tracer().toStrm.values(0).toString)
    assertResult("(5;-<(int[plus,1][plus,11],int[plus,2]);5;[plus,2];7;[plus,3])")(int(5).branch[Int](int.plus(1).plus(11) `,` int.plus(2)).plus(3).tracer().toStrm.values(1).toString)
    //
    assertResult("(5;[plus,0];5;-<(int[plus,1][plus,11],int[plus,2]);5;[plus,1];6;[plus,11];17;[plus,3])")(int(5).plus(0).branch[Int](int.plus(1).plus(11) `,` int.plus(2)).plus(3).tracer().toStrm.values(0).toString)
    assertResult("(5;[plus,0];5;-<(int[plus,1][plus,11],int[plus,2]);5;[plus,2];7;[plus,3])")(int(5).plus(0).branch[Int](int.plus(1).plus(11) `,` int.plus(2)).plus(3).tracer().toStrm.values(1).toString)
  }

}
