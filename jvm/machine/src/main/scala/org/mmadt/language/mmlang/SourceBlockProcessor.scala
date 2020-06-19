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

import org.asciidoctor.{Asciidoctor, OptionsBuilder, SafeMode}
import org.asciidoctor.ast.{Document, StructuralNode}
import org.asciidoctor.extension.Treeprocessor
import org.asciidoctor.jruby.{AsciiDocDirectoryWalker, DirectoryWalker}
import org.mmadt.language.LanguageFactory
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.storage.StorageFactory._

import scala.util.Try

class SourceBlockProcessor(config: java.util.Map[String, Object]) extends Treeprocessor(config) {
  lazy val engine: mmADTScriptEngine = LanguageFactory.getLanguage("mmlang").getEngine.get()
  override def process(document: Document): Document = {
    touch(document);
    document;
  }
  def touch(block: StructuralNode): Unit = {
    if (block.getBlocks != null) {
      val blocks = block.getBlocks
      for (i <- 0 until blocks.size()) {
        val z = blocks.get(i)
        if (z.getStyle == "source" && !z.getAttributes.keySet().contains("language")) {
          println(z.getAttributes())
          Try[Unit] {
            val builder: StringBuilder = new StringBuilder
            z.getContent.toString.split("\n").foreach(w => {
              builder.append("mmlang> ").append(w).append("\n")
              engine.eval(w).toStrm.values.foreach(a => {
                builder.append("==>").append(a).append("\n")
              })
            })
            blocks.set(i, createBlock(z, z.getContext, builder.toString()))
          }.getOrElse(int(1))
        }
        touch(z)
      }
    }
  }
}
object SourceBlockProcessor {

  def main(args: Array[String]): Unit = {
    val asciidoctor = Asciidoctor.Factory.create()
    val directoryWalker: DirectoryWalker = new AsciiDocDirectoryWalker("/Users/marko/software/mmadt/vm/jvm/machine/src/asciidoctor/");
    val asciidocFiles = directoryWalker.scan();
    asciidocFiles.stream().filter(z => z.getName.contains("introduction")).forEach(x => {
      println(x)
      asciidoctor.javaExtensionRegistry.treeprocessor(classOf[SourceBlockProcessor])
      asciidoctor.convertFile(x, OptionsBuilder.options().toDir(new File("/Users/marko/software/mmadt/vm/jvm/machine/target/asciidoctor")).safe(SafeMode.UNSAFE).toFile(true))
      asciidoctor.convertFile(x, OptionsBuilder.options().toDir(new File("/Users/marko/software/mmadt/vm/jvm/machine/target/site")).safe(SafeMode.UNSAFE).toFile(true))
    })
  }
}
