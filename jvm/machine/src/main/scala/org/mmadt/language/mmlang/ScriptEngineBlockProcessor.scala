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

package org.mmadt.language.mmlang
import java.io.File

import org.asciidoctor.ast.{ContentModel, StructuralNode}
import org.asciidoctor.extension.{BlockProcessor, Contexts, Name, Reader}
import org.asciidoctor.jruby.{AsciiDocDirectoryWalker, DirectoryWalker}
import org.asciidoctor.{Asciidoctor, OptionsBuilder, SafeMode}
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.{LanguageFactory, Tokens}

import scala.collection.JavaConverters

@Name("exec")
@Contexts(Array(Contexts.LISTING))
@ContentModel(ContentModel.RAW)
class ScriptEngineBlockProcessor(astring: String, config: java.util.Map[String, Object]) extends BlockProcessor {
  lazy val engine: mmADTScriptEngine = LanguageFactory.getLanguage("mmlang").getEngine.get()
  val style = "source"
  val language = "python"
  override def process(parent: StructuralNode, reader: Reader, attributes: java.util.Map[String, Object]): Object = {
    val builder: StringBuilder = new StringBuilder
    JavaConverters.collectionAsScalaIterable(reader.readLines()).foreach(w => {
      builder.append("mmlang> ").append(w).append("\n")
      engine.eval(w).toStrm.values.foreach(a => {
        builder.append(Tokens.RRDARROW).append(a).append("\n")
      })
    })
    println(builder)
    this.createBlock(parent, "listing", builder.toString(), JavaConverters.mapAsJavaMap(Map[String, Object]("style" -> style, "language" -> language)))
  }
}
object ScriptEngineBlockProcessor {
  val source: String = "machine/src/asciidoctor/"
  val target: String = "machine/target/asciidoctor/"
  def main(args: Array[String]): Unit = {
    val asciidoctor = Asciidoctor.Factory.create()
    val directoryWalker: DirectoryWalker = new AsciiDocDirectoryWalker(source);
    val asciidocFiles = directoryWalker.scan();
    JavaConverters.collectionAsScalaIterable[File](asciidocFiles).map(z => {
      println("Current file: " + z)
      z
    }).filter(z => Set("index.adoc").contains(z.getName)).foreach(z => {
      println("Processing file: " + z)
      asciidoctor.javaExtensionRegistry.block(classOf[ScriptEngineBlockProcessor])
      asciidoctor.convertFile(z, OptionsBuilder.options().toDir(new File(target)).safe(SafeMode.UNSAFE).mkDirs(true).toFile(true))
    })
  }
  /**
   * git checkout gh-pages
   * git 
   */
}
