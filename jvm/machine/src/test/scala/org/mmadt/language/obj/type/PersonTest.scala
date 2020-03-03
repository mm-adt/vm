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

package org.mmadt.language.obj.`type`

import org.mmadt.language.model.Model
import org.mmadt.language.obj.value.{IntValue, ObjValue, RecValue, StrValue}
import org.mmadt.language.obj.{Obj, Str}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

import scala.collection.immutable.ListMap

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class PersonTest extends FunSuite {

  test("person rec"){
    val marko:RecValue[StrValue,ObjValue] = vrec(Map(str("name") -> str("marko"),str("age") -> int(29)))
    assertResult(ListMap(str("name") -> str("marko"),str("age") -> int(29)))(marko.value())
    assertResult("['name'->'marko','age'->29]")(marko.toString)
    assertResult("person:['name'->'marko','age'->29]")(marko.as("person").toString)
    ///
    assertResult("rec")(marko.name)
    assertResult("person")(marko.as[Str]("person").name)
    assertResult(str("marko"))(marko ==> rec.get(str("name"),str))
    assertResult(int(29))(marko ==> rec.get(str("age"),int))
    assertResult(str("marko"))(marko.as[Obj]("person") ==> rec.get(str("name"),str))
    assertResult(int(29))(marko.as[Obj]("person") ==> rec.get(str("age"),int))
  }

  /*test("person compilation") {
    val marko = new TRec[Str, Obj](Map(str("name") -> str("marko"), str("age") -> int(29)), Nil, qOne).as("person")
    val model = new SimpleModel().
      put(int, int.mult(2), int.plus(int)).
      put(int, int.plus(0), int).
      typePut("person", rec[Str, Str].get[StrType]("firstname", str), rec[Str, Str].get[StrType]("name", str))
    val processor = new CompilingProcessor[Obj, Obj](model)
    println(model)
    println(processor.apply(marko, rec[Str, Str].get[StrType]("firstname", str)).toList)
  }*/

  test("person evaluation"){
    //val marko:Rec[Obj,Obj] = rec(str("name") -> str("marko"),str("age") -> int(29)).as[Rec[_,_]]("person")
    val model = Model.simple().
      put(int.mult(2),int.plus(int)).
      put(int.plus(0),int).
      put(rec.get(str("firstname"),str),rec.get(str("name"),str))
    println(model)
    println(model.get(rec.get(str("firstname"),str)))
    // println(IteratorChainProcessor(marko, rec.get(str("firstname"), str)).toList)
  }


}

