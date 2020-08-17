package org.mmadt.processor.inst.rewrite

import org.mmadt.language.obj.`type`.__.id
import org.mmadt.language.obj.op.RewriteInstruction._
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{testSet, testing}
import org.mmadt.storage.StorageFactory.int

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class IdRewriteTest extends BaseInstTest(
  testSet("[rule:id] rewrite",
    testing(int, id.id.rule(rule_id), int),
    testing(int, id.plus(1).id.id.rule(rule_id), int.plus(1)),
    testing(int, id.id.plus(1).id.rule(rule_id), int.plus(1))))
