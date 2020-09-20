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

import javax.script.ScriptContext
import org.asciidoctor.ast.{ContentModel, StructuralNode}
import org.asciidoctor.extension.{BlockProcessor, Contexts, Name, Reader}
import org.asciidoctor.jruby.{AsciiDocDirectoryWalker, DirectoryWalker}
import org.asciidoctor.{Asciidoctor, OptionsBuilder, SafeMode}
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.trace.ModelOp.MM
import org.mmadt.language.{LanguageException, LanguageFactory, Tokens}

import scala.collection.JavaConverters
import scala.util.{Failure, Success, Try}

@Name("exec")
@Contexts(Array(Contexts.LISTING))
@ContentModel(ContentModel.RAW)
class ScriptEngineBlockProcessor(astring:String, config:java.util.Map[String, Object]) extends BlockProcessor {
  val STYLE = "source"
  val LANGUAGE = "mmlang"
  lazy val engine:mmADTScriptEngine = LanguageFactory.getLanguage(LANGUAGE).getEngine.get()
  //////////////////////////////////////
  val EXPECTED_RESULT = "#"

  override def process(parent:StructuralNode, reader:Reader, attributes:java.util.Map[String, Object]):Object = {
    val builder:StringBuilder = new StringBuilder
    val query:StringBuilder = new StringBuilder
    var result:Boolean = true
    val eval = java.lang.Boolean.valueOf(attributes.getOrDefault("eval", Tokens.btrue).toString)
    val prompt = attributes.getOrDefault("prompt", engine.getFactory.getLanguageName + "> ").toString
    val none = attributes.getOrDefault("none", prompt + "\n").toString
    val exception = attributes.getOrDefault("exception", Tokens.blank).toString
    val LINEBREAK = attributes.getOrDefault("linebreak", "%").toString
    engine.getContext.getBindings(ScriptContext.ENGINE_SCOPE).put(Tokens.::, __.model(MM))
    JavaConverters.collectionAsScalaIterable(reader.readLines()).foreach(w => {
      val line:Tuple2[String, String] =
        if (w.contains("##"))
          (w.split("##")(0) + "(//<[0-9]>)".r.findFirstIn(w).getOrElse(""), w.split("##")(1).replaceAll("(//<[0-9]>)", Tokens.blank).trim)
        else (w.stripTrailing(), "")
      if (line._1.trim.isBlank)
        builder.append("\n")
      else {
        if (line._1.contains(LINEBREAK)) {
          if (line._1.contains("%%")) result = false;
          query.append(line._1.replace(LINEBREAK, Tokens.space)).append("\n").append(IntStream.range(0, prompt.length).mapToObj(_ => Tokens.space).collect(Collectors.joining))
        } else {
          query.append(line._1)
          if (eval)
            builder.append(prompt).append(query).append("\n")
          Try[Obj] {
            engine.eval(query.toString().replaceAll("\n", Tokens.blank).replaceAll("(//<[0-9]>)", Tokens.blank))
          } match {
            case Failure(e) if e.getClass.getSimpleName.equals(line._2) =>
              if (eval) {
                (e match {
                  case _:LanguageException => builder.append("language error: ")
                  case _ => builder.append("error: ")
                }).append(e.getLocalizedMessage).append("\n")
              } else
                builder.append(query)
            case Failure(e) => throw new Exception(e.getMessage + ":::" + builder, e)
            case Success(value) =>

              if (eval) {
                val expectedResults = if (!line._2.isEmpty) engine.eval(line._2) else null
                if (null != expectedResults && value != expectedResults)
                  throw new Exception("Unexpected result: " + value + " [expected] " + expectedResults)
                val results = Obj.iterator(value)
                if (results.isEmpty || !result) builder.append(none)
                else results.foreach(a => {
                  builder.append(Tokens.RRDARROW).append(a).append("\n")
                })
              } else {
                builder.append(query)
              }
          }
          result = true
          query.clear()
        }
      }
    })
    engine.getContext.getBindings(ScriptContext.ENGINE_SCOPE).remove(Tokens.::)
    println(builder)
    val endAttributes:java.util.Map[String, Object] = new util.HashMap[String, Object]
    endAttributes.putAll(config)
    endAttributes.putAll(JavaConverters.mapAsJavaMap(Map("style" -> STYLE, "language" -> LANGUAGE)))
    this.createBlock(parent, "listing", builder.toString().trim(), endAttributes)
  }
}

object ScriptEngineBlockProcessor {
  val source:String = "machine/src/asciidoctor/"
  val target:String = "machine/target/asciidoctor/"

  def main(args:Array[String]):Unit = {
    val asciidoctor = Asciidoctor.Factory.create()
    asciidoctor.readDocumentHeader(new File(source + "docinfo.html"))
    // RubyUtils.loadRubyClass(JRubyRuntimeContext.get(asciidoctor), new FileInputStream("/Library/Ruby/Gems/2.6.0/gems/asciidoctor-latex-1.5.0.17.dev/lib/asciidoctor-latex.rb"))
    asciidoctor.requireLibrary("asciidoctor-diagram")
    val directoryWalker:DirectoryWalker = new AsciiDocDirectoryWalker(source);
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
