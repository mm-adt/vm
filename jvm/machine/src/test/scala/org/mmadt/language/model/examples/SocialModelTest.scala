/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.model.examples

import org.mmadt.language.model.{Algebra, Model}
import org.mmadt.language.obj.`type`.{IntType, RecType}
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Obj, Str}
import org.mmadt.processor.Processor
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class SocialModelTest extends FunSuite {

  val social  :Model     = Model.simple()
  val compiler:Processor = Processor.compiler(social)

  // define model types
  val nat   :IntType          = social.define("nat")(int <= int.is(int.gt(0)))
  val person:RecType[Str,Obj] = social.define("person")(trec(str("name") -> str,str("age") -> nat))
  val people:RecType[Str,Obj] = social.define("people")(person.q(*))
  social.put(Algebra.universal(person)) // TODO: make obj a type variable in a model
  social.put(Algebra.universal(str))
  social.put(Algebra.group(nat)("+"))
  social.put(Algebra.group(nat)("*"))
  //println(social)

  test("model types"){
    assertResult(int <= int.is(int.gt(0)))(social.get(nat).get)
    assertResult(trec(str("name") -> str,str("age") -> nat))(social.get(person).get)
    // assertResult(trec(str("name") -> str,str("age") -> nat).q(*))(social.get(people).get)
  }

  test("model values"){
    assertResult(nat(1))(nat(1))
    assertResult("person")(person(str("name") -> str("marko"),str("age") -> int(29)).name)
  }

  test("model compilations"){
    assertResult(social.get(nat).get)(compiler(nat.plus(nat.zero())))
    assertResult(social.get(nat).get)(compiler(nat.plus(nat.zero()).plus(nat.plus(nat.neg())).plus(nat.zero()).plus(nat.plus(nat.neg())).plus(nat.zero())))
    assertResult(rec.get(str("name"),str))(compiler(person.id().get("name",str)))
    println(compiler(person.id().get("name",str).plus(" rodriguez")))
    assertResult(rec.get(str("name"),str).plus(" rodriguez"))(compiler(person.id().get("name",str).plus(" rodriguez")))
    // assertResult(rec.get(str("name"),str).plus(" rodriguez"))(compiler(person.id().get("firstname",str).id().plus(" rodriguez")))
    // assertResult(int <= rec.get(str("age"),int).is(int.gt(0)))(compiler(person.id().get(str("age"),nat)))
  }

  test("rec stream w/ rewrites"){
    val ppl = vrec(
      rec(str("name") -> str("marko")),
      rec(str("name") -> str("kuppitz")),
      rec(str("name") -> str("ryan")),
      rec(str("name") -> str("stephen")))
    println(ppl)
  }

}
