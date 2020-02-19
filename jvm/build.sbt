import sbt.Keys.autoScalaLibrary

ThisBuild / organization := "org.mmadt"
ThisBuild / scalaVersion := "2.12.10"
ThisBuild / version := "0.1-SNAPSHOT"
Compile / compileOrder := CompileOrder.JavaThenScala

lazy val machine = (project in file("machine"))
  .settings(
    name := "machine",
    description := "mm-ADT :: JVM Machine",
    crossPaths := false,
    autoScalaLibrary := false,
    mainClass in assembly := Some("org.mmadt.language.console.Console"),
    assemblyJarName in assembly := "mmadt-vm.jar",
    libraryDependencies := List(
      "org.jline" % "jline" % "3.13.3",
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
      "org.scalatest" %% "scalatest" % "3.0.8" % "test"))
  .enablePlugins(AssemblyPlugin)

