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

package org.mmadt.storage.mmkv

import java.io.{File, PrintWriter}
import java.util.concurrent.atomic.AtomicLong

import org.mmadt.language.mmlang.mmlangParser
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.RecType
import org.mmadt.language.obj.value.strm.RecStrm
import org.mmadt.language.obj.value.{IntValue, RecValue, StrValue, Value}
import org.mmadt.storage.StorageFactory._

import scala.collection.mutable
import scala.io.{BufferedSource, Source}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmkvStore[K <: Obj,V <: Obj](file:String) extends AutoCloseable {

  private val MMKV:String = "mmkv"
  private val K   :String = "k"
  private val V   :String = "v"

  val schema:RecType[StrValue,Obj] = {
    val source = Source.fromFile(file)
    try trec[StrValue,Obj](name = MMKV,value = source.getLines().take(1)
      .map(line => mmlangParser.parseAll(mmlangParser.recType,line).get)
      .next().value().asInstanceOf[Map[StrValue,Obj]])
    finally source.close();
  }

  val store:mutable.Map[Value[K],Value[V]] = {
    val source:BufferedSource = Source.fromFile(file)
    try source.getLines().drop(1)
      .map(k => mmlangParser.parse(k).asInstanceOf[RecValue[StrValue,Value[Obj]]].value.values)
      .foldLeft(new mutable.LinkedHashMap[Value[K],Value[V]]())((b,a) => b ++ Map(a.head.asInstanceOf[Value[K]] -> a.tail.head.asInstanceOf[Value[V]]))
    finally source.close()
  }

  val counter:AtomicLong = new AtomicLong(store.keys.map(x => x.asInstanceOf[IntValue].value).max)

  def get(key:Value[K]):Value[V] = store(key)
  def put(key:Value[K],value:Value[V]):Value[V] = store.put(key,value).getOrElse(value)
  def put(value:Value[V]):Value[V] = store.put(int(counter.get()).asInstanceOf[Value[K]],value).getOrElse(value)
  def remove(key:Value[K]):Value[V] = store.remove(key).get
  def strm():RecStrm[StrValue,Value[Obj]] = vrec(value = store.iterator.map(x => vrec(str(K) -> x._1,str(V) -> x._2)))

  override def close():Unit ={
    val writer = new PrintWriter(new File(file))
    try {
      writer.println(schema.toString)
      store.foreach(x => writer.println(vrec(str(K) -> x._1,str(V) -> x._2)))
    } finally writer.close()
  }
}


