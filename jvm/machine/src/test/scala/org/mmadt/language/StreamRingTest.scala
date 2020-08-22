package org.mmadt.language

import javax.script.ScriptContext
import org.mmadt.language.obj.Obj.stringToStr
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.trace.ModelOp._
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.BaseInstTest.engine
import org.mmadt.storage.StorageFactory.{str, zeroObj}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class StreamRingTest extends BaseInstTest {

  test("old examples to relocate") {
    engine.eval(":")
    engine.getContext.getBindings(ScriptContext.ENGINE_SCOPE).put(Tokens.::, __.model(MM))
    assertResult(str)(engine.eval("str[[id]{-1};[id]{-1}]"))
    assertResult("a".q(2))(engine.eval("'a'[_,_]"))
    //    assertResult(str("a".q(2), "b".q(2)))(engine.eval("('a','b')>-[_,_]"))
    assertResult(str("a".q(10), "b".q(10)))(engine.eval("'z'{10}-<('a','b')>-"))
    assertResult(str("a".q(20), "b".q(20)))(engine.eval("'z'{10}-<('a','b')>-[id]{2}"))
    assertResult(str("a".q(20), "b".q(20)))(engine.eval("'z'{10}-<('a','b')>-[_,_]"))
    assertResult(str.q(10).id.q(2))(engine.eval("str{10}[str,str]"))
    assertResult(str.q(10).id.q(2))(engine.eval("str{10}[_,_]"))
    assertResult(zeroObj)(engine.eval("str[[id]{-1},[id]]"))
    assertResult(str.q(10).id.q(4))(engine.eval("str{10}[str,str][_,_]"))
    assertResult(__.id.q(2))(engine.eval("[_,_]"))
    //    assertResult(__)(engine.eval("[_[id]{2},_[id]{-1}]"))
    assertResult(str.id.q(-1))(engine.eval("str[[id];[id]]{-1}"))
    assertResult(str.id.q(15))(engine.eval("str[str[id]{5},str[id]{10}]"))
    assertResult(str.id.q(15))(engine.eval("str[[id]{5},[id]{10}]"))
    assertResult(zeroObj)(engine.eval("[str,str{-1}]"))
    assertResult(str)(engine.eval("str[str[id]{-1}]{-1}"))
    assertResult(zeroObj)(engine.eval("[_{0};str]"))
    assertResult(zeroObj)(engine.eval("[str;_{0}]"))
    assertResult("obj{0}")(engine.eval("[str;_{0}]").toString)
    assertResult("obj{0}")(engine.eval("[str,str{-1}]").toString)
    assertResult("obj{0}")(engine.eval("['a','a'{-1}]").toString)
    assertResult(zeroObj)(engine.eval("['a','a'{-1}]"))
    assertResult(str.q(2).plus(str.q(2)).q(5))(engine.eval("str{2}[str[plus,str]{5}]"))
    //    assertResult(str.id.q(-1))(engine.eval("[str[id]{-1};str]"))
    //    assertResult(str.id.q(-1))(engine.eval("[str;str[id]{-1}]"))
    //    assertResult(str.id.q(-1))(engine.eval("[str;str]{-1}"))
    assertResult(str.q(2).id.q(7))(engine.eval("str{2}[str[id]{3},str[id]{4}]"))
    //assertResult(str.q(2).id.q(7))(engine.eval("_{2}[str[id]{3},str[id]{4}]"))
    //    assertResult(__)(engine.eval("[[_;_];_]"))
    //    assertResult(str)(engine.eval("[[str;str];str]"))
    //    assertResult(__.id.q(3))(engine.eval("[_,[_,_]]"))
    assertResult(str.id.q(7))(engine.eval("str[str[id]{3},str[id]{4}]"))
    assertResult(str.q(5).id.q(7))(engine.eval("str{5}[str[id]{3},str[id]{4}]"))
    assertResult(str.id.q(14))(engine.eval("str[id]{2}[str[id]{3},str[id]{4}]"))
    assertResult(str.id.q(5))(engine.eval("str[[str[id]{2}],[str[id]{3}]]"))
    assertResult(str.id.q(5))(engine.eval("str[[[str[id]{2}]],[[str[id]{3}]]]"))
    assertResult(str.id.q(5))(engine.eval("str[str[id]{2},str[id]{3}]"))
    //    assertResult(str)(engine.eval("[str;{1}]"))
    assertResult(str.id.q(3))(engine.eval("str[[id],[[id],[id]]]"))
    assertResult(str.id.q(3))(engine.eval("str[[[id],[id]],[id]]"))
    assertResult(str.id.q(9))(engine.eval("str[[id]{2},[[id]{3},[id]{4}]]"))
    assertResult(str.id.q(9))(engine.eval("str[[[id]{2},[id]{3}],[id]{4}]"))
    assertResult(zeroObj)(engine.eval("str[[[id]{2},[id]{-3}],[id]{1}]"))
    assertResult(str.id.q(3))(engine.eval("str[str,[str,str]]"))
    assertResult(str.id.q(3))(engine.eval("str[[str,str],str]"))
    assertResult(str.id.q(2))(engine.eval("str[str,str]"))
    assertResult(str.id.q(2))(engine.eval("str[str,str]"))
    assertResult(str.id.q(2))(engine.eval("str[[id],[id]]"))
    assertResult(str.id.q(3))(engine.eval("str[str,str[id]{2}]"))
    assertResult(str.id.q(3))(engine.eval("str[str[id]{2},str]"))
    //    assertResult(str.branch(str `;` str.id.q(2)))(engine.eval("str[str;[str,str]]"))
    //    assertResult(str.branch(str.id.q(2) `;` str.q(2)))(engine.eval("str[[str,str];str]"))
    //    assertResult(str.id.q(2))(engine.eval("str[str[str;str],str[str;str]]"))
    //    assertResult(__.id.q(2))(engine.eval("[_[_;_],_[_;_]]"))
    assertResult(str.id.q(2))(engine.eval("str[_[str;str],_[str;str]]"))
    //    assertResult(__.id.q(2))(engine.eval("[_,_]"))
    //    assertResult(str.id.q(2))(engine.eval("[str;[str,str]]"))
    engine.eval(":")
  }
}