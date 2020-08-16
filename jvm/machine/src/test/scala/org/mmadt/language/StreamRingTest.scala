package org.mmadt.language

import org.mmadt.language.obj.`type`.__._
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{testSet, testing}
import org.mmadt.storage.StorageFactory.{str, zeroObj}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class StreamRingTest extends BaseInstTest(
  testSet("stream ring axioms and theorems on lst",
    //comment("==abelian group axioms"),
    testing(str, branch(branch(str.id() `,` str.id()) `,` str.id()), str.q(3) <= str.id().q(3)),
    testing(str, branch(str.id().q(2) `,` str.id().q(3)), str.q(5) <= str.id().q(5)),
    testing(str, branch(str.id() `,` zeroObj), str),
    testing(str, branch(str.id() `,` str.id().q(-1)), zeroObj, compile = false),
    //comment("===monoid axioms"),
    testing(str, branch(branch(str.id() `;` str.id()) `;` str.id()), str),
    testing(str, branch(str.id() `;` branch(str.id() `;` str.id())), str),
    //comment("===ring axioms"),
    testing(str, branch(branch(str.id() `,` str.id()) `;` str.id()), str.q(2) <= str.id().q(2)),
    testing(str, branch(branch(str.id() `;` str.id()) `,` branch(str.id() `;` str.id())), str.q(2) <= str.id().q(2)),
    //comment("===ring theorems"),
    testing(str, branch(str.id().q(-1) `,` str.id().q(-1)), str.q(-2) <= str.id().q(-2)),
    testing(str, branch(str.id() `,` str.id()).q(-1), str.q(-2) <= str.id().q(-2)),
    //testing(str, branch(str.id().q(-1)`,`).q(-1),str,compile = false),
    //testing(str, branch(str.id()`;` str.q(0)), zeroObj,compile = false),
    //testing(str, branch(str.q(0)`;` str.id()), zeroObj,compile = false),
    testing(str, branch(str.id() `;` str.id().q(-1)), str.q(-1) <= str.id().q(-1)),
    testing(str, branch(str.id().q(-1) `;` str.id()), str.q(-1) <= str.id().q(-1)),
    testing(str, branch(str.id().q(-1) `;` str.id().q(-1)), str),
    //testing(str, branch(str.id() `;` str.id()).q(-1), str.q(-1) <= str.id().q(-1),compile = false),
  ),
  testSet("stream ring axioms and theorems on rec"))