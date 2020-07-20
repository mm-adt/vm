import com.typesafe.sbt.site.asciidoctor.AsciidoctorPlugin
import sbt.Keys.{autoScalaLibrary, _}
import sbt._
import sbtassembly.AssemblyPlugin.defaultShellScript

ThisBuild / organization := "org.mmadt"
ThisBuild / scalaVersion := "2.12.10"
ThisBuild / version := "0.1-SNAPSHOT"
Compile / compileOrder := CompileOrder.JavaThenScala


val copyData = taskKey[Unit]("Copy .mm files to data directory")
copyData := {
  import Path._
  println("Copying resources to data.")
  List("mmkv", "load").foreach(dir => {
    val src = (machine / Compile / resourceDirectory in Test).value / dir
    val pairs = (src ** "*.mm").get() pair rebase(src, baseDirectory.value / "data")
    IO.copy(pairs, CopyOptions.apply(overwrite = true, preserveLastModified = true, preserveExecutable = false))
  })
  List("model").foreach(dir => {
    val src = (machine / Compile / resourceDirectory in Compile).value / dir
    val pairs = (src ** "*.mm").get() pair rebase(src, baseDirectory.value / "data" / "model")
    IO.copy(pairs, CopyOptions.apply(overwrite = true, preserveLastModified = true, preserveExecutable = false))
  })

}
val deployDocs = taskKey[Unit]("Deploy documentation to GitHub pages")
deployDocs := {
  val images = List((machine / Compile / baseDirectory).value / "src" / "asciidoctor" / "images",
    (machine / Compile / target).value / "asciidoctor" / "images")
  println("Copying documentation images to target: " + images.head + " => " + images.tail.head)
  IO.copyDirectory(
    images.head,
    images.tail.head,
    CopyOptions.apply(overwrite = true, preserveLastModified = true, preserveExecutable = false))
  // set the main class for the main 'sbt run' task
  // (runMain in Compile).toTask(" org.mmadt.language.mmlang.StorageEngineBlockProcessor").value
}

(compile in Compile) := ((compile in Compile) dependsOn copyData).value
makeSite := {
  (makeSite in machine).value
}

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
    // base:= font_size_min: $base_font_size * 0.75
    libraryDependencies := List(
      "org.jline" % "jline" % "3.13.3",
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
      "org.asciidoctor" % "asciidoctorj-diagram" % "2.0.2",
      "org.asciidoctor" % "asciidoctorj" % "2.4.0",
      // tests
      "org.scalatest" %% "scalatest" % "3.0.8" % "test"),
    git.remoteRepo := scmInfo.value.get.connection.replace("scm:git:", ""),
    scmInfo := Some(ScmInfo(url("https://github.com/mm-adt/vm"), "scm:git:git@github.com:mm-adt/vm.git")),
    excludeFilter in ghpagesCleanSite := ((f: File) => f.getName.contains("index")),
  )
  .enablePlugins(AssemblyPlugin)
  .enablePlugins(AsciidoctorPlugin)
  .enablePlugins(SitePreviewPlugin)
  .enablePlugins(GhpagesPlugin)


