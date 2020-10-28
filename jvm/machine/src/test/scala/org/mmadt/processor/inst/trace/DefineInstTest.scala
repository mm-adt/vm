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

package org.mmadt.processor.inst.trace

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.Obj.{intToInt, stringToStr, symbolToToken}
import org.mmadt.language.obj.`type`.__.{branch, symbolToRichToken, _}
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.trace.ModelOp.{MM, MMX, Model}
import org.mmadt.language.obj.{Int, Obj, asType}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil._
import org.mmadt.processor.inst.trace.DefineInstTest._
import org.mmadt.storage.StorageFactory._

object DefineInstTest {
  private val natType:Type[Int] = int.named("nat") <= int.is(int.gt(0))
  private val myListType:Type[__] = 'mylist <= __.-<(is(eqs(1)) `|`(1 `;` 'mylist)) >-
  private val iListType:Type[__] = 'ilist <= lst.branch(is(empty) `|` branch(is(head.a(int)) `;` is(tail.a('ilist))))
  private val siListType:Type[__] = 'silist <= lst.branch(is(empty) `|` branch(is(head.a(str)) `;` is(tail.head.a(int)) `;` is(tail.tail.a('silist))))
  private val apairType:Type[__] = 'apair <= (int.to('m) `;` int.to('n)).is(from('m, int).lt(from('n, int)))
  private val bpairType:Type[__] = 'bpair <= (int.plus(1).to('m) `;` int.plus(2).to('n)).is(from('m, int).lt(from('n, int)))
  //private val vecType:Type[__] = 'vec <= __.split(id `;` lst.combine(id `,`).merge.count)
  private val MODEL:Model = ModelOp.MM.defining(natType).defining(myListType).defining(iListType).defining(siListType) //.defining(vecType)
}
class DefineInstTest extends BaseInstTest(
  /*testSet("[define] test w/ apair", MODEL.defining(apairType).defining(bpairType),
    comment("apair"),
    testing(1 `;` 2, 'apair, 'apair(1 `;` 2), "(1;2)=>apair"),
    testing(100 `;` 101, 'apair, 'apair(100 `;` 101), "(100;101)=>apair"),
    excepting(2 `;` 1, 'apair, LanguageException.typingError(2 `;` 1, 'apair), "(2;1)=>apair"),
    excepting(1 `;` 2 `;` 3, 'apair, LanguageException.typingError(1 `;` 2 `;` 3, 'apair), "(1;2;3)=>apair"),
    comment("bpair"),
    testing(1 `;` 2, 'bpair <= (int `;` int), 'bpair(2 `;` 4), "(1;2)=>bpair<=(int;int)"),
  ), testSet("[define] table test w/ nat model", MODEL,
    comment("nat"),
    testing(2, a('nat), true),
    testing(-2, a('nat), false),
    testing(-2, int.a('nat.plus(100)), false),
    testing(2, as('nat).plus(0), 2.named("nat")),
    excepting(2, as('nat).plus(-10), LanguageException.typingError(-8.named("nat"), natType), "2[as,nat][plus,-10]"),
    excepting(2, as('nat).plus(-10).plus(10), LanguageException.typingError(-8.named("nat"), natType), "2[as,nat][plus,-10][plus,10]"),
    comment("mylist"),
    testing(1 `;`(1 `;` 1), lst.a('mylist), true, "(1;(1;1)) => lst[a,mylist]"),
    testing(1 `,`(1 `,` 2), a('mylist), false),
    testing(1 `,`(2 `,` 1), a('mylist), false),
    testing(1 `,`(1 `,` 2), a('mylist), false),
    excepting(1 `;`(1 `;` 1), as('mylist).put(0, 34), LanguageException.typingError('mylist(34 `;` 1 `;` 'mylist(lst())), myListType), "(1;(1;1))[as,mylist][put,0,34]"),
    comment("ilist"),
    testing(lst(), a('ilist), true, "()[a,ilist]"),
    testing(1 `;`, a('ilist), true, "(1)[a,ilist]"),
    testing(1 `;` 2 `;` 3, a('ilist), true, "(1;2;3)[a,ilist]"),
    testing(1 `;` "a" `;` 1, lst.a('ilist), false, "(1;'a';1) => lst[a,ilist]"),
    comment("silist"),
    testing(lst(), a('silist), true, "()[a,silist]"),
    testing("a" `;` 1, a('silist), true, "('a';1)[a,silist]"),
    testing("a" `;` 1 `;` "b" `;` 2, a('silist), true, "('a';1;'b';2)[a,silist]"),
    testing(1 `;` "a" `;` 1, lst.a('silist), false, "(1;'a';1) => lst[a,silist]"),
    testing("a" `;` 1 `;` 2, lst.a('silist), false, "('a';1;2) => lst[a,silist]"),
    comment("vec"),
    //    testing(lst(), a('vec), true, "()[a,vec]"),
    //    testing(lst(), as('vec), (lst() `;` 0).named("vec"), "()[as,vec]"),
    //    testing(1 `;` 2, as('vec), (((1 `;` 2) `;`) `;` 2).named("vec"), "(1;2)[as,vec]"),
  ), testSet("[define] table test w/ mm", List(MM, MMX),
    comment("midway-define]"),
    testing(2, define('x <= int.plus(1)), 2, "2[define,x<=int+1]"),
    testing(2, define('x <= int.plus(1)).plus('x), 5, "2[define,x<=int+1][plus,x]"),
    testing(int(2, 3, 4.q(2)), define('x <= int.plus(1)).plus('x), int(5, 7, 9.q(2)), "[2,3,4{2}][define,x<=int+1][plus,x]"),
    //testing(2, define('x <= int.plus(1)).branch('x `,`), 3, "2 => int[define,x<=int+1][x]"),
    //testing(2, define('x <= int.plus(1)).branch('x `,`), 3, "2 => int[define,x<=int+1][x<=x]"),
    // testing(int(-2,2.q(5)), int.q(6).define('y<=int.plus(-1000),'x <=int.plus(1).as('y)).branch('x`,`), int(-998,1002.q(5)), "[-2,2{5}] => int{6}[define,y<=int+-1000,x<=int+1[as,y]][x]"),
  ), testSet("[define] pair test", List(MM, MMX).map(m => m.defining('pair <= (str `;` str).to('x).:=(plus('x.get(1)) `;` plus('x.get(0))))),
    testing("a" `;` "b", 'pair <= (str `;` str), 'pair("ab" `;` "ba"), "('a';'b')=>pair<=(str;str)"),
    testing("a" `;` "b", 'pair, 'pair("ab" `;` "ba"), "('a';'b')=>pair"),
    testing("a" `;` "b", as('pair) `=>` 'pair, 'pair("abba" `;` "baab"), "('a';'b')=>pair=>pair"),
    testing("a" `;` "b", as('pair) `=>` as('pair) `=>` 'pair, 'pair("abbabaab" `;` "baababba"), "('a';'b')=>pair=>pair=>pair"),
    testing("ab" `;` "ba", 'pair <= (str `;` str), 'pair("abba" `;` "baab"), "('ab';'ba')=>pair<=(str;str)"),
    testing("ab" `;` "ba", 'pair, 'pair("abba" `;` "baab"), "('ab';'ba')=>pair"),
    testing("ab" `;` "ba", as('pair).get(0, str), "abba", "('ab';'ba')=>[as,pair][get,0,str]"),
    testing(strm(("a" `;` "b"), ("ab" `;` "ba")), 'pair <= (str `;` str).q(2), strm('pair("ab" `;` "ba"), 'pair("abba" `;` "baab")), "[('a';'b'),('ab';'ba')]=> pair<=(str;str){2}"),
    testing(strm(("a" `;` "b"), ("ab" `;` "ba")), 'pair.q(2), strm('pair("ab" `;` "ba"), 'pair("abba" `;` "baab")), "[('a';'b'),('ab';'ba')]=> pair{2}"),
    testing('pair("ab" `;` "ba"), 'pair <= (str `;` str), 'pair("abba" `;` "baab"), "pair:('ab';'ba')=>pair<=(str;str)"),
    testing('pair("ab" `;` "ba"), 'pair, 'pair("abba" `;` "baab"), "pair:('ab';'ba')=>pair"),
    IGNORING("eval-3", "eval-4", "eval-5")('pair("ab" `;` "ba"), (str `;` str) <=[Obj] 'pair, ("ab" `;` "ba"), "pair:('ab';'ba')=>(str;str)<=pair"),
    excepting("ab", 'pair <= (str `;` str), LanguageException.typingError("ab", asType(str `;` str)), "'ab' => pair<=(str;str)"),
    // excepting("ab", 'pair, LanguageException.typingError("ab", 'pair), "'ab' => pair"),
  )*/
)

/*test("vec documentation example") {
  engine.eval(
    """:[model,mm][define,vec:(lst,int)<=lst-<(_,=(_)>-[count]),
      |                single<=vec:(lst,is<4).0[tail][head],
      |                single<=vec:(lst,is>3).0[head],
      |                single<=int]""".stripMargin)
  assertResult('vec((1 `;` 2 `;` 3) `,` 3))(engine.eval("(1;2;3)[as,vec]"))
  assertResult('vec((1 `;` 2 `;` 3) `,` 3))(engine.eval("(1;2;3) => vec<=lst"))
  assertResult('vec((1 `;` 2 `;` 3) `,` 3))(engine.eval("(1;2;3) => vec"))
}*/

