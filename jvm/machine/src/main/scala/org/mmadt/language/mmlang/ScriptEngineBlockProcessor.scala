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
import java.util

import org.asciidoctor.ast.{ContentModel, StructuralNode}
import org.asciidoctor.extension.{BlockProcessor, Contexts, Name, Reader}
import org.asciidoctor.jruby.{AsciiDocDirectoryWalker, DirectoryWalker}
import org.asciidoctor.{Asciidoctor, OptionsBuilder, SafeMode}
import org.mmadt.VmException
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.obj.Obj
import org.mmadt.language.{LanguageFactory, Tokens}

import scala.collection.JavaConverters
import scala.util.{Failure, Success, Try}

@Name("exec")
@Contexts(Array(Contexts.LISTING))
@ContentModel(ContentModel.RAW)
class ScriptEngineBlockProcessor(astring: String, config: java.util.Map[String, Object]) extends BlockProcessor {
  lazy val engine: mmADTScriptEngine = LanguageFactory.getLanguage("mmlang").getEngine.get()
  val style = "source"
  val language = "mmlang"
  val prompt = "mmlang> "

  override def process(parent: StructuralNode, reader: Reader, attributes: java.util.Map[String, Object]): Object = {
    val builder: StringBuilder = new StringBuilder
    val eval = java.lang.Boolean.valueOf(attributes.getOrDefault("eval", "true").toString)
    JavaConverters.collectionAsScalaIterable(reader.readLines()).foreach(w => {
      if (eval) {
        builder.append(prompt).append(w).append("\n")
        Try[Obj] {
          engine.eval(w)
        } match {
          case Failure(exception) if exception.isInstanceOf[VmException] && java.lang.Boolean.valueOf(attributes.getOrDefault("exception", "false").toString) =>
            builder.append("language error: ").append(exception.getLocalizedMessage).append("\n")
          case Failure(exception) => throw new Exception(exception.getMessage + ":::" + builder, exception)
          case Success(value) =>
            val results = value.toStrm.values.toList
            if (results.isEmpty) builder.append(prompt).append("\n")
            else results.foreach(a => {
              builder.append(Tokens.RRDARROW).append(a).append("\n")
            })
        }
      } else
        builder.append(w).append("\n")
    })
    println(builder)
    val endAttributes: java.util.Map[String, Object] = new util.HashMap[String, Object]
    endAttributes.putAll(config)
    endAttributes.putAll(JavaConverters.mapAsJavaMap(Map("style" -> style, "language" -> language)))
    this.createBlock(parent, "listing", builder.toString().trim(), endAttributes)
  }
}

object ScriptEngineBlockProcessor {
  val source: String = "machine/src/asciidoctor/"
  val target: String = "machine/target/asciidoctor/"

  def main(args: Array[String]): Unit = {
    val asciidoctor = Asciidoctor.Factory.create()
    asciidoctor.readDocumentHeader(new File(source + "docinfo.html"))
    // RubyUtils.loadRubyClass(JRubyRuntimeContext.get(asciidoctor), new FileInputStream("/Library/Ruby/Gems/2.6.0/gems/asciidoctor-latex-1.5.0.17.dev/lib/asciidoctor-latex.rb"))
    asciidoctor.requireLibrary("asciidoctor-diagram")
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

  /*
compile; assembly; deployDocs
run ScriptEngineBlockProcessor.main()

#!/bin/bash

cd ..
git commit -a -m "documentation processed and generated"
git push
git checkout gh-pages
cd ..
cp jvm/machine/target/asciidoctor/index.html .
cd images
cp -rf ../jvm/machine/target/asciidoctor/images/ .
# rm -rf jvm/machine/target
git add ./**/*.png
cd ..
git commit -a -m "documentation deployed to gh-pages"
git push
git checkout master
cd jvm
 */
}
