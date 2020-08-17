package org.mmadt.language

import org.mmadt.language.obj.Obj.stringToStr
import org.mmadt.language.obj.`type`.__
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
    testing(str, branch(branch(branch(branch(str.id() `,`) `,`) `,`) `,`), str, "str[[[[[id]]]]]"),
    testing(str, branch(branch(str.id() `,` str.id()) `,` str.id()), str.q(3) <= str.id().q(3), "str[[[id],[id]],[id]]"),
    testing(str, branch(str.id().q(2) `,` str.id().q(3)), str.q(5) <= str.id().q(5), "str[[id]{2},[id]{3}]"),
    testing(str, branch(str.id() `,` str.q(0)), str, "str[[id],{0}]"),
    testing(str, branch(str.id() `,` str.id().q(-1)), zeroObj, "str[[id],[id]{-1}]"),
    //comment("===monoid axioms"),
    testing(str, branch(branch(str.id() `;` str.id()) `;` str.id()), str, "str[[id];[id];[id]]"),
    testing(str, branch(str.id() `;` branch(str.id() `;` str.id())), str, "str[[id];[[id];[id]]]"),
    //comment("===ring axioms"),
    testing(str, branch(branch(str.id() `,` str.id()) `;` str.id()), str.q(2) <= str.id().q(2), "str[[[id],[id]];[id]]"),
    testing(str, branch(branch(str.id() `;` str.id()) `,` branch(str.id() `;` str.id())), str.q(2) <= str.id().q(2)), // TODO: "str[[[id];[id]],[[id];[id]]]"
    //comment("===ring theorems"),
    testing(str, branch(str.id().q(-1) `,` str.id().q(-1)), str.q(-2) <= str.id().q(-2), "str[[id]{-1},[id]{-1}]"),
    testing(str, branch(str.id() `,` str.id()).q(-1), str.q(-2) <= str.id().q(-2), "str[[id],[id]]{-1}"),
    testing(str, branch(str.id().q(-1) `,`).q(-1), str.id()), // TODO: "str[[id]{-1}]{-1}"
    testing(str, branch(str.id() `;` str.q(0)), zeroObj, "str[[id];{0}]"),
    testing(str, branch(str.q(0) `;` str.id()), zeroObj, "str[{0};[id]]"),
    testing(str, branch(str.id() `;` str.id().q(-1)), str.q(-1) <= str.id().q(-1), "str[[id];[id]{-1}]"),
    testing(str, branch(str.id().q(-1) `;` str.id()), str.q(-1) <= str.id().q(-1), "str[[id]{-1};[id]]"),
    testing(str, branch(str.id().q(-1) `;` str.id().q(-1)), str, "str[[id]{-1};[id]{-1}]"),
    testing(str, branch(str.id() `;` str.id()), str, "str[[id];[id]]"),
    // TODO: testing(str, branch(str.id() `;` str.id()).q(-1), str.q(-1) <= str.id().q(-1),"str[[id];[id]]{-1}"),
    //comment("===stream ring axioms"),
    testing(str, branch(str.id().q(2) `,` str.id().q(3)), str.id().q(5), "str[[id]{2},[id]{3}]"), // bulking
    testing(str.q(2), str.q(2).branch(str.id().q(3) `,`), str.q(6) <= str.q(2).id().q(3), "str{6}<=str{2}[[id]{3}]"), // applying
    testing(str.q(2), str.q(2).branch(str.id().q(3) `,` str.id().q(4)), str.q(14) <= str.q(2).id().q(7), "str{14}<=str{2}[[id]{3},[id]{4}]"), // splitting
    testing(str, branch(str.id().q(6) `,` str.id().q(8)), str.q(14) <= str.id().q(14), "str[[id]{6},[id]{8}]"), // splitting
    testing(str, branch(branch(str.id().q(2) `,`) `,` branch(str.id().q(3) `,`)), str.q(5) <= str.id().q(5), "str[[[id]{2}],[[id]{3}]]"), // merging
    testing(str, branch(str.id().q(2) `,` str.id().q(3)), str.q(5) <= str.id().q(5), "str[[id]{2},[id]{3}]"), // merging
    testing(str, branch(str.q(0) `,` str.id()), str, "str[{0},[id]]"), // removing
    testing(str, branch(str.id().q(0) `,` str.id()), str, "str[[id]{0},[id]]"), // removing
  ),
  testSet("stream ring axioms and theorems on rec",
    //comment("==abelian group axioms"),
    testing(str, branch(branch(branch(branch(id() -> id()) `,`) `,`) `,`), str, "str[[[[[id]->[id]]]]]"),
    // TODO testing(str, branch(branch(id() -> str.id() `_,` id() -> str.id()) `,` id() -> str.id()), str.q(3) <= str.id().q(3), "str[[[id]->[id],[id]->[id]],[id]->[id]]"),
    // TODO testing(str, branch(str.id().q(2) -> str.id().q(2) `_,` str.id().q(3) -> str.id().q(3)), str.q(5) <= str.id().q(5), "str[[id]{2}->[id]{2},[id]{3}->[id]{3}]"),
   testing(str, branch(str.id() -> str.id() `_,` str.q(0) -> str.q(0)), str, "str[[id]->[id],{0}->{0}]"),
    testing(str, branch(str.id() -> str.id() `_,` str.id().q(-1) -> str.id().q(-1)), zeroObj, "str[str[id]->str[id],str[id]{-1}->str[id]{-1}]"),
    testing(str, branch(id() -> id() `_,` id().q(-1) -> id().q(-1)), zeroObj, "str[[id]->[id],[id]{-1}->[id]{-1}]"),
    //comment("===monoid axioms"),
    // testing(str, branch(branch(str.id() -> str.id() `_;` str.id() -> str.id()) `;` str.id() -> str.id()), str, "str[[id]->[id];[id]->[id];[id]->[id]]"),
    // TODO testing(str, branch(str.id()->str.id() `_;` str -> branch(str.id() -> str.id() `_;` str.id() -> str.id())), str, "str[[id]->[id];[id]->[[id]->[id];[id]->[id]]]"),
  )) {

  assertResult(str)(engine.eval("str[[id]{-1};[id]{-1}]"))
  assertResult("a".q(2))(engine.eval("'a'[_,_]"))
  assertResult(str("a".q(2), "b".q(2)))(engine.eval("('a','b')>-[_,_]"))
  assertResult(str("a".q(10), "b".q(10)))(engine.eval("'z'{10}-<('a','b')>-"))
  assertResult(str("a".q(20), "b".q(20)))(engine.eval("'z'{10}-<('a','b')>-[id]{2}"))
  assertResult(str("a".q(20), "b".q(20)))(engine.eval("'z'{10}-<('a','b')>-[_,_]"))
  assertResult(str.q(10).id().q(2))(engine.eval("str{10}[str,str]"))
  assertResult(str.q(10).id().q(2))(engine.eval("str{10}[_,_]"))
  assertResult(zeroObj)(engine.eval("str[[id]{-1},[id]]"))
  assertResult(str.q(10).id().q(2).id().q(2))(engine.eval("str{10}[str,str][_,_]"))
  assertResult(__.id().q(2))(engine.eval("[_,_]"))
  assertResult(__)(engine.eval("[_[id]{2},_[id]{-1}]"))
  assertResult(str.id().q(-1))(engine.eval("str[[id];[id]]{-1}"))
  assertResult(str.id().q(15))(engine.eval("str[str[id]{5},str[id]{10}]"))
  assertResult(str.id().q(15))(engine.eval("str[[id]{5},[id]{10}]"))
  assertResult(zeroObj)(engine.eval("[str,str{-1}]"))
  assertResult(str)(engine.eval("str[str[id]{-1}]{-1}"))
  assertResult(zeroObj)(engine.eval("[_{0};str]"))
  assertResult(zeroObj)(engine.eval("[str;_{0}]"))
  assertResult("obj{0}")(engine.eval("[str;_{0}]").toString)
  assertResult("obj{0}")(engine.eval("[str,str{-1}]").toString)
  assertResult("obj{0}")(engine.eval("['a','a'{-1}]").toString)
  assertResult(zeroObj)(engine.eval("['a','a'{-1}]"))
  assertResult(str.q(2).plus(str.q(2)).q(5))(engine.eval("str{2}[str[plus,str]{5}]"))
  assertResult(str.id().q(-1))(engine.eval("[str[id]{-1};str]"))
  assertResult(str.id().q(-1))(engine.eval("[str;str[id]{-1}]"))
  assertResult(str.id().q(-1))(engine.eval("[str;str]{-1}"))
  // TODO: assertResult(__.q(2).id().q(7))(engine.eval("_{2}[str[id]{3},str[id]{4}]"))
  assertResult(__)(engine.eval("[[_;_];_]"))
  assertResult(str)(engine.eval("[[str;str];str]"))
  assertResult(__.id().q(3))(engine.eval("[_,[_,_]]"))
  assertResult(str.id().q(7))(engine.eval("str[str[id]{3},str[id]{4}]"))
  assertResult(str.q(5).id().q(7))(engine.eval("str{5}[str[id]{3},str[id]{4}]"))
  assertResult(str.id().q(2).id().q(7))(engine.eval("str[id]{2}[str[id]{3},str[id]{4}]"))
  assertResult(str.id().q(5))(engine.eval("str[[str[id]{2}],[str[id]{3}]]"))
  assertResult(str.id().q(5))(engine.eval("str[[[str[id]{2}]],[[str[id]{3}]]]"))
  assertResult(str.id().q(5))(engine.eval("str[str[id]{2},str[id]{3}]"))
  assertResult(str)(engine.eval("[str;_{1}]"))
  assertResult(str.id().q(3))(engine.eval("str[[id],[[id],[id]]]"))
  assertResult(str.id().q(3))(engine.eval("str[[[id],[id]],[id]]"))
  assertResult(str.id().q(9))(engine.eval("str[[id]{2},[[id]{3},[id]{4}]]"))
  assertResult(str.id().q(9))(engine.eval("str[[[id]{2},[id]{3}],[id]{4}]"))
  assertResult(zeroObj)(engine.eval("str[[[id]{2},[id]{-3}],[id]{1}]"))
  assertResult(str.id().q(3))(engine.eval("str[str,[str,str]]"))
  assertResult(str.id().q(3))(engine.eval("str[[str,str],str]"))
  assertResult(str.id().q(2))(engine.eval("str[str,str]"))
  assertResult(str.id().q(2))(engine.eval("str[str,str]"))
  assertResult(str.id().q(3))(engine.eval("str[str,str[id]{2}]"))
  assertResult(str.id().q(3))(engine.eval("str[str[id]{2},str]"))
  assertResult(str.branch(str `;` str.id().q(2)))(engine.eval("str[str;[str,str]]"))
  assertResult(str.branch(str.id().q(2) `;` str.q(2)))(engine.eval("str[[str,str];str]"))
  assertResult(str.id().q(2))(engine.eval("str[str[str;str],str[str;str]]"))
  assertResult(__.id().q(2))(engine.eval("[_[_;_],_[_;_]]"))
  assertResult(str.id().q(2))(engine.eval("str[_[str;str],_[str;str]]"))
  assertResult(__.id().q(2))(engine.eval("[_,_]"))
  assertResult(str.id().q(2))(engine.eval("[str;[str,str]]"))
}