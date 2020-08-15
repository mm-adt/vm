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

package org.mmadt.storage.mmkv

import java.util.concurrent.atomic.AtomicLong

import org.mmadt.language.obj.Obj.{ViaTuple, rootVia}
import org.mmadt.language.obj.Rec._
import org.mmadt.language.obj.`type`.RecType
import org.mmadt.language.obj.value.strm.RecStrm
import org.mmadt.language.obj.value.{RecValue, StrValue, Value}
import org.mmadt.language.obj.{Obj, Rec}
import org.mmadt.language.{LanguageException, LanguageFactory, LanguageProvider, Tokens}
import org.mmadt.storage.StorageFactory._

import scala.collection.mutable
import scala.io.{BufferedSource, Source}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmkvStore[K <: Obj, V <: Obj](val file: String) extends AutoCloseable {

  private lazy val mmlang: LanguageProvider = LanguageFactory.getLanguage("mmlang")
  private val K: StrValue = str("k")
  private val V: StrValue = str("v")

  val schema: RecType[StrValue, Obj] = {
    val source = Source.fromFile(file)
    try source.getLines().take(1).map(line => mmlang.parse[RecType[StrValue, Obj]](line)).next()
    finally source.close();
  }

  private val store: mutable.Map[K, V] = {
    val source: BufferedSource = Source.fromFile(file)
    try source.getLines().drop(1)
      .map(k => mmlang.parse[Rec[StrValue, Value[Obj]]](k).gmap.values)
      .foldLeft(new mutable.LinkedHashMap[K, V])((b, a) => b ++ Map(a.head.asInstanceOf[K] -> a.tail.head.asInstanceOf[V]))
    finally source.close()
  }

  private val counter: AtomicLong = new AtomicLong(0) //if (store.keys.isEmpty) 0L else store.keys.map(x => x.asInstanceOf[IntValue].g).max)

  def get(key: K): V = {
    val temp = store(key.hardQ(qOne))
    LanguageException.testTypeCheck(temp, asType(schema.gmap.fetch(V)))
    temp
  }
  def put(key: K, value: V): V = store.put(key, value).getOrElse(value)
  def put(value: V): V = store.put(int(counter.get()).asInstanceOf[K], value).getOrElse(value)
  def remove(key: K): V = store.remove(key).get
  def stream(via: ViaTuple = rootVia): RecStrm[StrValue, Value[Obj]] = vrec(values = store.iterator.map(x => {
    val kv = rec(name = schema.name, via = via, g = (Tokens.`,`, List(
      K -> x._1.asInstanceOf[Value[V]],
      V -> x._2.asInstanceOf[Value[V]]))).asInstanceOf[RecValue[StrValue, Value[Obj]]]
    LanguageException.testTypeCheck(kv, schema)
    kv
  }))
  def clear(): Unit = {
    counter.set(0L)
    store.clear()

  }
  def count(): Long = this.store.size

  override def close(): Unit = {
    /*val writer = new PrintWriter(new File(file))
    try {
      writer.println(schema.toString)
      store.foreach(x => writer.println(vrec(K -> x._1.asInstanceOf[Value[V]],V -> x._2.asInstanceOf[Value[V]])))
      writer.flush()
      store.clear()
      counter.set(0L)
      mmkvStore.dbs.remove(file).nonEmpty
    } finally writer.close()*/
  }
}

object mmkvStore extends AutoCloseable {
  private val dbs: mutable.Map[String, mmkvStore[Obj, Obj]] = new mutable.LinkedHashMap

  def open[K <: Obj, V <: Obj](file: String): mmkvStore[K, V] =
    if (file.equals(Tokens.empty)) dbs.last._2.asInstanceOf[mmkvStore[K, V]]
    else {
      val db = dbs.getOrElseUpdate(file, new mmkvStore(file))
      dbs.remove(file)
      dbs.put(file, db)
      db.asInstanceOf[mmkvStore[K, V]]
    }

  override def close(): Unit = {
    dbs.values.foreach(m => m.close())
    dbs.clear()
  }
}


