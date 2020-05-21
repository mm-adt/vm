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

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{RecValue, Value}
import org.mmadt.storage.StorageFactory._

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object TypeChecker {
  def matchesVT[O <: Obj](obj: Value[O], pattern: Type[O]): Boolean = {
    if (!obj.alive && !pattern.alive) return true
    (pattern.name.equals(Tokens.obj) || (pattern.name.equals(Tokens.anon) && (pattern.root || obj.compute(pattern).alive)) || // all objects are obj
      (!obj.name.equals(Tokens.rec) && !obj.name.equals(Tokens.lst) &&
        (obj.name.equals(pattern.name) || pattern.domain.name.equals(obj.name)) && ((pattern.q == qZero && obj.q == qZero) || obj.compute(pattern).alive)) || // nominal type checking (prevent infinite recursion on recursive types) w/ structural on atomics
      obj.isInstanceOf[Strm[Obj]] || // TODO: testing a stream requires accessing its values (we need strm type descriptors associated with the strm -- or strms are only checked nominally)
      ((obj.isInstanceOf[Lst[_]] && pattern.isInstanceOf[Lst[_]] &&
        testList(obj.asInstanceOf[Lst[Obj]], pattern.asInstanceOf[Lst[Obj]]) && obj.compute(pattern).alive) || // structural type checking on records
        (obj.isInstanceOf[RecValue[_, _]] && pattern.isInstanceOf[RecType[_, _]] &&
          testRecord(obj.asInstanceOf[Rec[Obj, Obj]].gmap, pattern.asInstanceOf[ORecType].gmap) && obj.compute(pattern).alive))) && // structural type checking on records
      withinQ(obj, pattern) // must be within the type's quantified window
  }

  def matchesVV[O <: Obj](obj: Value[O], pattern: Value[O]): Boolean = (!obj.alive && !pattern.alive) || (obj.g.equals(pattern.g) && withinQ(obj, pattern))

  def matchesTT[O <: Obj](obj: Type[O], pattern: Type[O]): Boolean = {
    if (!obj.alive && !pattern.alive) return true
    ((obj.name.equals(Tokens.obj) || pattern.name.equals(Tokens.obj) || obj.name.equals(Tokens.anon) || (pattern.name.equals(Tokens.anon) && pattern.root)) || // all objects are obj
      (!obj.name.equals(Tokens.rec) && !obj.name.equals(Tokens.lst) && obj.name.equals(pattern.name)) ||
      (obj match {
        case recType: ORecType if pattern.isInstanceOf[RecType[_, _]] => testRecord(recType.gmap, pattern.asInstanceOf[ORecType].gmap)
        case lstType: Lst[Obj] => testList(lstType, pattern.asInstanceOf[Lst[Obj]])
        case _ => false
      })) &&
      obj.trace
        .map(_._2)
        .zip(pattern.trace.map(_._2))
        .map(insts => insts._1.op.equals(insts._2.op) &&
          insts._1.args.zip(insts._2.args).
            map(a => a._1.test(a._2)).
            fold(insts._1.args.length == insts._2.args.length)(_ && _))
        .fold(obj.trace.length == pattern.trace.length)(_ && _) &&
      withinQ(obj, pattern)
  }

  def matchesTV[O <: Obj](obj: Type[O], pattern: Value[O]): Boolean = !obj.alive && !pattern.alive

  ////////////////////////////////////////////////////////

  private def testRecord(leftMap: collection.Map[Obj, Obj], rightMap: collection.Map[Obj, Obj]): Boolean = {
    if (leftMap.equals(rightMap)) return true

    val typeMap: mutable.Map[Obj, Obj] = mutable.Map() ++ rightMap

    leftMap.map(a => typeMap.find(k =>
      a._1.test(k._1) && a._2.test(k._2)).map(z => typeMap.remove(z._1))).toList

    typeMap.isEmpty || !typeMap.values.exists(x => x.q._1.g != 0)
  }

  private def testList(leftList: Lst[Obj], rightList: Lst[Obj]): Boolean = {
    if (rightList.glist.isEmpty || leftList.glist.equals(rightList.glist)) return true
    leftList.glist.zip(rightList.glist).foldRight(true)((a, b) => a._1.test(a._2) && b)
  }
}
