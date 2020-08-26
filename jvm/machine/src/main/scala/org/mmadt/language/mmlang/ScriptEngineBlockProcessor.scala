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
import java.util.stream.{Collectors, IntStream}

import org.asciidoctor.ast.{ContentModel, StructuralNode}
import org.asciidoctor.extension.{BlockProcessor, Contexts, Name, Reader}
import org.asciidoctor.jruby.{AsciiDocDirectoryWalker, DirectoryWalker}
import org.asciidoctor.{Asciidoctor, OptionsBuilder, SafeMode}
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.obj.Obj
import org.mmadt.language.{LanguageException, LanguageFactory, Tokens}

import scala.collection.JavaConverters
import scala.util.{Failure, Success, Try}

@Name("exec")
@Contexts(Array(Contexts.LISTING))
@ContentModel(ContentModel.RAW)
class ScriptEngineBlockProcessor(astring: String, config: java.util.Map[String, Object]) extends BlockProcessor {
  val STYLE = "source"
  val LANGUAGE = "mmlang"
  lazy val engine: mmADTScriptEngine = LanguageFactory.getLanguage(LANGUAGE).getEngine.get()
  //////////////////////////////////////
  val PROMPT = "prompt" // String
  val EVAL = "eval" // Boolean
  val NONE = "none" // String
  val EXCEPTION = "exception" // String
  val LINE_BREAK = "linebreak" // String

  override def process(parent: StructuralNode, reader: Reader, attributes: java.util.Map[String, Object]): Object = {
    val builder: StringBuilder = new StringBuilder
    val query: StringBuilder = new StringBuilder
    val eval = java.lang.Boolean.valueOf(attributes.getOrDefault(EVAL, Tokens.btrue).toString)
    val prompt = attributes.getOrDefault(PROMPT, engine.getFactory.getLanguageName + "> ").toString
    val none = attributes.getOrDefault(NONE, prompt + "\n").toString
    val exception = attributes.getOrDefault(EXCEPTION, Tokens.blank).toString
    val linebreak = attributes.getOrDefault(LINE_BREAK, "%").toString

    JavaConverters.collectionAsScalaIterable(reader.readLines()).foreach(w => {
      if (w.trim.isBlank)
        builder.append("\n")
      else {
        if (w.stripTrailing().endsWith(linebreak)) {
          val line = w.substring(0, w.stripTrailing().length - (linebreak.length + 1))
          query.append(line).append("\n").append(IntStream.range(0, prompt.length).mapToObj(_ => Tokens.space).collect(Collectors.joining))
        } else {
          query.append(w)
          if (eval)
            builder.append(prompt).append(query).append("\n")
          Try[Obj] {
            engine.eval(query.toString().replaceAll("\n", Tokens.blank).replace(linebreak, Tokens.blank))
          } match {
            case Failure(e) if e.getClass.getSimpleName.equals(exception) =>
              if (eval) {
                (e match {
                  case _: LanguageException => builder.append("language error: ")
                  case _ => builder.append("error: ")
                }).append(e.getLocalizedMessage).append("\n")
              } else
                builder.append(query)
            case Failure(e) => throw new Exception(e.getMessage + ":::" + builder, e)
            case Success(value) =>
              if (eval) {
                val results = value.toStrm.values.toList
                if (results.isEmpty) builder.append(none)
                else results.foreach(a => {
                  builder.append(Tokens.RRDARROW).append(a).append("\n")
                })
              } else {
                builder.append(query)
              }
          }
          query.clear()
        }
      }
    })
    engine.eval(":")
    println(builder)
    val endAttributes: java.util.Map[String, Object] = new util.HashMap[String, Object]
    endAttributes.putAll(config)
    endAttributes.putAll(JavaConverters.mapAsJavaMap(Map("style" -> STYLE, "language" -> LANGUAGE)))
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
