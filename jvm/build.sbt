import com.mdsol.sbt.AsciiDoctorPlugin.autoImport.asciiDocDirectory
import sbt.Keys.{autoScalaLibrary, _}
import sbt._
import sbtassembly.AssemblyPlugin.defaultShellScript

ThisBuild / organization := "org.mmadt"
ThisBuild / scalaVersion := "2.12.10"
ThisBuild / version := "0.1-SNAPSHOT"
Compile / compileOrder := CompileOrder.JavaThenScala
/*makeSite := {
  (makeSite in machine).value
}*/
val copyData = taskKey[Unit]("Copy .mm files to data directory")
copyData := {
  import Path._
  List("mmkv", "load").foreach(dir => {
    val src = (machine / Compile / resourceDirectory in Test).value / dir
    val pairs = (src ** "*.mm").get() pair rebase(src, baseDirectory.value / "data")
    IO.copy(pairs, CopyOptions.apply(overwrite = true, preserveLastModified = true, preserveExecutable = false))
  })
}
(compile in Compile) := ((compile in Compile) dependsOn copyData).value

lazy val machine = (project in file("machine"))
  .settings(
    name := "machine",
    description := "mm-ADT :: JVM Machine",
    crossPaths := false,
    autoScalaLibrary := false,
    mainClass in assembly := Some("org.mmadt.language.console.Console"),
    assemblyJarName in assembly := s"mmadt-vm-${version.value}.jar",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultShellScript)),
    assemblyMergeStrategy in assembly ~= (old => {
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case _ => MergeStrategy.first
    }),
    libraryDependencies := List(
      "org.jline" % "jline" % "3.13.3",
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
      "org.asciidoctor" % "asciidoctorj" % "2.1.0",
      // tests
      "org.scalatest" %% "scalatest" % "3.0.8" % "test"),
    //git.remoteRepo := scmInfo.value.get.connection.replace("scm:git:", ""),
    //scmInfo := Some(ScmInfo(url("https://github.com/mm-adt/vm"), "scm:git:git@github.com:mm-adt/vm.git")),
    // excludeFilter in ghpagesCleanSite := ((_: File) => true),
    //asciiDocExtensions := List(ExtensionConfiguration("org.mmadt.language.mmlang.SourceBlockProcessor", None)),
    asciiDocDirectory := baseDirectory.value / "src" / "asciidoctor",
    asciiDocOutputDirectory := target.value / "docs" / "asciidoctor",
    asciiDocBackend := "html",
    asciiDocType := Some("book"),
    asciiDocImagesDir := Some((baseDirectory.value / "src" / "asciidoctor" / "images").toString),
  )
  .enablePlugins(AssemblyPlugin)
  //.enablePlugins(AsciidoctorPlugin)
  //.enablePlugins(SitePreviewPlugin)
  //.enablePlugins(GhpagesPlugin)
  .enablePlugins(AsciiDoctorPlugin)






